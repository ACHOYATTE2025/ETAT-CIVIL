##🏛️ Gestion d'État Civil Multi-Tenant
Plateforme SaaS de gestion d'état civil développée avec Spring Boot et PostgreSQL, conçue pour permettre à plusieurs organisations (villes, mairies, institutions) de gérer leurs actes d'état civil en toute sécurité sur une même infrastructure.

##📋 Présentation
Ce projet propose un système complet pour :

Gérer les actes d'état civil (naissance, mariage, décès)

Supporter plusieurs organisations sur une seule application (multi-tenant)

Assurer l'authentification et la gestion des utilisateurs avec JWT

Gérer les rôles (Administrateur, Utilisateur)

Permettre l'abonnement et le paiement via Paystack

Fournir un tableau de bord administrateur avec statistiques
---
##✨ Fonctionnalités principales
🔒 Authentification et autorisation sécurisées (JWT)

🏢 Gestion multi-organisations (chaque organisation gère ses propres données)

📄 Gestion des documents d'état civil (création, consultation, édition)

👥 Gestion des rôles utilisateurs

💳 Gestion des abonnements et paiements via Paystack

📊 Tableau de bord avec indicateurs clés : activité, paiements, utilisateurs

🔗 API RESTful pour intégration externe

🛡️ Sécurité avancée : séparation stricte des données par organisation
---
##🛠️ Technologies utilisées
Spring Boot (backend)

Spring Security (authentification/autorisation)

Spring Data JPA (accès aux données)

PostgreSQL (base de données relationnelle)

Paystack API (paiement en ligne)

Maven (gestion de projet)

Docker (déploiement — optionnel)
---
#🚀 Installation et lancement
Prérequis
Java 17 ou plus

Maven

PostgreSQL
---
##Étapes pour cloner le dépôt :
git clone https://github.com/votre-utilisateur/etat-civil.git
cd etat-civil

#Lancer l'application
./mvnw spring-boot:run

#Accédez à l'API via :
http://localhost:8080/api

#📚 Documentation API
La documentation de l’API sera disponible via Swagger :
http://localhost:8080/swagger-ui/index.html
---
##🗺️ Structure du projet
css
Copy
Edit
gestion-etat-civil-multitenant/
├── src/main/java/
│   ├── com.example.civilstatus/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
├── src/main/resources/
│   ├── application.properties
│   └── ...
└── pom.xml

#🔥 Fonctionnalités prévues
Génération de documents PDF (certificats de naissance, mariage, décès)

Notifications par email aux utilisateurs

Interface web utilisateur (Frontend Angular ou React)

Support multi-devises pour les paiements

Historique et suivi des modifications d’actes
---
##👨‍💻 Auteur
ACHO YATTE DEIVY CONSTANT

www.linkedin.com/in/yatté-deivy-constant-acho-04364a185 | acho.quebec@gmail.com

📄 Licence
Ce projet est sous licence MIT.

🎯 Contributions
Les contributions sont les bienvenues !
Merci de soumettre une issue ou une pull request pour toute amélioration ou correction.

