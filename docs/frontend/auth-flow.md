# Flux d'authentification côté front

## Objectif du document
- Figer le flux d'authentification actuellement en place dans le front.
- Clarifier les écrans, la navigation et le rôle du store d'authentification.
- Donner une vue simple du comportement réel avant que le module n'évolue encore.

## Périmètre
Ce document décrit le comportement actuel du front autour de :
- l'inscription ;
- la vérification d'email ;
- la connexion ;
- la restauration de session ;
- les guards de navigation ;
- le profil connecté ;
- la sécurité du compte ;
- la place prévue pour Google sign-in.

---

# 1. Vue d'ensemble

## Point d'entrée
L'entrée principale de l'application passe par :
- `client/app/_layout.tsx`
- `client/app/index.tsx`

## Idée générale
Le fonctionnement actuel est le suivant :
1. l'application démarre ;
2. le layout racine tente de restaurer la session ;
3. tant que cette hydratation n'est pas terminée, un écran de chargement est affiché ;
4. ensuite, `index.tsx` redirige :
   ◦ vers `/(tabs)` si l'utilisateur est authentifié ;
   ◦ vers `/sign-in` sinon.

## Rôle du store
Le store `useAuthStore` porte actuellement :
- `accessToken`
- `user`
- `isAuthenticated`
- `isHydrating`

Et il expose les actions principales :
- `setSession`
- `refreshUser`
- `hydrateSession`
- `logout`

---

# 2. Hydratation de session au démarrage

## Où cela se passe
Le bootstrap de session se fait dans :
- `client/app/_layout.tsx`

## Mécanisme actuel
Le layout racine :
- lit `isHydrating` dans le store ;
- appelle `hydrateSession()` une seule fois au bootstrap ;
- bloque le rendu principal tant que la session est en cours de restauration.

## Séquence actuelle
1. l'application monte ;
2. `hydrateSession()` est lancée ;
3. le store lit le token enregistré localement ;
4. si aucun token n'existe :
   ◦ l'utilisateur reste non authentifié ;
   ◦ l'hydratation se termine ;
5. si un token existe :
   ◦ le front appelle `GET /api/v1/auth/me` ;
   ◦ si l'appel réussit, le store reconstruit l'utilisateur minimal ;
   ◦ sinon, le token est supprimé et la session est vidée.

## Pourquoi c'est important
Cette étape évite :
- les faux écrans connectés ;
- les boucles de navigation ;
- les flashes entre écran public et écran privé.

---

# 3. Flux d'inscription

## Écran concerné
- `sign-up`

## Composant principal
- `client/src/features/auth/ui/sign-up-form.tsx`

## Données envoyées
Le formulaire envoie actuellement :
- `firstName`
- `lastName`
- `birthDate`
- `email`
- `password`
- `country`

## API utilisée
Le front appelle :
- `POST /api/v1/auth/register`

via :
- `register(...)` dans `client/src/features/auth/api/auth-api.ts`

## Comportement après succès
Après inscription réussie :
- le front ne connecte pas automatiquement l'utilisateur ;
- il redirige vers `/verify-email` ;
- l'email est transmis dans les paramètres de route.

## Gestion actuelle des erreurs
Le formulaire gère notamment :
- `409` : un compte existe déjà avec cet email ;
- `400` : données invalides ;
- autre : erreur générique.

---

# 4. Flux de vérification d'email

## Écran concerné
- `verify-email`

## Composant principal
- `client/src/features/auth/ui/verify-email-form.tsx`

## API utilisées
Le front appelle :
- `POST /api/v1/auth/verify-email`
- `POST /api/v1/auth/resend-verification-code`

## Séquence actuelle
1. l'utilisateur arrive depuis l'inscription ;
2. il saisit le code reçu par email ;
3. le front appelle `verifyEmail(...)` ;
4. si le code est valide, redirection vers `/sign-in`.

## Renvoi du code
Le formulaire gère aussi :
- un bouton de renvoi ;
- un cooldown local de 30 secondes ;
- un message d'information si le code est renvoyé avec succès.

## Gestion actuelle des erreurs
Le formulaire distingue notamment :
- email déjà vérifié ;
- code expiré ;
- code déjà utilisé ;
- code invalide ;
- erreur générique.

---

# 5. Flux de connexion

## Écran concerné
- `sign-in`

## Composant principal
- `client/src/features/auth/ui/sign-in-form.tsx`

## API utilisée
Le front appelle :
- `POST /api/v1/auth/login`

## Séquence actuelle
1. l'utilisateur saisit son email et son mot de passe ;
2. le front appelle `login(...)` ;
3. si le backend répond avec un token :
   ◦ `setSession(...)` est appelée ;
   ◦ le token est stocké localement ;
   ◦ le store passe `isAuthenticated` à `true` ;
4. le front redirige ensuite vers `/(tabs)`.

## Ce que fait `setSession`
`setSession(...)` :
- sauvegarde le token via `auth-storage` ;
- met à jour le store avec :
  ◦ `accessToken`
  ◦ `user`
  ◦ `isAuthenticated = true`
  ◦ `isHydrating = false`

## Gestion actuelle des erreurs
Le formulaire gère notamment :
- `401` : email ou mot de passe invalide ;
- `403` : email non vérifié ;
- autre : erreur générique.

---

# 6. Flux "mot de passe oublié"

## Écrans concernés
- `forgot-password`
- `reset-password`

## Composants principaux
- `client/src/features/auth/ui/forgot-password-form.tsx`
- `client/src/features/auth/ui/reset-password-form.tsx`

## Étape 1 : demande de réinitialisation
Le formulaire "mot de passe oublié" :
- demande un email ;
- appelle `POST /api/v1/auth/forgot-password` ;
- redirige vers `/reset-password` avec l'email en paramètre si l'opération réussit.

## Étape 2 : définition du nouveau mot de passe
Le formulaire "reset password" :
- demande :
  ◦ l'email ;
  ◦ le nouveau mot de passe ;
  ◦ le code de réinitialisation ;
- appelle `POST /api/v1/auth/reset-password` ;
- redirige vers `/sign-in` après succès.

## Gestion actuelle des erreurs
Le flux gère notamment :
- email introuvable ;
- code expiré ;
- code déjà utilisé ;
- code invalide ;
- erreur générique.

---

# 7. Guards de navigation

## Objectif
Empêcher :
- un utilisateur non connecté d'accéder à l'espace privé ;
- un utilisateur connecté de revenir inutilement sur les écrans d'authentification.

## Guard public
Le layout :
- `client/app/(auth)/_layout.tsx`

fait la règle suivante :
- si l'utilisateur est authentifié et que l'hydratation est terminée ;
- alors redirection vers `/(tabs)`.

## Guard privé
Le layout :
- `client/app/(tabs)/_layout.tsx`

fait la règle suivante :
- si l'utilisateur n'est pas authentifié et que l'hydratation est terminée ;
- alors redirection vers `/sign-in`.

## Porte d'entrée
La route :
- `client/app/index.tsx`

sert uniquement de dispatch :
- `/(tabs)` si authentifié ;
- `/sign-in` sinon.

---

# 8. Déconnexion

## Où cela se déclenche
La déconnexion est proposée dans :
- l'écran `Profil`

## Ce que fait `logout`
L'action :
- supprime le token local ;
- vide `accessToken` ;
- vide `user` ;
- remet `isAuthenticated` à `false`.

## Effet sur la navigation
Après déconnexion :
- le guard des tabs ne laisse plus l'utilisateur rester dans l'espace privé ;
- l'application revient vers le flux public.

---

# 9. Profil connecté et sécurité du compte

## Écrans actuels
- `/profile`
- `/profile/information`
- `/profile/security`

## Logique actuelle
### Profil
L'écran `Profil` joue le rôle de menu :
• Informations personnelles
• Sécurité

### Informations personnelles
L'écran `profile/information` :
- charge `GET /api/v1/auth/me` ;
- préremplit le formulaire ;
- appelle `PATCH /api/v1/auth/me` ;
- met à jour le store avec `refreshUser(...)` après succès.

### Sécurité
L'écran `profile/security` :
- permet de changer le mot de passe connecté ;
- appelle `PATCH /api/v1/auth/me/password` ;
- distingue le cas "mot de passe actuel incorrect" ;
- affiche un message de succès en cas de réussite.

---

# 10. Google sign-in : état actuel du front

## Ce qui existe déjà
Le composant :
- `client/src/features/auth/ui/social-connections.tsx`

est déjà branché dans :
- `sign-in-form.tsx`
- `sign-up-form.tsx`

## Ce que cela signifie
La place du bouton Google est déjà définie visuellement dans le flow réel :
- visible sur connexion ;
- visible sur inscription.

## Ce qui n'est pas encore implémenté
Pour l'instant :
- le clic sur Google ne lance aucun flow OAuth réel ;
- aucun échange backend pour Google n'est encore branché ;
- aucune session applicative n'est encore créée via Google.

## Navigation prévue après succès
La navigation cible logique est déjà claire :
- après un login social réussi ;
- le front devra appeler `setSession(...)` ;
- puis rediriger vers `/(tabs)`.

---

# 11. Résumé global du flux

## Parcours non connecté
1. entrée dans l'application ;
2. hydratation de session ;
3. redirection vers `/sign-in` si aucun token valide ;
4. possibilité de :
   ◦ s'inscrire ;
   ◦ vérifier son email ;
   ◦ se connecter ;
   ◦ demander une réinitialisation de mot de passe.

## Parcours connecté
1. token valide restauré ;
2. récupération de `/me` ;
3. accès à `/(tabs)` ;
4. accès au profil ;
5. accès à :
   ◦ la modification des informations personnelles ;
   ◦ la modification du mot de passe ;
6. possibilité de se déconnecter.

## Point clé à retenir
Aujourd'hui, le front d'authentification est organisé autour de trois piliers :
- un store `zustand` pour la session ;
- des guards `expo-router` pour protéger les routes ;
- un ensemble de formulaires spécialisés pour chaque étape du parcours utilisateur.
