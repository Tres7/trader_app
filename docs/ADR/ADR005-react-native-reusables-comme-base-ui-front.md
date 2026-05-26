# ADR 005: react-native-reusables comme base UI du front

## Status
- Accepté

## Contexte
Le front mobile doit être développé rapidement tout en gardant une interface cohérente, réutilisable et maintenable.  
L'application comporte déjà plusieurs écrans et parcours : authentification, profil, sécurité, navigation connectée et écrans à venir pour les autres modules métier.  
Il faut donc une base de composants adaptée à Expo et React Native, qui permette d'accélérer la construction de l'interface sans repartir de zéro à chaque écran.

## Options
- Option 1 : construire tous les composants UI en interne dès le départ
  - Cette option donne un contrôle total sur le design system.
  - Elle évite une dépendance supplémentaire.
  - En revanche, elle demande un effort important dès le début pour construire, fiabiliser et homogénéiser tous les composants de base.
  - Elle ralentit la livraison initiale et augmente le risque d'incohérences visuelles entre écrans.

- Option 2 : utiliser une base de composants réutilisables adaptée à React Native
  - Cette option permet de partir d'une fondation déjà cohérente.
  - Elle accélère la mise en place des écrans tout en restant personnalisable.
  - Elle réduit le coût de création des primitives de base comme les boutons, inputs, cards, labels ou separators.
  - Elle correspond bien à un projet mobile Expo qui veut avancer vite sans négliger la structure UI.

- Option 3 : utiliser une bibliothèque UI plus lourde ou plus prescriptive
  - Cette option peut apporter davantage de composants prêts à l'emploi.
  - En revanche, elle peut imposer des conventions visuelles plus rigides ou une couche d'abstraction plus lourde.
  - Pour Trader App V1, cela introduirait une complexité peu justifiée.

## Décision
Le front s'appuie sur react-native-reusables comme base UI.  
Les écrans de l'application s'appuient prioritairement sur ses primitives et sur les composants partagés construits au-dessus, afin de garder une interface cohérente et un développement rapide.

## Conséquences
### Positives (bénéfices)
- La construction des écrans est plus rapide.
- La cohérence visuelle de l'application est plus facile à maintenir.
- Les composants de base sont centralisés et réutilisables.
- Le projet évite de réinventer toute une base UI dès la V1.

### Négatifs (inconvénients)
- Il faut apprendre et respecter les conventions de la base choisie.
- Certains ajustements visuels peuvent demander de bien comprendre les primitives et leur composition.
- La dépendance à une base externe impose de rester attentif à sa compatibilité avec le projet.

### Impacts futurs
- Les nouveaux écrans devront privilégier les primitives UI déjà posées.
- Les composants spécifiques au produit pourront être construits par-dessus cette base commune.
- Si le design system évolue, il restera plus facile à faire converger sur un socle partagé déjà structuré.
