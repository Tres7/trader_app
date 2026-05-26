# ADR 003: Architecture front feature based

## Status
- Accepté

## Contexte
Le front mobile doit gérer plusieurs domaines métier : authentification, profil, plan de trading, news, notifications et bilan.  
Une organisation purement technique devient vite difficile à maintenir quand chaque parcours utilisateur combine UI, API, état local et logique métier.  
Il faut donc une structure qui reflète le produit et les domaines fonctionnels.

## Options
- Option 1 : organiser le front par type de fichier
  - Exemple : `components`, `screens`, `services`, `hooks`, `stores`.
  - Cette option est fréquente et simple à démarrer.
  - En revanche, elle disperse vite le code d'une même fonctionnalité dans plusieurs dossiers.
  - À mesure que l'application grandit, les changements deviennent plus coûteux à localiser.

- Option 2 : adopter une structure hybride sans convention forte
  - Cette option laisse beaucoup de liberté au départ.
  - Elle peut sembler pragmatique pour avancer vite.
  - En pratique, elle produit souvent des frontières floues entre code métier et code partagé.
  - La lisibilité et la cohérence baissent avec le temps.

- Option 3 : organiser le front par domaines / features
  - Cette option regroupe ensemble l'UI, les appels API, les types et l'état d'une même fonctionnalité.
  - Elle suit naturellement le produit et le métier.
  - Elle facilite l'évolution incrémentale de chaque module.

## Décision
Le front adopte une architecture par domaines, dite feature based.  
Chaque fonctionnalité regroupe son UI, ses appels réseau, ses types et sa logique locale.  
Les éléments vraiment transverses sont placés dans `shared`.

## Conséquences
### Positives (bénéfices)
- Le code est plus proche du métier.
- Les changements sont plus faciles à localiser.
- La structure reste lisible quand de nouveaux modules apparaissent.
- La séparation entre spécifique métier et partagé est plus claire.

### Négatifs (inconvénients)
- Il faut éviter de dupliquer des éléments qui devraient vivre dans `shared`.
- La frontière entre feature et code partagé demande du jugement.
- Les contributeurs doivent apprendre la convention du projet.

### Impacts futurs
- Les prochains modules pourront être ajoutés comme de nouvelles features.
- Les composants ou helpers réellement transverses pourront être remontés vers `shared`.
- Les tests unitaires pourront eux aussi être organisés par domaine fonctionnel.
