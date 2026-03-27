# Installation

Ce document regroupe les étapes d'installation et de lancement du client mobile Expo pour `Trader App`.

## Prérequis

- `Node.js` installé sur la machine
- `npm` disponible
- un téléphone avec l'application `Expo Go` installée
- le téléphone et le PC connectés si possible au même réseau Wi-Fi

## Création du projet Expo

Depuis la racine du dépôt, créer le client Expo avec :

```bash
npx create-expo-app@latest client
```

Cette commande :

- crée le dossier `client/`
- initialise une application Expo
- génère un projet TypeScript par défaut selon le template courant d'Expo

## Installation des dépendances

Se placer dans le dossier du client puis installer les dépendances :

```bash
cd client
npm install
```

## Lancement en local

Pour démarrer le projet Expo en développement :

```bash
npx expo start
```

Expo affiche alors :

- un QR code à scanner avec `Expo Go`
- un accès web local
- des raccourcis comme `w` pour le web ou `a` pour Android

## Test sur ordinateur

Pour ouvrir rapidement le projet dans le navigateur :

```bash
npx expo start --web
```

Ou bien :

```bash
npx expo start
```

puis appuyer sur `w`.

## Test sur téléphone avec Expo Go

1. Installer `Expo Go` sur le téléphone
2. Lancer le projet avec `npx expo start`
3. Scanner le QR code affiché dans le terminal

## Problème réseau en mode local

Il peut arriver que l'application fonctionne sur le PC mais pas sur le téléphone. Dans ce cas, la cause la plus fréquente est un problème d'accès réseau à l'adresse IP locale affichée par Expo.

Dans cette situation, lancer Expo en mode tunnel :

```bash
npx expo start --tunnel
```

Le mode `--tunnel` permet de contourner les problèmes de réseau local, d'IP ou de pare-feu entre le PC et le téléphone.

## Commande recommandée si le QR code ne fonctionne pas

```bash
cd client
npx expo start --tunnel
```

## Configuration du fichier `.env` du serveur

Le fichier `server/.env` doit être créé localement à partir de `server/.env.example`.

Vous trouverez donc dans `server/.env.example` une base de code contenant les variables dont les conteneurs auront besoin:

## Explication des variables

`POSTGRES_DB`
: nom de la base de données PostgreSQL créée au démarrage du conteneur.

`POSTGRES_USER`
: utilisateur PostgreSQL utilisé par l'application.

`POSTGRES_PASSWORD`
: mot de passe de l'utilisateur PostgreSQL.

`PGADMIN_DEFAULT_EMAIL`
: identifiant de connexion à l'interface web pgAdmin.

`PGADMIN_DEFAULT_PASSWORD`
: mot de passe de connexion à pgAdmin.

`SPRING_DATASOURCE_URL`
: URL de connexion JDBC du backend Spring Boot vers PostgreSQL.
Ici, `db` correspond au nom du service PostgreSQL dans `compose.yaml`.

`SPRING_DATASOURCE_USERNAME`
: utilisateur utilisé par Spring Boot pour se connecter à la base.

`SPRING_DATASOURCE_PASSWORD`
: mot de passe utilisé par Spring Boot pour se connecter à la base.

`SPRING_JPA_HIBERNATE_DDL_AUTO`
: stratégie JPA au démarrage.
La valeur `validate` permet de vérifier que le schéma correspond aux entités sans recréer la base.

`SPRING_FLYWAY_ENABLED`
: active Flyway pour les migrations de base de données.

`SERVER_PORT`
: port HTTP exposé par le backend Spring Boot dans le conteneur.

## Étapes recommandées

1. Copier `server/.env.example` vers `server/.env`
2. Remplacer les valeurs `change_me`
3. Vérifier que les identifiants PostgreSQL et Spring Boot correspondent
4. Lancer ensuite les services avec Docker Compose
