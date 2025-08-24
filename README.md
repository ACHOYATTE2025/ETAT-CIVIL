# 🏛️ Gestion d'État Civil Multi-Tenant

**Plateforme SaaS de gestion d'état civil** développée avec **Spring Boot** et **MySQL**, conçue pour permettre à plusieurs organisations (villes, mairies, institutions) de gérer leurs actes d'état civil en toute sécurité sur une même infrastructure.

---

## 📋 Présentation

Ce projet propose un système complet pour :

- Gérer les actes d'état civil (naissance, mariage, décès)
- Supporter plusieurs organisations sur une seule application (**multi-tenant**)
- Assurer l'authentification et la gestion des utilisateurs avec **JWT**
- Gérer les rôles (Administrateur, Utilisateur)
- Permettre l'abonnement et le paiement 
- Fournir un tableau de bord administrateur avec statistiques

---

## ✨ Fonctionnalités principales

- 🔒 Authentification et autorisation sécurisées (JWT)
- 🏢 Gestion multi-organisations (chaque organisation gère ses propres données)
- 📄 Gestion des documents d'état civil (création, consultation, édition)
- 👥 Gestion des rôles utilisateurs
- 💳 Gestion des abonnements et paiements 
- 📊 Tableau de bord avec indicateurs clés : activité, paiements, utilisateurs
- 🔗 API RESTful pour intégration externe
- 🛡️ Séparation stricte des données par organisation

---

## 🛠️ Technologies utilisées

- **Spring Boot** (backend)
- **Spring Security** (authentification double facteur /autorisation) | Token - Jwt 
- **Spring Data JPA** (accès aux données)
- **MySQL** (base de données relationnelle)
- **Paystack API** (paiement en ligne)
- **Maven** (gestion de projet)
- **Docker** (déploiement — optionnel)

---

## 🚀 Installation et lancement

### Prérequis

- Java 17 ou plus
- Maven
- MySQL


### Étapes

1. **Cloner le dépôt :**

   
   git clone https://github.com/votre-utilisateur/etat-civil.git
   cd etat-civil
   
3. ** Créer la base de données PostgreSQL **
   CREATE DATABASE GestionEtatCivil;

4. ** Lancer l’application **
   ./mvnw spring-boot:run

5. ** Accéder à l'API via **
   http://localhost:8080/swagger-ui/index.html
---
### 🗺️ Structure du projet
ETAT-CIVIL
├── src/main/java/
│   ├── com.saasdemo/backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
|   |   ├── enums/
│   │   ├── repository/
│   │   ├── security/
|   |   ├── service/
│   │   └── util/
├── src/main/resources/
│   ├── application.properties
│   └── ...
└── pom.xml
---

### 🔥 Fonctionnalités prévues
Génération automatique de documents PDF (certificats de naissance, mariage, décès)

Notifications par e-mail aux utilisateurs

Interface web utilisateur (Frontend Angular ou React)

Support multi-devises pour les paiements

Tableau de Bord interactif 

Historique et suivi des moidifications d'acte

---

### 👨‍💻 Auteur
**ACHO YATTE DEIVY CONSTANT   | acho.quebec@gmail.com  | www.linkedin.com/in/yatté-deivy-constant-acho-04364a185**



### 📄 Licence
Ce projet est sous licence MIT.

### 🎯 Contributions
Les contributions sont les bienvenues !
Merci de soumettre une issue ou une pull request pour toute amélioration ou correction.

