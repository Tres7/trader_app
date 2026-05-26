# Ports et adapters dans le backend

## Objectif du document
- Clarifier ce qu'est un port dans l'architecture hexagonale du projet.
- Clarifier ce qu'est un adapter dans le projet.
- Donner des exemples concrets tirés du code actuel pour éviter de garder ces notions trop théoriques.

## Pourquoi ce document est utile
Dans Trader App, l'approche hexagonale est déjà appliquée dans les faits :
- le métier n'appelle pas directement les détails techniques ;
- les cas d'usage passent par des abstractions ;
- l'infrastructure branche des implémentations concrètes.

Le problème, c'est qu'on peut très bien utiliser cette architecture sans toujours mettre les bons mots dessus.

Ce document sert donc à répondre clairement à deux questions :
1. Qu'est-ce qu'un port ?
2. Qu'est-ce qu'un adapter ?

---

# 1. Vue d'ensemble

## Idée générale
L'architecture hexagonale cherche à garder le métier au centre et à isoler les dépendances techniques autour de lui.

On peut la résumer ainsi :
- le cœur du système exprime ce dont il a besoin ;
- le monde extérieur vient se brancher sur ce cœur ;
- les contrats sont définis côté métier ou application ;
- les implémentations concrètes vivent côté infrastructure.

## Résumé rapide
- Un **port** est un contrat.
- Un **adapter** est une implémentation concrète ou un point de branchement.

---

# 2. Qu'est-ce qu'un port ?

## Définition
Un port est une interface ou une abstraction qui exprime une collaboration nécessaire au cœur applicatif.

Autrement dit :
- le métier ou l'application disent "j'ai besoin de ceci" ;
- ils ne disent pas "je veux PostgreSQL", "je veux RabbitMQ" ou "je veux BCrypt".

## Rôle d'un port
- Protéger le cœur métier des détails techniques.
- Permettre de remplacer une implémentation par une autre.
- Faciliter les tests.
- Rendre les dépendances explicites.

## Où trouve-t-on les ports dans Trader App ?
Dans ce projet, les ports se trouvent surtout dans :
- `application/ports`
- `application/service`
- parfois `domain/repositories`

## Exemples concrets de ports dans le projet
### Ports applicatifs
• `AuthEventPublisher`
• `JwtService`
• `PasswordHasher`
• `PasswordVerifier`

### Contrats de persistance côté domaine
• `UserRepository`
• `EmailVerificationCodeRepository`
• `PasswordResetCodeRepository`

## Exemple simple
Le use case `RegisterUser` a besoin :
- de sauvegarder un utilisateur ;
- de hacher un mot de passe ;
- d'émettre un événement métier.

Il ne devrait pas savoir :
- si la sauvegarde passe par JPA ;
- si le hash utilise BCrypt ;
- si l'événement part dans RabbitMQ.

Il dépend donc de ports comme :
• `UserRepository`
• `PasswordHasher`
• `AuthEventPublisher`

---

# 3. Qu'est-ce qu'un adapter ?

## Définition
Un adapter est une implémentation concrète qui permet de connecter un port à une technologie réelle, ou de connecter le système à une entrée/sortie externe.

En pratique, un adapter :
- traduit ;
- branche ;
- convertit ;
- transporte ;
- persiste ;
- publie ;
- sécurise.

## Rôle d'un adapter
- Implémenter un port défini par le cœur.
- Faire le pont entre le projet et un système externe.
- Encapsuler une technologie particulière.

## Où trouve-t-on les adapters dans Trader App ?
Dans ce projet, les adapters vivent principalement dans :
- `infrastructure/persistence`
- `infrastructure/messaging`
- `infrastructure/security`
- `infrastructure/http`

## Exemples concrets d'adapters dans le projet
### Adapters de persistance
• `JpaUserRepository`
• `JpaEmailVerificationCodeRepository`
• `JpaPasswordResetCodeRepository`

### Adapters de messagerie
• `RabbitMqAuthEventPublisher`

### Adapters de sécurité
• `JwtTokenService`
• `BCryptPasswordHasher`
• `BCryptPasswordVerifier`

### Adapters HTTP
• `AuthController`
• les classes `...Response` dans `infrastructure/http/responses`

---

# 4. Différence entre port et adapter

## Lecture simple
- Le port dit : "voici le contrat attendu".
- L'adapter dit : "voici comment je le fais concrètement".

## Exemple 1 : publication d'événement
### Port
• `AuthEventPublisher`

### Adapter
• `RabbitMqAuthEventPublisher`

### Lecture
Le use case veut publier un événement d'authentification, mais il ne veut pas dépendre directement de RabbitMQ.

Il dépend donc du port `AuthEventPublisher`.

Ensuite, l'infrastructure fournit l'adapter `RabbitMqAuthEventPublisher`, qui connaît RabbitMQ et son fonctionnement technique.

## Exemple 2 : hash de mot de passe
### Port
• `PasswordHasher`

### Adapter
• `BCryptPasswordHasher`

### Lecture
Le use case ne veut pas connaître BCrypt.
Il veut seulement pouvoir transformer un mot de passe brut en hash.

## Exemple 3 : génération de JWT
### Port
• `JwtService`

### Adapter
• `JwtTokenService`

### Lecture
L'application veut générer et valider un token.
Le détail de signature JWT reste caché dans l'adapter.

## Exemple 4 : persistance utilisateur
### Port
• `UserRepository`

### Adapter
• `JpaUserRepository`

### Lecture
Le domaine/application veulent charger ou sauvegarder un utilisateur.
L'implémentation réelle passe par JPA et PostgreSQL, mais ce détail est isolé dans l'adapter.

---

# 5. Les controllers sont-ils aussi des adapters ?

## Réponse courte
Oui, conceptuellement, un controller HTTP est aussi un adapter.

## Pourquoi ?
Parce qu'il relie :
- le monde HTTP entrant ;
- au cœur applicatif.

Le controller :
1. reçoit une requête HTTP ;
2. lit le body, les paramètres ou l'utilisateur courant ;
3. construit une commande ;
4. appelle un use case ;
5. transforme le résultat en réponse HTTP.

Autrement dit :
- il adapte l'entrée HTTP au langage de l'application ;
- puis adapte le résultat de l'application au protocole HTTP.

## Dans Trader App
`AuthController` joue exactement ce rôle.

Il ne devrait pas porter la logique métier.
Son rôle est de faire le pont.

---

# 6. Les repositories JPA sont-ils des adapters ?

## Réponse courte
Oui.

## Pourquoi ?
Parce qu'ils implémentent un contrat métier ou applicatif avec une technologie concrète de persistance.

## Dans Trader App
Quand tu vois :
- un contrat `UserRepository` côté domaine ;
- puis une implémentation JPA côté infrastructure ;

tu es exactement dans le schéma :
- port = contrat ;
- adapter = implémentation.

---

# 7. Comment reconnaître rapidement un port ou un adapter ?

## Indices pour reconnaître un port
- C'est souvent une interface.
- Le nom exprime un besoin métier ou applicatif.
- Il ne contient pas de détail technique.
- Il vit souvent dans `domain` ou `application`.

## Indices pour reconnaître un adapter
- Il dépend d'une techno concrète.
- Il parle à Spring, JPA, RabbitMQ, JWT, BCrypt, HTTP, etc.
- Il implémente souvent une interface.
- Il vit généralement dans `infrastructure`.

## Petit réflexe pratique
Demande-toi :

### Question 1
Le code exprime-t-il un besoin abstrait du système ?
• Si oui, on est probablement sur un port.

### Question 2
Le code branche-t-il une solution technique concrète ?
• Si oui, on est probablement sur un adapter.

---

# 8. Ce que cela change dans la manière de coder

## Quand on ajoute un nouveau besoin métier
Il faut d'abord réfléchir au contrat utile au cœur.

Exemples :
- "j'ai besoin d'envoyer un email"
- "j'ai besoin de publier un événement"
- "j'ai besoin de stocker un agrégat"

La première question n'est pas :
• "Quelle techno vais-je utiliser ?"

La première question est :
• "De quoi l'application a-t-elle besoin ?"

## Ensuite seulement
On choisit et on code l'adapter.

Exemples :
- SMTP ou Resend
- RabbitMQ
- PostgreSQL/JPA
- JWT

---

# 9. Règles pratiques pour Trader App

## Ce qu'il faut viser
- Les use cases dépendent de ports.
- Les détails techniques vivent dans les adapters.
- Les controllers orchestrent l'entrée HTTP sans porter la logique métier.
- Les implémentations techniques sont remplaçables sans casser le cœur.

## Ce qu'il faut éviter
- Injecter directement une classe technique dans un use case si une abstraction métier/applicative est attendue.
- Laisser la couche applicative connaître RabbitMQ, BCrypt ou JPA dans le détail.
- Mettre la logique métier dans les adapters.

---

# 10. Résumé

## À retenir
- Un **port** est un contrat attendu par le domaine ou l'application.
- Un **adapter** est une implémentation concrète de ce contrat, ou un pont avec l'extérieur.

## Lecture finale appliquée au projet
Dans Trader App :
- `UserRepository`, `JwtService`, `PasswordHasher`, `AuthEventPublisher` sont des ports ou des contrats assimilés ;
- `JpaUserRepository`, `JwtTokenService`, `BCryptPasswordHasher`, `RabbitMqAuthEventPublisher`, `AuthController` sont des adapters.

## Conclusion
Oui, l'approche ports et adapters est déjà présente dans le projet.
Elle n'était simplement pas encore nommée explicitement partout.
