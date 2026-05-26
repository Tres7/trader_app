# ADR 001: Monolithe modulaire pour le backend

## Status
- Accepté

## Contexte
Trader App doit couvrir plusieurs domaines métier dès la V1 : authentification, plan de trading, news, notifications et bilan hebdomadaire.  
Le projet doit rester rapide à développer, simple à lancer en dev et raisonnable à maintenir avec une petite équipe.  
En même temps, il faut éviter qu'un backend trop centralisé devienne difficile à faire évoluer quand de nouveaux modules apparaîtront.

## Options
- Option 1 : partir sur des microservices dès le début
  - Cette option permet une séparation forte des domaines.
  - Elle facilite théoriquement la scalabilité indépendante de chaque service.
  - En revanche, elle ajoute immédiatement de la complexité opérationnelle : déploiements multiples, communication réseau, observabilité, contrats inter-services, résilience distribuée.
  - Pour une V1, le coût est trop élevé par rapport au besoin réel.

- Option 2 : construire un monolithe classique sans frontières internes claires
  - Cette option est rapide à démarrer.
  - Elle réduit les efforts d'architecture au début.
  - En revanche, elle favorise rapidement le couplage entre fonctionnalités.
  - À mesure que le produit grandit, les responsabilités se mélangent et la maintenance devient plus coûteuse.

- Option 3 : construire un monolithe modulaire
  - Cette option conserve la simplicité de déploiement d'une seule application.
  - Elle impose en parallèle de vraies frontières métier internes.
  - Elle permet de structurer le code par module sans payer le coût immédiat des microservices.

## Décision
Le backend sera construit comme un monolithe modulaire.  
L'application restera déployée comme un seul service, mais organisée en modules métier distincts, chacun avec ses propres responsabilités et limites explicites.

## Conséquences
### Positives (bénéfices)
- Déploiement et exploitation plus simples en V1.
- Complexité opérationnelle bien plus faible qu'une architecture distribuée.
- Séparation métier plus saine qu'un monolithe non structuré.
- Base solide pour faire évoluer l'application sans tout réorganiser plus tard.

### Négatifs (inconvénients)
- Une mauvaise discipline peut recréer du couplage entre modules.
- La scalabilité reste attachée à un déploiement unique.
- Les frontières entre modules doivent être respectées activement dans le code.

### Impacts futurs
- Les nouveaux modules devront suivre les mêmes conventions de découpage.
- Les dépendances inter-modules devront être surveillées avec soin.
- Une migration vers des microservices restera possible plus tard si elle devient réellement justifiée.
