# Structure typique d'un module backend

## Objectif du document
- Expliquer comment un module backend est organisé dans Trader App.
- Clarifier le rôle des principaux dossiers et fichiers.
- Donner une lecture simple de l'architecture sans dépendre uniquement des conventions implicites du projet.

## Vue d'ensemble
Chaque module backend suit une structure inspirée de l'architecture hexagonale.

Les couches principales sont :
- `domain`
- `application`
- `infrastructure`
- `presentation`

L'idée générale est la suivante :
- le **domain** porte les règles métier ;
- l'**application** orchestre les cas d'usage ;
- l'**infrastructure** branche la technique ;
- la **presentation** expose les entrées HTTP.

## Exemple d'arborescence
```text
modules/
  auth/
    application/
    domain/
    infrastructure/
    presentation/
```

Chaque module garde sa propre logique métier et sa propre organisation interne.

---

# 1. La couche `domain`

## Rôle
La couche `domain` contient le cœur métier pur.

On y met ce qui exprime :
- les règles métier ;
- les objets métier ;
- les contraintes métier ;
- les erreurs métier ;
- les contrats métier de persistance.

## Ce qu'on y trouve souvent
- `entities`
- `valueObjects`
- `exceptions`
- `repositories`

## Détail des sous-dossiers

### `entities`
- Représentent les objets métier principaux du module.
- Elles portent le comportement métier important.
- Exemple dans `auth` :
  * `User`
  * `EmailVerificationCode`
  * `PasswordResetCode`

### `valueObjects`
- Représentent des valeurs métier avec validation intégrée.
- Ils protègent le domaine contre les données invalides.
- Exemple :
  * `Email`
  * `FirstName`
  * `LastName`
  * `BirthDate`
  * `Country`
  * `PasswordHash`

### `exceptions`
- Représentent les erreurs métier explicites.
- Elles permettent d'exprimer des cas comme :
  * identifiants invalides ;
  * email non vérifié ;
  * code expiré ;
  * utilisateur introuvable.

### `repositories`
- Contiennent les interfaces (contrats) métier de persistance.
- Le domaine et l'application connaissent ces contrats, mais pas leur implémentation technique.
- Exemple :
  * `UserRepository`
  * `EmailVerificationCodeRepository`

## Ce qu'on évite dans `domain`
- les annotations HTTP ;
- les détails Spring ;
- le code SQL ;
- les réponses API ;
- les dépendances à RabbitMQ, PostgreSQL ou JWT.

---

# 2. La couche `application`

## Rôle
La couche `application` orchestre les cas d'usage du module.

Elle sert à :
- recevoir une intention métier ;
- appeler le domaine ;
- utiliser les ports nécessaires ;
- coordonner les effets secondaires ;
- renvoyer un résultat exploitable.

## Ce qu'on y trouve souvent
- `commands`
- `dto`
- `events`
- `ports`
- `service`
- `usecases`

## Détail des sous-dossiers

### `commands`
- Représentent les données d'entrée d'un cas d'usage.
- Ils servent à découpler la couche HTTP du cœur applicatif.
- Exemple :
  * `RegisterUserCommand`
  * `LoginCommand`
  * `VerifyEmailCommand`

### `dto`
- Représentent des objets de transfert internes à l'application.
- Ils servent souvent à transporter un résultat de cas d'usage.
- Exemple :
  * `LoginResult`
  * `AuthenticatedUser`

### `events`
- Représentent les événements métier émis par le module.
- Ils servent à propager une information vers d'autres modules ou adapters.
- Exemple :
  * `UserRegisteredEvent`
  * `PasswordResetRequestedEvent`

### `ports`
- Définissent les contrats nécessaires aux cas d'usage pour parler au monde extérieur.
- Ils sont souvent séparés en ports d'entrée et de sortie.
- Exemple :
  * `AuthEventPublisher`

### `service`
- Contiennent des abstractions applicatives utiles aux cas d'usage.
- Exemple :
  * `JwtService`
  * `PasswordHasher`
  * `PasswordVerifier`

### `usecases`
- Contiennent les cas d'usage métier du module.
- C'est souvent ici que se fait l'orchestration principale.
- Exemple :
  * `RegisterUser`
  * `Login`
  * `GetCurrentUser`
  * `UpdateCurrentUserProfile`

## Ce qu'on évite dans `application`
- écrire directement du SQL ;
- créer des `ResponseEntity` ;
- dépendre directement des détails HTTP ;
- faire de la logique de présentation.

---

# 3. La couche `infrastructure`

## Rôle
La couche `infrastructure` implémente les détails techniques.

Elle sert à brancher :
- la persistance ;
- la sécurité ;
- la messagerie ;
- les intégrations externes ;
- les adapters techniques des ports définis en application.

## Ce qu'on y trouve souvent
- `http`
- `messaging`
- `persistence`
- `security`

## Détail des sous-dossiers

### `http`
- Contient les objets de réponse HTTP spécifiques à l'infrastructure web.
- Exemple :
  * `RegisterUserResponse`
  * `GetCurrentUserResponse`

### `messaging`
- Contient les adapters liés au broker de messages.
- Exemple :
  * `RabbitMqAuthEventPublisher`
  * configuration RabbitMQ

### `persistence`
- Contient la persistance réelle.
- On y trouve souvent :
  * les entités JPA ;
  * les repositories Spring Data ;
  * les implémentations concrètes des repositories métier.

### `security`
- Contient la sécurité technique.
- Exemple :
  * filtre JWT ;
  * service de token ;
  * hash/verifier de mot de passe ;
  * configuration Spring Security.

## Ce qu'on évite dans `infrastructure`
- mettre des règles métier importantes ;
- transformer les controllers en couche métier ;
- contourner les ports de l'application quand une abstraction existe déjà.

---

# 4. La couche `presentation`

## Rôle
La couche `presentation` définit ce qui entre depuis l'extérieur, en particulier via HTTP.

Elle sert à :
- recevoir la requête ;
- valider la forme d'entrée ;
- mapper cette entrée vers un command applicatif.

## Ce qu'on y trouve souvent
- `rest/requests`

## Détail

### `requests`
- Représentent les corps des requêtes HTTP entrantes.
- Elles correspondent à la forme du contrat API exposé.
- Exemple :
  * `LoginRequest`
  * `RegisterUserRequest`
  * `UpdateCurrentUserPasswordRequest`

## Remarque importante
Dans ce projet, les controllers sont actuellement placés dans :
- `infrastructure/http/controllers`

Autrement dit :
- les **requests entrantes** vivent dans `presentation`
- les **controllers** et **responses sortantes** vivent dans `infrastructure/http`

Ce choix reste cohérent tant qu'il est appliqué de manière constante.

---

# 5. Comment lire un module de haut niveau

## Pour comprendre rapidement un module
- Commencer par les `usecases` dans `application/usecases`
- Regarder ensuite les `commands` associés
- Lire les objets métier dans `domain`
- Vérifier les `ports` utilisés
- Terminer par les adapters réels dans `infrastructure`

## Exemple concret sur le module `auth`
Pour comprendre l'inscription :
1. regarder `RegisterUser` ;
2. regarder `RegisterUserCommand` ;
3. regarder `User`, `Email`, `BirthDate`, `Country` ;
4. regarder `AuthEventPublisher` ;
5. regarder l'implémentation RabbitMQ et la persistance.

---

# 6. Règles pratiques à respecter

## Règles générales
- Le controller ne doit pas contenir la logique métier.
- Le use case orchestre ; il ne devient pas une couche HTTP.
- Le domaine reste indépendant des détails techniques.
- L'infrastructure implémente les adapters, elle ne définit pas les règles métier.

## En cas de doute
- Si le code répond à une règle métier : il appartient probablement au `domain`.
- Si le code orchestre une action métier : il appartient probablement à `application`.
- Si le code parle à la base, au broker, au réseau ou à Spring : il appartient probablement à `infrastructure`.
- Si le code représente un contrat HTTP entrant : il appartient probablement à `presentation`.

---

# 7. Résumé

## En une phrase
Un module backend dans Trader App est organisé pour garder le métier au centre, l'application comme orchestrateur, l'infrastructure comme branchement technique et la présentation comme point d'entrée.

## À retenir
- `domain` = règles métier
- `application` = cas d'usage
- `infrastructure` = détails techniques
- `presentation` = contrats d'entrée HTTP
