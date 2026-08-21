# ADR 007: Déploiement VPS via SSH, Caddy/sslip.io, rollback manuel

## Status
- Accepté

## Contexte
Le backend doit être déployé sur un VPS pour devenir accessible en dehors de l'environnement local, avec HTTPS (obligatoire pour une app mobile consommant l'API), tout en restant simple à exploiter et à faire évoluer par un développeur solo. Le projet n'a pas encore de nom de domaine, et le déploiement doit rester piloté depuis GitHub Actions en réutilisant les manifests déjà produits par le pipeline CI (`deploy/manifests/manifest-X.Y.Z.yaml`, image pinnée par digest).

Trois décisions distinctes mais liées ont dû être prises : comment obtenir du HTTPS sans domaine, comment gérer un déploiement raté, et où stocker les secrets nécessaires au déploiement.

## Options

### HTTPS et reverse proxy
- Option 1 : nginx + certbot configuré manuellement
  - Largement documenté et éprouvé.
  - Demande une configuration et un renouvellement de certificat gérés à la main (ou via un cron séparé), plus de surface à maintenir pour un seul développeur.

- Option 2 : Traefik
  - HTTPS automatique également, avec une intégration Docker poussée (labels par service).
  - Sa configuration (providers, entrypoints, middlewares) est plus complexe que nécessaire pour un unique service à exposer.

- Option 3 : Caddy, exécuté comme service Docker Compose (pas un paquet système)
  - HTTPS automatique (Let's Encrypt) avec une configuration minimale (`Caddyfile` de quelques lignes).
  - Tourne comme n'importe quel autre conteneur du `docker-compose.prod.yaml`, aucune installation système à gérer sur le VPS.

### Nom d'hôte sans domaine
- Option 1 : acheter un nom de domaine
  - Coût récurrent et gestion DNS supplémentaire, disproportionné pour une phase de test/staging.

- Option 2 : sslip.io
  - Service DNS public gratuit : `<ip-avec-tirets>.sslip.io` résout automatiquement vers l'IP correspondante, sans compte ni configuration.
  - Vu comme un nom de domaine valide par Let's Encrypt, donc compatible avec l'HTTPS automatique de Caddy.

### Stratégie de rollback
- Option 1 : rollback automatique si le health-check post-déploiement échoue
  - Séduisant en apparence (auto-guérison), mais dangereux dans ce contexte précis :
    - Un health-check mal calibré (timeout trop court, endpoint répondant 200 alors que la DB est down, cold start lent) peut déclencher des rollbacks en boucle ou des rollbacks sur un déploiement qui aurait fini par réussir.
    - Les migrations Flyway sont irréversibles : revenir à une ancienne image applicative après une migration en avant peut casser l'application plus qu'un déploiement raté ne l'aurait fait (schéma DB déjà migré, code applicatif redevenu incompatible).
    - En solo, un rollback automatique et silencieux retire la visibilité sur ce qui s'est réellement passé — le développeur peut ne pas savoir qu'un rollback a eu lieu avant de consulter les logs.

- Option 2 : rollback manuel, en redéployant un `manifest_version` antérieur
  - `deploy.yml` accepte un input `manifest_version` (vide = dernière version, sinon une version antérieure précise).
  - Un rollback est donc littéralement un nouveau déploiement, avec la même logique (backup préalable, health-check, etc.), juste une image différente — aucune logique de rollback séparée à écrire ni à maintenir.

### Emplacement des secrets
- Option 1 : tous les secrets (DB, JWT, mail, RabbitMQ, R2) en secrets GitHub Actions
  - Centralisé, mais fait transiter des identifiants applicatifs sensibles par la CI à chaque déploiement, alors qu'ils ne sont utiles qu'au runtime du VPS.

- Option 2 : secrets applicatifs uniquement sur le VPS (`/opt/traderapp/shared/server.env`, créé une fois à la main, jamais committé ni régénéré par la CI), secrets GitHub Actions limités à l'accès SSH (`VM_HOST`, `VM_USER`, `SSH_PRIVATE_KEY`, `VM_PORT`)
  - Réduit la surface d'exposition : un secret GitHub compromis (SSH) ne donne accès qu'au VPS lui-même, pas directement à un dump des identifiants applicatifs stockés dans GitHub.
  - Cohérent avec le fait que le bundle de déploiement (`current/`) est entièrement jetable et recréé à chaque run, alors que les secrets doivent survivre indéfiniment.

## Décision
- **Caddy** est utilisé comme reverse proxy, exécuté comme service Docker Compose, avec HTTPS automatique via Let's Encrypt.
- **sslip.io** fournit le nom d'hôte public (`<ip-avec-tirets>.sslip.io`), le temps que le projet n'ait pas de nom de domaine dédié.
- Le **rollback est exclusivement manuel** : `deploy.yml` (workflow_dispatch) prend un input `manifest_version` ; une version antérieure = rollback, en relançant le même workflow. Aucune logique de rollback automatique n'est implémentée.
- Les **secrets applicatifs vivent uniquement sur le VPS** (`shared/server.env`), jamais dans GitHub Actions. Les secrets GitHub sont strictement limités à l'accès SSH.

## Conséquences

### Positives (bénéfices)
- Zéro coût et zéro dépendance à un nom de domaine pour avoir du HTTPS valide en production.
- Le renouvellement de certificat est entièrement automatique (géré par Caddy), aucune tâche récurrente à maintenir.
- Le rollback manuel force une compréhension explicite de ce qui a échoué avant d'agir, cohérent avec un contexte solo où la disponibilité pour réagir à un incident n'est pas garantie 24/7.
- Le déploiement (`deploy.yml`) et le rollback partagent exactement le même chemin de code — aucune logique de rollback séparée, donc aucun risque de divergence ou de bug spécifique au rollback jamais testé.
- Un secret GitHub compromis ne donne pas un accès direct aux identifiants applicatifs (DB, JWT, mail, R2), seulement à l'accès SSH du VPS.

### Négatifs (inconvénients)
- `sslip.io` est un service tiers gratuit : sa disponibilité n'est pas contractuellement garantie. Une panne de ce service casserait la résolution DNS du backend (mitigation : migrer vers un vrai domaine reste une opération simple, un seul champ `PUBLIC_HOSTNAME` à changer).
- Un déploiement qui échoue nécessite une intervention manuelle (relancer `deploy.yml` avec une version antérieure) plutôt qu'une auto-guérison — acceptable en solo, deviendrait un point de friction avec plusieurs développeurs ou une astreinte formelle.
- Les secrets applicatifs vivant uniquement sur le VPS doivent être sauvegardés/documentés séparément (aucune trace dans git ni dans GitHub) — leur perte (VPS détruit sans backup du fichier) nécessiterait de tous les régénérer.

### Impacts futurs
- Si le projet passe à plusieurs développeurs ou vise une haute disponibilité, la stratégie de rollback manuel et l'architecture mono-instance (downtime pendant chaque déploiement, voir `deploy/README.md`) devront être réévaluées vers quelque chose de plus automatisé (blue-green, health-check applicatif plus poussé qu'un simple `/actuator/health`).
- Si un nom de domaine est acheté plus tard, la migration se limite à changer la variable GitHub `PUBLIC_HOSTNAME` — aucun changement de code.
- Toute nouvelle variable secrète nécessaire au runtime doit être ajoutée à `deploy/server.env.template` (documentation) et à `/opt/traderapp/shared/server.env` sur le VPS, jamais comme secret GitHub Actions, pour rester cohérent avec cette décision.
