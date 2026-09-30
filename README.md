# Edinar Control Backend

Backend service for the **Edinar Control** platform, built with Spring Boot and Java 21.

---

## 📁 Project Architecture

```
edinar-control-backend/
│
├── src/
│   ├── main/
│   │   ├── java/com/edinar/control/
│   │   │   ├── config/          # Spring & application configuration classes
│   │   │   ├── controller/      # REST API Controllers (endpoints)
│   │   │   ├── domain/          # Entities & domain models
│   │   │   ├── dto/             # Data Transfer Objects (request/response)
│   │   │   ├── exception/       # Custom exceptions & global exception handlers
│   │   │   ├── mapper/          # Entity-DTO mappers (e.g., MapStruct)
│   │   │   ├── service/         # Business logic layer & interfaces
│   │   │   └── ControlBackendApplication.java
│   │   │
│   │   └── resources/
│   │       ├── db/migration/    # Flyway SQL migration scripts
│   │       ├── mapper/          # XML query mapper files
│   │       └── application.yml  # Application configuration file
│   │
│   └── test/                    # Unit and integration tests
│
├── docker-compose.yml           # PostgreSQL 17 local container definition
├── pom.xml                      # Maven dependencies and build definition
└── README.md                    # Project documentation
```

---

## 🛠️ Tech Stack & Prerequisites

- **Java**: 21 LTS
- **Framework**: Spring Boot
- **Database**: PostgreSQL 17 (Docker)
- **Migration**: Flyway
- **Build Tool**: Maven (`./mvnw`)

---

## ⚙️ Configuration

Application settings are located in `src/main/resources/application.yml`.

### Environment Variables

| Variable | Description | Default                                           |
| :--- | :--- |:--------------------------------------------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:port/edinar_control` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `<your-postgres-username>`                        |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `<your-postgres-password>`                        |
| `PORT` / `SERVER_PORT` | Server HTTP port | `port`                                            |
| `JWT_SECRET` | Secret HMAC du JWT (obligatoire en production) | valeur de développement                           |
| `JWT_EXPIRATION_MS` | Durée de validité du JWT en millisecondes | `86400000`                                        |

Profils disponibles : `dev` pour la base PostgreSQL locale et `prod` pour des identifiants fournis par variables d'environnement.

---

## 🐳 Running with Docker

Démarrer la base de données PostgreSQL 17 :

```bash
docker compose up -d
```

## 🚀 Getting Started

Endpoints principaux :

- `POST /api/auth/register`, `POST /api/auth/login`
- `GET /api/dashboard/summary`, `GET /api/cards/{id}/summary`
- `POST /api/cards/internal-transfers`
- `POST /api/cards/{id}/reconcile`
- `GET /api/cards/{id}/transactions?page=0&size=50&type=SEND&isVoid=false`
- `POST /api/cards/{id}/transactions/{transactionId}/void`

Toutes les routes `/api` hors authentification et documentation Swagger exigent `Authorization: Bearer <token>`.

### Build the project

```bash
./mvnw clean package
```

### Run the application

```bash
./mvnw spring-boot:run
```

### Run tests

```bash
./mvnw test
```
