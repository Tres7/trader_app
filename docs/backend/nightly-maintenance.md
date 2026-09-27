# Maintenance nightly (`.github/workflows/nightly.yml`)

## Objectif du document
- Expliquer à quoi sert le workflow `Nightly` et ce que fait chacun de ses jobs.
- Documenter le schéma récurrent d'échec de `Trivy (production image)` et sa résolution.
- Documenter le schéma d'échec de `npm audit` et sa résolution.
- Éviter de re-découvrir ce diagnostic à chaque nouvel échec.

## Périmètre
Ce document couvre les 3 jobs du workflow `Nightly` (`.github/workflows/nightly.yml`) :
- `npm audit` (dépendances client) ;
- `Trivy (production image)` (image serveur déployée) ;
- `Nightly DB backup` (sauvegarde base de données — détails complets dans `deploy/README.md`, section "Backups").

---

# 1. Vue d'ensemble

## Rôle du workflow
`Nightly` tourne chaque nuit (`cron: '30 3 * * *'`) et sert de filet de sécurité continu :
- il détecte les nouvelles CVE publiées depuis le dernier build/install, même sans aucun changement de code côté projet ;
- il garantit que la base est sauvegardée indépendamment de la fréquence des déploiements.

## Pourquoi il peut échouer sans qu'on ait rien changé
Les deux jobs de sécurité (`npm audit`, `Trivy`) scannent un état figé (le `package-lock.json` du client, l'image Docker actuellement déployée). De nouvelles vulnérabilités sont publiées en continu par l'écosystème (Alpine, npm, Maven...) — un run qui passait hier peut donc échouer aujourd'hui sans qu'aucun commit ne soit en cause.

---

# 2. Job `npm audit`

## Ce qu'il fait
- `npm ci` dans `client/` ;
- exécute `.github/scripts/check-npm-audit.js`, qui lance `npm audit --json` et échoue (`exit 1`) sur toute vulnérabilité `HIGH`/`CRITICAL` **non exclue**.

## Liste d'exclusions
Le script a une liste en dur de GHSA IDs volontairement ignorés (pas de fix disponible en amont) :
```js
const EXCLUDED = new Set([
  'GHSA-mh99-v99m-4gvg',
  'GHSA-rgw5-rvv9-x895',
  'GHSA-w3rx-r6r6-pgpr',
  'GHSA-5p2g-fcmc-qvqq',
]);
```

## Cause la plus fréquente
Une dépendance **transitive** (pas déclarée directement dans `client/package.json`) devient vulnérable — `browserslist`, `@xmldom/xmldom`, `js-yaml` en sont des exemples déjà rencontrés.

## Comment le résoudre
1. Identifier le paquet et la version corrigée via `npm audit --json` (dans `client/`).
2. Ajouter une entrée dans `"overrides"` de `client/package.json` (déjà utilisé pour `postcss`, `browserslist`, `@xmldom/xmldom`, `js-yaml`) pour forcer la version patchée, sans attendre que le parent direct (`expo`, `eslint`...) bump lui-même sa dépendance.
3. Si deux versions majeures différentes du même paquet coexistent dans l'arbre (ex. `js-yaml` en v3 et v4), scoper l'override par paquet parent plutôt que d'imposer une version unique partout — un saut de version majeure peut casser un paquet qui dépend spécifiquement de l'ancienne API :
   ```json
   "overrides": {
     "js-yaml": "^4.3.2",
     "@istanbuljs/load-nyc-config": {
       "js-yaml": "^3.15.2"
     }
   }
   ```
4. `npm install` (pas `npm ci`, pour régénérer le lockfile), puis vérifier avec `node .github/scripts/check-npm-audit.js` que ça passe.
5. Si un fix n'existe nulle part en amont, ajouter le GHSA ID à `EXCLUDED` plutôt que de bloquer indéfiniment le Nightly.

---

# 3. Job `Trivy (production image)`

## Ce qu'il fait
- trouve le dernier manifest publié (`deploy/manifests/manifest-*.yaml`, le plus haut numéro de version) ;
- en extrait la référence d'image (`ghcr.io/.../server@sha256:...`) ;
- scanne cette image avec Trivy (`severity: CRITICAL,HIGH`, `ignore-unfixed: true`) et échoue si une CVE fixable est trouvée.

## Cause la plus fréquente : l'image de base Alpine a vieilli
Le `server/Dockerfile` fait `RUN apk upgrade --no-cache && ...` dans le stage runtime — ça met à jour les paquets Alpine **au moment du build**, pas après. Une fois l'image buildée et déployée, elle ne se patch plus jamais toute seule.

Le `Build & Publish` (`.github/workflows/build-publish.yml`) qui rebuild et republie un nouveau manifest **ne se déclenche que si `server/**` a changé** depuis le dernier manifest publié. Résultat : si personne ne touche à `server/` pendant plusieurs jours/semaines, l'image reste figée pendant que de nouvelles CVE Alpine sont publiées (déjà vu deux fois : `openssl`/`libssl3`/`libcrypto3` fin août, `libexpat` fin septembre — même diagnostic, même remède).

### Comment le résoudre
1. Confirmer que c'est bien ce cas de figure : la CVE porte sur un paquet Alpine (`libssl3`, `libcrypto3`, `libexpat`, etc.), pas sur une dépendance Java du jar.
2. Faire un commit qui touche un fichier sous `server/**` pour re-déclencher `Build & Publish` — le contenu exact importe peu (un commentaire à jour dans le Dockerfile, ou même un espace) tant qu'il y a un vrai diff détecté par `git diff --name-only` dans le job `changes` de `build-publish.yml`.
3. Une fois mergé sur `main` : `Build & Publish` rebuild l'image (l'`apk upgrade --no-cache` récupère les paquets patchés), la pousse sur `ghcr.io`, et le bot CD publie automatiquement un nouveau manifest.
4. Le Nightly suivant scanne ce nouveau manifest et devrait passer.

## Cause secondaire : CVE dans une dépendance Java transitive (pas Alpine)
Certaines CVE portent sur `app.jar` lui-même (visible dans le rapport Trivy comme cible séparée de l'image Alpine) — ex. `io.netty:netty-handler`, `org.apache.tomcat.embed:tomcat-embed-core`. Ce sont des dépendances **gérées transitivement par le BOM Spring Boot**, pas déclarées directement dans `server/build.gradle` — dependabot ne les détecte donc pas et ne propose jamais de PR pour les corriger seul.

### Comment le résoudre
Épingler la version corrigée via `resolutionStrategy.eachDependency` dans `server/build.gradle` (pattern déjà utilisé pour `io.netty` et `org.apache.tomcat.embed`) :
```gradle
configurations.all {
	resolutionStrategy.eachDependency { details ->
		if (details.requested.group == 'io.netty') {
			details.useVersion '4.2.17.Final'
		}
		if (details.requested.group == 'org.apache.tomcat.embed') {
			details.useVersion '11.0.25'
		}
	}
}
```
Ce même commit sert aussi de déclencheur pour `Build & Publish` (voir section précédente) — les deux problèmes se corrigent souvent dans le même geste.

## Limite connue
Il reste des CVE `CRITICAL` sur Tomcat qui n'ont **aucun fix disponible** à la version actuelle de Spring Boot (`4.1.1`) — seul un futur patch Spring Boot qui bump Tomcat en interne les résoudra. Pas d'action possible tant que ce patch n'est pas sorti, au-delà du pin déjà en place pour les CVE déjà connues et fixables.

---

# 4. Job `Nightly DB backup`

## Ce qu'il fait
Lance `backup-db.sh` sur le VPS via SSH, indépendamment des déploiements.

## Documentation complète
Voir `deploy/README.md`, section "Backups" — ce job n'est pas détaillé davantage ici pour éviter la duplication.

---

# 5. Résumé

## Points clés à retenir
- Le Nightly peut échouer sans aucun changement de code — c'est le fonctionnement normal d'un scan de sécurité continu, pas un signe de régression.
- `npm audit` qui échoue → ajouter/ajuster un override ciblé dans `client/package.json`.
- `Trivy` qui échoue sur un paquet Alpine → il faut un rebuild ; touche n'importe quel fichier sous `server/**` pour le déclencher.
- `Trivy` qui échoue sur une dépendance Java transitive (netty, tomcat...) → pin via `resolutionStrategy.eachDependency` dans `server/build.gradle`.
- Certaines CVE (Tomcat via Spring Boot) n'ont pas de fix disponible pour l'instant — ce n'est pas un oubli, c'est une limite amont documentée.
