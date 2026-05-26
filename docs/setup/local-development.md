# Développement local

## Objectif du document
- Donner une procédure simple pour lancer l'application en local.
- Centraliser les ports, les services et les variables utiles.
- Éviter de devoir reconstituer le setup à chaque reprise du projet.

## Périmètre
Ce document couvre l'environnement local actuel du projet, tel qu'il existe aujourd'hui :
- backend Spring Boot ;
- base PostgreSQL ;
- RabbitMQ ;
- pgAdmin ;
- front Expo / React Native.

## Remarque importante
Ce document décrit l'état réel du projet au moment de sa rédaction.

Actuellement, le `docker compose` du projet lance :
- `db`
- `pgadmin`
- `server`
- `rabbitmq`

Mailhog n'est donc pas encore présent dans le `compose.yaml` actuel, même s'il peut faire partie des outils visés à terme.

---

# 1. Pré-requis

## Outils à avoir
- Docker Desktop
- Docker Compose
- Node.js
- npm
- Java 21 si vous voulez aussi lancer le backend hors Docker plus tard

## Vérifications utiles
Avant de lancer le projet, vérifier que :
- Docker Desktop est démarré ;
- les ports nécessaires ne sont pas déjà pris ;
- le téléphone et le PC sont sur le même réseau local si vous testez sur mobile physique.

---

# 2. Structure utile à connaître

## Racine du projet
Les commandes sont généralement lancées depuis la racine du projet :

```powershell
cd c:\Users\kpogl\OneDrive\Documents\Informatique\Projets_perso\trader_app
```

## Dossiers principaux utiles au setup
- `server/`
- `client/`
- `compose.yaml`
- `server/.env`
- `client/.env`

---

# 3. Services locaux actuels

## Services du `compose.yaml`
Le fichier `compose.yaml` lance actuellement :

### Base de données PostgreSQL
• service : `db`  
• image : `postgres:16-bookworm`

### pgAdmin
• service : `pgadmin`  
• image : `dpage/pgadmin4:latest`

### Backend
• service : `server`  
• build local à partir de `server/Dockerfile`

### RabbitMQ
• service : `rabbitmq`  
• image : `rabbitmq:3-management`

## Volumes persistants
Les volumes définis sont :
- `traderapp_db`
- `rabbitmq_data`

Cela signifie que :
- la base PostgreSQL persiste entre les redémarrages ;
- RabbitMQ garde aussi ses données locales.

---

# 4. Variables d'environnement actuelles

## Backend : `server/.env`
Le projet utilise actuellement notamment :
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `PGADMIN_DEFAULT_EMAIL`
- `PGADMIN_DEFAULT_PASSWORD`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `SPRING_RABBITMQ_HOST`
- `SPRING_RABBITMQ_PORT`
- `SPRING_RABBITMQ_USERNAME`
- `SPRING_RABBITMQ_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`
- les variables mail (`MAIL_USERNAME`, `MAIL_PASSWORD`, etc.)

## Front : `client/.env`
Le front utilise actuellement :
- `EXPO_PUBLIC_API_BASE_URL`

Cette variable est essentielle :
- elle détermine l'URL de base appelée par Axios ;
- elle doit être correcte selon le mode de test utilisé.

---

# 5. Lancer l'environnement backend avec Docker

## Commande principale
Depuis la racine du projet :

```powershell
docker compose up --build
```

## Variante en arrière-plan
Si vous voulez lancer les services en détaché :

```powershell
docker compose up --build -d
```

## Arrêter les services

```powershell
docker compose down
```

## Vérifier les conteneurs

```powershell
docker compose ps
```

## Lire les logs
### Backend

```powershell
docker logs -f traderapp_server
```

### Base de données

```powershell
docker logs -f traderapp-db
```

### Tous les services via Compose

```powershell
docker compose logs -f
```

---

# 6. Ports utiles en local

## Backend
- application Spring Boot : `http://localhost:8080`

## PostgreSQL
- port hôte : `5433`
- port conteneur : `5432`

Cela veut dire que, depuis votre machine hôte, la connexion PostgreSQL passe par :
- hôte : `localhost`
- port : `5433`

## pgAdmin
- URL : `http://localhost:5051`

## RabbitMQ
- AMQP : `5672`
- interface d'administration : `http://localhost:15672`

---

# 7. Accéder à pgAdmin

## URL locale

```text
http://localhost:5051
```

## Identifiants
Ils viennent actuellement de `server/.env` :
- `PGADMIN_DEFAULT_EMAIL`
- `PGADMIN_DEFAULT_PASSWORD`

## Connexion au serveur PostgreSQL depuis pgAdmin
Pour enregistrer le serveur PostgreSQL dans pgAdmin, utiliser :
- Host : `db` si vous êtes dans le réseau Docker concerné
- ou `host.docker.internal` / `localhost` selon votre mode d'accès
- Port : `5432` dans le réseau Docker, `5433` depuis l'hôte
- Database : valeur de `POSTGRES_DB`
- Username : valeur de `POSTGRES_USER`
- Password : valeur de `POSTGRES_PASSWORD`

---

# 8. Accéder à RabbitMQ

## Interface d'administration

```text
http://localhost:15672
```

## Identifiants
Ils viennent de `server/.env` :
- `SPRING_RABBITMQ_USERNAME`
- `SPRING_RABBITMQ_PASSWORD`

## Rôle dans le projet
RabbitMQ sert de message broker pour :
- publier des événements métier ;
- découpler certains modules ;
- préparer les échanges inter-modules.

---

# 9. Lancer le front Expo

## Commandes utiles
Depuis le dossier `client/` :

```powershell
cd client
npm install
npm run start
```

## Scripts disponibles
Le `package.json` du client expose actuellement :
- `npm run start`
- `npm run android`
- `npm run ios`
- `npm run web`
- `npm run lint`

## Modes fréquents
### Lancer Expo normalement

```powershell
npm run start
```

### Lancer sur Android

```powershell
npm run android
```

### Lancer sur le web

```powershell
npm run web
```

---

# 10. Bien configurer `EXPO_PUBLIC_API_BASE_URL`

## Cas 1 : test dans le navigateur web sur le même PC
Vous pouvez souvent utiliser :

```text
http://localhost:8080
```

## Cas 2 : test sur téléphone physique
Il faut utiliser l'adresse IP locale de votre PC, par exemple :

```text
http://10.225.166.152:8080
```

## Pourquoi ?
Depuis un téléphone :
- `localhost` pointe vers le téléphone lui-même ;
- pas vers votre machine de développement.

## Symptôme classique d'une mauvaise URL
Si la valeur est mauvaise, le front affiche souvent des erreurs du type :
- `ERR_NETWORK`
- aucune réponse HTTP ;
- échec au login malgré un backend démarré.

---

# 11. Ordre de lancement recommandé

## Séquence simple
1. démarrer Docker Desktop ;
2. lancer le backend et les services avec `docker compose up --build` ;
3. attendre que :
   ◦ PostgreSQL soit healthy ;
   ◦ le backend soit démarré sur `8080` ;
4. vérifier `client/.env` ;
5. lancer Expo dans `client/`.

## Vérification rapide
Une fois le backend démarré, vous devez pouvoir :
- atteindre `http://localhost:8080` ou au moins le port ;
- ouvrir pgAdmin sur `http://localhost:5051` ;
- ouvrir RabbitMQ sur `http://localhost:15672`.

---

# 12. Dépannage courant

## Le téléphone n'arrive pas à se connecter au backend
Vérifier :
- que `EXPO_PUBLIC_API_BASE_URL` pointe vers l'IP locale du PC ;
- que le backend expose bien `8080:8080` ;
- que le téléphone et le PC sont sur le même réseau ;
- que le pare-feu Windows ne bloque pas le port `8080`.

## Le backend démarre mais le front échoue quand même
Regarder :
- les logs de `traderapp_server` ;
- la réponse HTTP réelle côté front ;
- la configuration de l'URL de base Axios.

## PostgreSQL est lancé mais l'application ne répond pas
Vérifier :
- l'état du conteneur `traderapp-db` ;
- les logs Flyway/Hibernate ;
- la disponibilité réelle du backend sur `8080`.

## Un conteneur est "Started" mais pas réellement utilisable
Se rappeler que :
- `Started` ne veut pas forcément dire "métier opérationnel" ;
- il faut aussi lire les logs applicatifs.

---

# 13. Commandes pratiques à garder

## Redémarrer proprement l'environnement

```powershell
docker compose down
docker compose up --build
```

## Vérifier uniquement les services

```powershell
docker compose ps
```

## Ouvrir les logs du backend

```powershell
docker logs -f traderapp_server
```

## Lancer le front

```powershell
cd client
npm run start
```

---

# 14. Résumé

## À retenir
- Le backend et les services locaux passent actuellement par Docker Compose.
- Le front se lance via Expo dans le dossier `client/`.
- La variable la plus sensible côté front est `EXPO_PUBLIC_API_BASE_URL`.
- Les outils d'administration disponibles aujourd'hui sont :
  ◦ pgAdmin ;
  ◦ RabbitMQ Management.

## Point d'attention principal
Le problème local le plus fréquent sur mobile n'est pas le backend lui-même, mais la connectivité :
- mauvaise IP ;
- port bloqué ;
- appareil hors du bon réseau.
