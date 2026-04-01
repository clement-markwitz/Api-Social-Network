# 🌐 Social Network API (Backend)

Il s'agit de l'API REST robuste et scalable . Ce backend gère la logique métier complexe, l'authentification sécurisée, les interactions sociales et un système de recommandation intelligent.

## 🚀 Stack Technique

* **Framework Principal :** [Spring Boot 3](https://spring.io/projects/spring-boot) (Java 21).
* **Sécurité :** [Spring Security](https://spring.io/projects/spring-security) avec implémentation de stateless **JWT** (JSON Web Tokens).
* **Bases de Données :**
    * **MongoDB :** Stockage des données utilisateur, des posts et des messages.
    * **Neo4j :** Moteur de graphe utilisé pour modéliser les relations complexes (follows, intérêts) et générer des recommandations.
* **Temps Réel :** [WebSockets](https://spring.io/guides/gs/messaging-stomp-websocket/) avec protocole STOMP pour la messagerie instantanée.
* **Tests & Qualité :** JUnit 5, [Jacoco](https://www.jacoco.org/jacoco/) pour la couverture de code, et [JMeter](https://jmeter.apache.org/) pour les tests de performance.
* **DevOps :** Docker Compose pour l'orchestration des services (API, Mongo, Neo4j) et déploiement via [Nomad](https://www.nomadproject.io/).

## ✨ Fonctionnalités Backend

* **Gestion des Utilisateurs & Profils :** Système complet d'inscription, connexion, et mise à jour de profils avec gestion des intérêts.
* **Système Social Avancé :** * Gestion des relations de type "Follow".
    * Création et gestion de communautés et de pages dédiées.
    * Système de blocage et de bannissement pour la modération.
* **Interactions & Contenu :** Publication de posts (textes, médias), gestion des réactions (likes) et commentaires.
* **Moteur de Recommandation :** Algorithmes basés sur le graphe Neo4j pour suggérer des amis, des pages ou des posts pertinents selon les intérêts de l'utilisateur.
* **Messagerie Privée :** Gestion des conversations et des messages en temps réel avec accusés de réception.
* **Statistiques :** Endpoint dédié à l'extraction de métriques sur l'activité de la plateforme.

## 🛠 Architecture du Code

Le projet suit les principes de la **Clean Architecture** :
* **Controllers :** Exposent les endpoints REST et gèrent les requêtes HTTP.
* **Services :** Contiennent toute la logique métier (validation, calculs, recommandations).
* **Repositories :** Interfaces d'accès aux données (Spring Data MongoDB & Neo4j).
* **DTOs & Mappers :** Assurent le transfert de données propre entre les couches et l'API.

## 📦 Installation et Déploiement

### Avec Docker (Recommandé)
Le projet inclut un fichier `docker-compose.yml` qui configure l'API et ses dépendances :
```bash
docker-compose up --build
```

### Développement (Gradle)
```bash
./gradlew bootRun
```

---

### 💡 Note pour le recruteur
Ce projet met en évidence ma capacité à concevoir une architecture **multi-bases de données** (Polyglot Persistence), à sécuriser une API avec les standards industriels (**JWT**) et à intégrer des fonctionnalités de **recommandation** basées sur la théorie des graphes.
