# ADR 004: Axios comme client HTTP du front

## Status
- Accepté

## Contexte
Le front mobile doit appeler une API backend JWT, gérer les erreurs HTTP, les timeouts, la configuration centralisée de l'URL de base et l'injection automatique du token.  
Il faut un client HTTP simple à centraliser et cohérent avec une application React Native / Expo qui consomme une API REST.

## Options
- Option 1 : utiliser `fetch` natif
  - Cette option évite une dépendance supplémentaire.
  - Elle suffit pour des appels simples.
  - En revanche, la gestion des erreurs, des intercepteurs, des timeouts et des comportements communs devient plus artisanale.
  - La centralisation de la logique HTTP est moins confortable.

- Option 2 : utiliser Axios
  - Cette option apporte un client HTTP plus complet.
  - Elle simplifie l'ajout d'intercepteurs, de timeouts et de comportements partagés.
  - Elle s'intègre bien à une architecture front avec un point d'entrée HTTP unique.
  - Elle ajoute une dépendance, mais avec un vrai gain de clarté et de confort.

- Option 3 : utiliser une autre abstraction plus lourde
  - Cette option pourrait couvrir des cas plus avancés.
  - Elle introduirait cependant une complexité supplémentaire peu justifiée pour la V1.
  - Elle n'apporte pas un bénéfice assez net par rapport à Axios dans le contexte actuel.

## Décision
Le front utilise Axios comme client HTTP principal.  
Tous les appels API passent par un client partagé capable de centraliser l'URL de base, le bearer token JWT et la gestion des erreurs réseau.

## Conséquences
### Positives (bénéfices)
- La couche HTTP est centralisée.
- L'ajout du token JWT est plus simple à gérer.
- Les timeouts et erreurs peuvent être traités de manière cohérente.
- Le code des features reste plus lisible.

### Négatifs (inconvénients)
- Axios ajoute une dépendance supplémentaire.
- Il faut maintenir une discipline pour éviter les appels HTTP hors du client partagé.

### Impacts futurs
- Les prochains endpoints devront être branchés via le client Axios partagé.
- Les comportements globaux comme la gestion d'erreurs ou des headers communs resteront faciles à faire évoluer.
- L'intégration future d'un login social ou d'autres providers restera cohérente avec l'infrastructure déjà en place.
