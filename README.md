# TaskManager - Documentation Technique & CI/CD Pipeline

Bienvenue dans la documentation officielle du projet **TaskManager**. Ce document résume les réponses à l'épreuve de mise en place de pipeline CI/CD et de développement d'application web.

---

## QUESTION 1 – CONCEPTION ARCHITECTURALE ET MODÉLISATION

### 1. Reformulation du besoin fonctionnel

* **Acteurs** :
  * **Utilisateur standard** : Peut s'inscrire, se connecter, et gérer ses propres tâches.
  * *(Optionnel)* **Administrateur** : Peut gérer l'ensemble des utilisateurs et données générales.
* **Principales fonctionnalités** :
  * Authentification (JWT) sécurisée.
  * Gestion complète (CRUD) des tâches personnelles.
  * Validation stricte des données et gestion centralisée des erreurs.
  * Pagination des résultats.
* **Contraintes techniques** :
  * Application backend développée en **Spring Boot** (Java).
  * Base de données relationnelle.
  * TDD avec tests automatisés (> 60% de couverture).
  * SonarQube et pipeline CI/CD complet.

### 2. Modélisation des données

La modélisation de la base de données s'appuie sur deux tables principales :

* **`users`** :
  * `id` (Clé Primaire, UUID ou Auto Increment)
  * `username` / `email` (Unique)
  * `password` (Haché)
  * `role` (ex: USER, ADMIN)
  * `created_at`, `updated_at`
* **`tasks`** :
  * `id` (Clé Primaire)
  * `user_id` (Clé Étrangère vers `users.id`, avec contrainte `ON DELETE CASCADE`)
  * `title` (Obligatoire)
  * `description`
  * `status` (Enum: PENDING, IN_PROGRESS, COMPLETED)
  * `due_date`
  * `created_at`, `updated_at`

### 3. Structure REST de l'API

| Endpoint | Méthode | Description | Code Succès | Code Erreur |
|----------|---------|-------------|-------------|-------------|
| `/api/v1/auth/register` | POST | Création de compte | 201 | 400 (Validation) |
| `/api/v1/auth/login` | POST | Connexion (récup. du Token) | 200 | 401 (Unauthorized) |
| `/api/v1/tasks` | GET | Lister de ses tâches (paginé) | 200 | 401 |
| `/api/v1/tasks` | POST | Créer une tâche | 201 | 400 (Validation) |
| `/api/v1/tasks/{id}` | GET | Récupérer une tâche | 200 | 404 (Not Found), 403 (Forbidden) |
| `/api/v1/tasks/{id}` | PUT | Mettre à jour une tâche | 200 | 404, 400, 403 |
| `/api/v1/tasks/{id}` | DELETE| Supprimer une tâche | 204 | 404, 403 |

> *Note : Les erreurs respectent une structure JSON unifiée via un `@ControllerAdvice` (`status`, `message`, `errors`).*

### 4. Architecture Applicative (Spring Boot)

L'application respecte les principes de la **Clean Architecture** (Architecture en couches) et **SOLID** :

1. **Controllers** (`@RestController`) : C'est le point d'entrée. Réceptionnent les appels HTTP, font appel aux Services, et formattent la réponse JSON via des DTOs (Data Transfer Objects).
2. **Services** (`@Service`) : Rassemblent la logique métier (validation complexe, association d'une tâche à un utilisateur). N'écrivent pas directement en base de données, mais font appel aux Repositories.
3. **Repositories** (`@Repository` - Spring Data JPA) : Gèrent l'accès aux données physiques.
4. **Exceptions** : Un gestionnaire global centralisé avec `@ExceptionHandler` permet de ne pas dupliquer la logique d'interception d'erreurs (Validation, NonTrouvé, AccèsRefusé) dans les contrôleurs.

---

*(Les questions 3, 4, 5 et 6 seront ajoutées progressivement au fil de l'implémentation du pipeline et de l'automatisation de déploiement).*
