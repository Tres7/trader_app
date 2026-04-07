# ADR 002: Architecture hexagonale pour le backend

## Status
- Accepté

## Contexte
Le backend doit protéger la logique métier des détails techniques comme HTTP, JWT, PostgreSQL, RabbitMQ ou l'envoi d'emails.  
Le projet manipule plusieurs intégrations externes et plusieurs cas d'usage métier.  
Il faut donc une structure qui garde le domaine stable, testable et peu dépendant des frameworks.

## Options
- Option 1 : architecture MVC classique orientée framework
  - Cette option est simple à comprendre et rapide à produire.
  - Elle colle bien à l'organisation naturelle de Spring Boot.
  - En revanche, elle fait souvent remonter les détails du framework jusque dans la logique métier.
  - Les tests métier deviennent plus difficiles et les dépendances techniques s'infiltrent partout.

- Option 2 : architecture en couches classiques service / repository / controller
  - Cette option améliore un peu la séparation des responsabilités.
  - Elle reste assez répandue et facile à onboarder.
  - En pratique, elle finit souvent par mélanger logique applicative, persistance et décisions métier.
  - Le domaine reste encore trop dépendant de l'infrastructure.

- Option 3 : architecture hexagonale avec ports et adapters
  - Cette option isole mieux le domaine et l'application.
  - Les dépendances techniques passent par des ports et des adapters.
  - Elle facilite les tests, le remplacement d'une implémentation technique et la lisibilité du métier.
  - Elle demande davantage de rigueur et de structure.

## Décision
Le backend adopte une architecture hexagonale.  
Chaque module s'organise autour de couches claires : `domain`, `application`, `infrastructure` et `presentation`.  
Les interactions techniques passent par des ports et adapters pour préserver le cœur métier.

## Conséquences
### Positives (bénéfices)
- La logique métier reste mieux isolée.
- Les cas d'usage sont plus faciles à tester.
- Les détails techniques sont mieux encapsulés.
- Les modules restent plus lisibles et plus évolutifs.

### Négatifs (inconvénients)
- L'architecture introduit plus de fichiers et plus de formalisme.
- La courbe d'apprentissage est un peu plus élevée.
- Une mauvaise application du pattern peut créer de la complexité inutile sur des cas simples.

### Impacts futurs
- Les nouvelles intégrations devront passer par des ports explicites.
- Les tests unitaires pourront se concentrer sur `domain` et `application`.
- Les évolutions comme Google sign-in, de nouveaux fournisseurs externes ou de nouveaux canaux de notification seront plus faciles à intégrer proprement.
