TMDB Scala Project
------------------

Pierre-Alexandre MARTIN et
Mathias NOCET <br>
Utilisation de Chat GPT 4 et GitHub Copilot

Ce projet est une application Scala qui interagit avec l'API TMDB pour obtenir des informations sur les acteurs et les films.

Configuration:
- Scala version 3.3.1
- Utilise sbt pour la gestion du projet
- Dépendances : json4s pour le parsing JSON

Instructions de démarrage:
1. Clonez le projet et ouvrez-le dans votre IDE Scala.
2. Assurez-vous que sbt est correctement configuré.
3. Exécutez `sbt run` pour lancer l'application.

Choix de conception:
- Architecture orientée objet pour la représentation des acteurs et des films.
- Utilisation de la programmation fonctionnelle pour la manipulation des données.
- Mise en cache des données pour optimiser les performances et réduire les requêtes API.
- La mise en cache se déroule sur 2 niveaux, un niveau "flash memory" et un niveau "Hard disk".
-- Le niveau "flash memory" est utilisé pour stocker les données en mémoire vive.
-- Le niveau "Hard disk" est utilisé pour stocker les données sur le disque dur.

Commentaires sur la section 5:
- L'utilisation des classes `Actor` et `Movie` permet une meilleure organisation et lisibilité du code.
- Le passage d'une architecture fonctionnelle à une architecture orientée objet offre une meilleure modélisation des entités.

