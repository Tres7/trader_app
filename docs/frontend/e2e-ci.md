# Tests E2E en CI (`.github/workflows/e2e.yml`)

## Objectif du document
- Expliquer quand les tests E2E (Maestro sur émulateur Android) tournent en CI.
- Indiquer quand poser le label `e2e` sur une PR.
- Décrire comment réagir à un échec du run nocturne.

## Pourquoi ils ne tournent pas sur chaque PR
Un run E2E prend au moins 10 minutes : build de l'APK, démarrage de l'émulateur, du backend et de Metro. Le lancer sur chaque PR ralentit trop le travail. Les erreurs de logique sont déjà couvertes par `Backend checks` et `Frontend checks` ; les E2E servent de filet pour les parcours bout en bout.

---

# 1. Déclencheurs

| Déclencheur | Quand | Rôle |
|---|---|---|
| Label `e2e` sur la PR | À l'ajout du label, puis à chaque push tant que le label reste posé | Valider une PR qui touche un parcours critique |
| `schedule` | Chaque nuit à 02:00 UTC, sur `main` | Filet de sécurité : détecte une régression dans les 24 h |
| `workflow_dispatch` | Manuel (onglet Actions) | Relancer à la demande, par exemple sur `main` en fin de journée |

Sans le label, le workflow apparaît sur la PR avec un job `e2e` à l'état *skipped* : c'est normal.

`concurrency` + `cancel-in-progress` : un nouveau push sur une PR annule le run E2E en cours, seul le dernier commit est testé. L'ajout d'un autre label (`java`, `dependencies`…) n'annule pas un run en cours.

Le check E2E n'est pas requis par le ruleset `protect-main` : une PR sans label peut être fusionnée.

---

# 2. Quand poser le label `e2e`

Les flows Maestro se trouvent dans `.maestro/flows/`. La CI n'exécute aujourd'hui que `sign-in.yaml` (voir `.github/scripts/run-e2e-tests.sh`). Pose le label si la PR touche :
- l'authentification, côté client (`auth-flow.md`) ou serveur (endpoints `/api/v1/auth/**`, sécurité, JWT) ;
- la navigation racine, le bootstrap de l'app ou la configuration de l'URL d'API ;
- les dépendances natives ou Expo (`package.json`, `app.json`/`app.config`, plugins), ou la configuration Android ;
- les migrations Flyway ou la configuration Spring qui conditionnent le démarrage du backend ;
- les flows Maestro eux-mêmes, `.github/scripts/run-e2e-tests.sh` ou `e2e.yml`.

En cas de doute, pose le label. Quand un nouveau flow est ajouté à la CI, complète cette liste.

---

# 3. Échec du run nocturne
1. Ouvrir le run en échec et télécharger l'artefact `e2e-artifacts` (`logs/backend.log`, `logs/maestro-output.log`, `logs/device.log`, captures Maestro).
2. Vérifier s'il s'agit d'un échec d'infrastructure (émulateur qui ne boote pas, fichier `maestro-exit-code.txt` absent) : relancer via `workflow_dispatch` avant d'investiguer.
3. Si l'échec se reproduit, lister les PR fusionnées depuis le dernier run nocturne vert et identifier celle qui touche le parcours cassé. Pour confirmer, créer une branche sur le commit suspect et lancer un `workflow_dispatch` dessus (le dispatch prend une branche, pas un SHA).
