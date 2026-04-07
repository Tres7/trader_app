# ADR 006: RabbitMQ comme message broker

## Status
- Accepté

## Contexte
Le backend est organisé comme un monolithe modulaire avec plusieurs domaines métier qui doivent s'échanger des informations sans couplage fort.  
Certaines actions métier doivent produire des effets asynchrones dans d'autres modules, comme l'envoi d'emails, la propagation d'événements ou l'alimentation de traitements secondaires.  
Il faut donc un mécanisme simple et fiable pour transporter ces événements entre modules, compatible avec l'environnement local Docker Compose.

## Options
- Option 1 : appels synchrones directs entre modules
  - Cette option est simple à comprendre et à tracer au début.
  - Elle évite d'introduire un broker de messages.
  - En revanche, elle renforce le couplage entre modules et rend les traitements transverses plus rigides.
  - Elle s'accorde mal avec une architecture modulaire qui veut garder des frontières propres entre domaines.

- Option 2 : utiliser RabbitMQ comme broker de messages
  - Cette option apporte un vrai mécanisme asynchrone pour transporter des événements métier.
  - Elle est suffisamment légère pour une V1 et simple à lancer dans Docker Compose.
  - Elle permet de mieux découpler les modules tout en gardant une architecture monolithique au niveau du déploiement.
  - Elle correspond bien à des cas comme les notifications, les emails transactionnels ou les projections locales entre domaines.

- Option 3 : utiliser une plateforme de streaming plus lourde
  - Cette option peut être pertinente pour de très gros volumes ou des besoins analytiques plus avancés.
  - En revanche, elle serait disproportionnée pour les besoins actuels de la V1.
  - Elle ajouterait une complexité d'exploitation et de conception inutile à ce stade.

## Décision
RabbitMQ est choisi comme message broker du backend.  
Les modules publient des événements métier et les traitements intéressés les consomment via des adapters d'infrastructure.

## Conséquences
### Positives (bénéfices)
- Les modules sont moins couplés entre eux.
- Les traitements asynchrones sont plus propres à faire évoluer.
- RabbitMQ reste simple à exploiter en environnement local.
- L'architecture devient plus cohérente avec une séparation modulaire orientée événements.

### Négatifs (inconvénients)
- Le débogage des flux asynchrones est plus complexe que des appels directs.
- Il faut gérer la fiabilité des messages et les scénarios de retry.
- La compréhension du système demande de suivre à la fois les flux synchrones et les événements.

### Impacts futurs
- Les nouveaux modules devront publier et consommer des événements de manière explicite.
- Les schémas de messages devront être documentés et stabilisés.
- Si le volume ou la complexité des échanges change fortement, le choix du broker pourra être réévalué, mais RabbitMQ reste adapté à la V1.
