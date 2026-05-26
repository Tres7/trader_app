# Cycle de vie d'un endpoint backend

## Objectif du document
- Expliquer comment un endpoint HTTP est construit dans Trader App.
- Montrer le chemin typique depuis la requête entrante jusqu'à la réponse renvoyée.
- Documenter la convention utilisée dans le projet pour garder les controllers simples.

## Vue d'ensemble
Dans ce projet, un endpoint suit généralement ce cycle :

1. réception de la requête HTTP ;
2. lecture du body ou des paramètres ;
3. création d'un `Command` applicatif ;
4. exécution d'un `UseCase` ;
5. récupération du résultat métier ;
6. construction d'une `Response` HTTP ;
7. renvoi d'un `ResponseEntity`.

Cette séquence garde le controller léger et laisse la logique métier dans la bonne couche.

---

# 1. Étape 1 : réception de la requête HTTP

## Où cela se passe
- dans un controller, par exemple :
  * `infrastructure/http/controllers/AuthController.java`

## Ce que fait le controller à ce stade
- il reçoit la route ;
- il reçoit le body HTTP ;
- il récupère éventuellement l'utilisateur authentifié ;
- il prépare l'appel applicatif.

## Exemple
```java
@PostMapping("/register")
public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {}
```

Ici :
- la route est `POST /api/v1/auth/register` ;
- l'entrée HTTP est mappée vers `RegisterUserRequest`.

---

# 2. Étape 2 : mapping de la requête vers un objet d'entrée applicatif

## Pourquoi cette étape existe
- pour éviter que le use case dépende directement de la couche HTTP ;
- pour transformer le contrat API en intention applicative claire.

## Ce qui est utilisé
- un `Request` côté présentation ;
- un `Command` côté application.

## Exemple
```java
RegisterUserCommand command = new RegisterUserCommand(
    request.firstName(),
    request.lastName(),
    request.birthDate(),
    request.email(),
    request.password(),
    request.country()
);
```

## Ce qu'il faut retenir
- le `Request` décrit la forme HTTP entrante ;
- le `Command` décrit ce que le cas d'usage doit exécuter.

---

# 3. Étape 3 : exécution du use case

## Rôle du use case
Le use case orchestre l'action métier.

Il peut :
- valider les règles métier via les objets du domaine ;
- charger ou sauvegarder des entités ;
- appeler des ports ;
- coordonner les effets secondaires ;
- renvoyer un résultat.

## Exemple
```java
User user = registerUser.execute(command);
```

Ici :
- le controller ne fait pas lui-même l'inscription ;
- il délègue toute l'action métier à `RegisterUser`.

## Exemple de ce qui se passe dans le use case
Dans `RegisterUser` :
- vérification que l'email n'existe pas déjà ;
- hash du mot de passe ;
- création de l'utilisateur ;
- sauvegarde ;
- génération du code de vérification ;
- publication de l'événement d'inscription.

---

# 4. Étape 4 : construction de la réponse HTTP

## Pourquoi cette étape existe
Le résultat métier ne doit pas être renvoyé brut au client.

Il faut :
- choisir les champs exposés ;
- contrôler le contrat API ;
- éviter de renvoyer des objets internes du domaine.

## Exemple
```java
RegisterUserResponse response = new RegisterUserResponse(
    user.getId().value().toString(),
    user.getEmail().value(),
    user.isEmailVerified(),
    "User registered successfully. Email sent"
);
```

## Ce qu'il faut retenir
- l'objet `Response` représente ce que l'API expose ;
- il ne doit contenir que les données utiles au client.

---

# 5. Étape 5 : renvoi du `ResponseEntity`

## Rôle
Le controller termine en renvoyant explicitement :
- le code HTTP ;
- le body de réponse.

## Exemple
```java
return ResponseEntity.status(HttpStatus.CREATED).body(response);
```

## Pourquoi c'est utile
- le statut HTTP reste explicite ;
- la réponse est claire et contrôlée ;
- la couche HTTP garde son rôle de transport.

---

# 6. Exemple complet résumé

## Exemple sur `register`
Le flux complet est :

- `RegisterUserRequest`
  * objet d'entrée HTTP ;
- `RegisterUserCommand`
  * objet d'entrée applicatif ;
- `registerUser.execute(command)`
  * exécution du use case ;
- `RegisterUserResponse`
  * objet de sortie HTTP ;
- `ResponseEntity.status(...).body(response)`
  * réponse finale envoyée au client.

---

# 7. Cas des endpoints authentifiés

## Particularité
Certains endpoints ne reçoivent pas seulement un body.

Ils reçoivent aussi l'utilisateur courant via :
```java
@AuthenticationPrincipal AuthenticatedUser authenticatedUser
```

## Exemple
```java
@GetMapping("/me")
public ResponseEntity<GetCurrentUserResponse> me(
    @AuthenticationPrincipal AuthenticatedUser authenticatedUser
) {
```

Ici, le cycle reste le même :
- récupération de l'entrée ;
- appel du use case ;
- construction de la réponse ;
- renvoi HTTP.

La seule différence est que l'identité utilisateur vient du contexte de sécurité.

---

# 8. Ce qu'un controller ne doit pas faire

## À éviter
- implémenter la logique métier ;
- accéder directement aux repositories si un use case existe ;
- faire du calcul métier complexe ;
- appeler directement la base ou RabbitMQ ;
- construire des règles de validation métier profondes.

## Pourquoi
Parce que sinon :
- le controller devient difficile à maintenir ;
- la logique métier se disperse ;
- les tests deviennent plus compliqués ;
- l'architecture perd sa cohérence.

---

# 9. Ce que le use case ne doit pas faire

## À éviter
- manipuler `ResponseEntity` ;
- dépendre d'annotations Spring Web ;
- connaître la forme exacte du JSON entrant ;
- se comporter comme un controller déguisé.

## Pourquoi
Le use case doit rester dans la couche application et parler le langage métier, pas le langage HTTP.

---

# 10. Convention pratique à suivre dans ce projet

## Convention recommandée
Pour chaque endpoint :

- un `Request` décrit l'entrée HTTP ;
- un `Command` transporte l'intention vers l'application ;
- un `UseCase` exécute la logique ;
- un `Response` expose la sortie HTTP ;
- le controller fait uniquement l'orchestration de surface.

## Résultat attendu
Cette convention permet :
- des controllers courts ;
- des cas d'usage lisibles ;
- un domaine protégé ;
- une API plus facile à faire évoluer.

---

# 11. Résumé

## En une phrase
Dans Trader App, un endpoint backend ne porte pas la logique métier : il reçoit la requête, crée un command, exécute un use case, construit une response et renvoie la réponse HTTP.

## À retenir
- `Request` = contrat HTTP entrant
- `Command` = intention applicative
- `UseCase` = orchestration métier
- `Response` = contrat HTTP sortant
- `ResponseEntity` = réponse finale transportée au client
