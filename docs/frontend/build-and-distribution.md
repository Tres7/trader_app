# Build et distribution du client mobile

## Objectif du document
- Figer comment l'application client (Expo/React Native) est actuellement buildée et distribuée aux testeurs.
- Clarifier ce qui nécessite un nouveau build et ce qui n'en nécessite pas.
- Documenter l'absence volontaire d'OTA (mises à jour over-the-air) pendant la phase de test.

## Périmètre
Ce document décrit le comportement actuel autour de :
- les profils de build EAS disponibles ;
- comment lancer un build et le partager avec les testeurs ;
- ce qui se passe pour un testeur qui a déjà l'app installée ;
- la distinction changement JS vs changement natif ;
- pourquoi l'OTA n'est pas en place aujourd'hui ;
- le statut de la cible web.

---

# 1. Profils de build (`client/eas.json`)

## Profils actuels
- `development` : build avec client de développement (`developmentClient: true`), distribution interne.
- `preview` : distribution interne, build Android en `apk` (installable directement sans passer par un store).
- `production` : `autoIncrement` activé, destiné à la soumission sur les stores (`submit.production`).

## Profil utilisé en phase de test
- `preview` est le profil utilisé pour distribuer une version aux testeurs internes actuellement.
- Il génère un `.apk` Android partageable par lien, sans nécessiter de compte testeur sur un store.

---

# 2. Lancer un build

## Commande
```
eas build --profile preview
```

## Ce que ça fait
- EAS compile le binaire natif (Android/iOS) avec le JS embarqué à l'instant du build.
- Le nom de package (`com.traderapp.client`) et les credentials de signature sont gérés et réutilisés automatiquement par EAS.

## Après le build
- EAS fournit un lien (et un QR code) vers le build généré.
- Ce lien est partagé manuellement aux testeurs (pas de distribution automatique).

---

# 3. Ce qui se passe côté testeur

## Pas de suppression/réinstallation nécessaire
- Tant que le `package`/`bundleIdentifier` et la clé de signature restent identiques (cas par défaut avec EAS), installer un nouveau build par-dessus l'ancien est traité comme une **mise à jour normale** par l'OS.
- Aucune perte de données, aucune désinstallation à faire.

## Ce qui casserait cette continuité
- Changer de clé de signature.
- Changer le nom de package/bundle identifier.

---

# 4. Changement JS vs changement natif

## Pourquoi la distinction compte
- Un changement JS pur (logique, UI, styles, nouvelle feature n'ajoutant aucune dépendance native) pourrait en théorie être livré instantanément via l'OTA (`expo-updates`), sans nouveau build.
- Un changement natif (nouvelle dépendance nécessitant du code natif, permission ajoutée/modifiée, upgrade du SDK Expo ou de React Native) nécessite **toujours** un nouveau build, OTA ou pas.

## Situation actuelle
- L'OTA n'est pas configuré (voir section 5).
- Résultat : **aujourd'hui, tout changement — JS ou natif — nécessite un nouveau build et une redistribution manuelle.** La distinction JS/natif n'a donc pas d'effet pratique pour l'instant ; elle deviendra pertinente le jour où l'OTA sera mis en place.

## Exemples déjà rencontrés
- Ajout du toggle de thème (store, composant, hooks) : JS pur, aucune nouvelle dépendance native — candidat naturel à l'OTA si elle existait.
- Un bump de dépendance purement outillage de build (ex. `browserslist` via `overrides`) : n'affecte ni le JS embarqué ni le binaire, aucun impact sur les testeurs, aucun build nécessaire pour ce cas précis.

---

# 5. Pourquoi pas d'OTA pour l'instant

## Décision actuelle
- Mettre en place `expo-updates` (channels, `runtimeVersion` policy, job CI `eas update`) a été jugé prématuré tant que le projet est en phase de test avec un nombre limité de testeurs.

## Raisons
- Le coût d'itération manuel (rebuild + réinstall) reste faible avec peu de testeurs.
- La `runtimeVersion` policy est une décision qu'il vaut mieux prendre une fois l'architecture native stabilisée, pour éviter de devoir la revoir et forcer un nouveau binaire de toute façon.

## Quand reconsidérer
- Quand la friction du rebuild manuel commence à ralentir les itérations.
- Avant la bascule vers un public plus large que les testeurs internes.

---

# 6. Statut de la cible web

## Décision actuelle
- Le web est abandonné comme cible réelle de l'application.
- `expo start --web` reste utilisable ponctuellement pour prévisualiser un changement visuel, mais n'est pas un canal de distribution.

## Limite connue
- `expo-secure-store` n'a aucune implémentation web (stub vide) : tout le flux d'authentification (`hydrateSession`, lecture/écriture/suppression du token) plante sur web.
- Cette limite n'est pas corrigée puisque le web n'est pas une cible supportée.

---

# 7. Résumé global

## Points clés à retenir
- Distribution actuelle : `eas build --profile preview` → lien partagé manuellement.
- Un testeur qui a déjà l'app reçoit une mise à jour normale en installant le nouveau build, pas une réinstallation.
- Aucun canal OTA aujourd'hui : tout changement, JS ou natif, nécessite un nouveau build.
- La distinction JS/natif ne redevient utile que le jour où l'OTA est mis en place.
- Le web n'est pas une cible supportée, y compris pour l'authentification.
