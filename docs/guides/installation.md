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
