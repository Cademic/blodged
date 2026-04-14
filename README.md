# Blodged

A social feed for coders: posts, replies, likes, and follows, with a Vue 3 SPA and a Spring Boot API. This project continues work from **CST-339 (Java III)** at Grand Canyon University, evolving toward a communication-focused experience with a modern frontend and PostgreSQL-backed persistence.

---

## Features

- **Accounts** — Registration, login (JWT-style token in the client), profiles, optional private profiles, follow/unfollow.
- **Posts** — Create, edit, delete; feed and per-user listings; like/unlike.
- **Replies** — Threaded replies on posts with CRUD where supported by the API.
- **Admin & settings** — Routed areas for administration and account settings (see app routes).
- **API docs** — OpenAPI/Swagger for REST controllers under `/api/**`.

---

## Tech stack

| Layer | Technologies |
|--------|----------------|
| **Frontend** | Vue 3 (Composition API, `<script setup>`), TypeScript, Vite, Vue Router, Pinia |
| **Backend** | Java 17, Spring Boot 3.3, Spring Web, Spring Security, Spring JDBC, Thymeleaf (legacy/server pages alongside the API) |
| **Database** | PostgreSQL 16 |
| **Docs** | springdoc-openapi (Swagger UI) |
| **Containers** | Docker Compose (Postgres + backend image) |

---

## Repository layout

```
blodged/
├── Source/
│   ├── Frontend/          # Vite + Vue app (`npm run dev`)
│   └── Backend/           # Spring Boot (`mvn spring-boot:run` or Docker)
├── docker-compose.yml     # Postgres + backend
├── Documents/
│   ├── Requirements/      # User stories & functional/non-functional requirements
│   └── Images/            # Brand assets
└── README.md
```

Key frontend areas: `src/pages/` (routes), `src/components/`, `src/api/` (HTTP client), `src/store/`, `src/router/`.  
Key backend areas: `controller/` (MVC + REST), `controller/REST/`, `config/`, `data/`, `business/`.

---

## Prerequisites

- **Docker-only workflow:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose plugin).
- **Local full stack without Docker:**
  - **Backend:** JDK 17, Apache Maven, PostgreSQL reachable at the URL in `application.properties` (default: `localhost:5432`, database/user/password `blodged`).
  - **Frontend:** Node.js (LTS recommended) and npm.

---

## Quick start (Docker)

From the **repository root**:

```bash
docker compose up --build
```

This starts:

| Service | Port | Notes |
|---------|------|--------|
| PostgreSQL | `5432` | DB, user, and password: `blodged` |
| Spring Boot | `8080` | REST API and legacy web routes |

- **Swagger UI:** [http://localhost:8080/swagger.html](http://localhost:8080/swagger.html) — documents REST controllers under `com.blodged.controller.REST` for paths `/api/**`.

The first time Postgres initializes, `Source/Backend/docker/postgres/init.sql` creates tables and inserts **sample users and posts** for local testing.

To stop: `Ctrl+C` in the terminal, or `docker compose down` (add `-v` to remove the named volume and reset the DB).

---

## Frontend development

The Vite dev server proxies API traffic to the backend on port **8080**, so run the backend (Docker or local) first.

```bash
cd Source/Frontend
npm install
npm run dev
```

By default the app is served by Vite (typically [http://localhost:5173](http://localhost:5173)). Proxied paths include `/api`, `/accounts`, `/like`, `/unlike`, and `/replies` (see `vite.config.ts`).

**Production build:**

```bash
npm run build
npm run preview   # optional local preview of the build
```

---

## Backend without Docker

1. Start PostgreSQL and ensure a database named `blodged` exists with credentials matching `application.properties` (or set `SPRING_DATASOURCE_*` env vars).
2. Optionally run `Source/Backend/docker/postgres/init.sql` once to create schema and seed data.
3. From `Source/Backend`:

```bash
mvn spring-boot:run
```

Or package and run the JAR:

```bash
mvn -DskipTests package
java -jar target/backend.jar
```

---

## Configuration

| Concern | Location / notes |
|---------|------------------|
| Datasource | `Source/Backend/src/main/resources/application.properties` — defaults to `jdbc:postgresql://localhost:5432/blodged` with user/password `blodged`; overridden in Compose via `SPRING_DATASOURCE_*`. |
| CORS | `app.cors.allowed-origin-patterns` — default allows `http://localhost:*` and `http://127.0.0.1:*` for local dev (`WebConfig.java`). |
| Sessions | Servlet session timeout and cookie flags in `application.properties` (used where session-based flows apply). |
| OpenAPI | `springdoc.swagger-ui.path=/swagger.html`; scans `com.blodged.controller.REST` for `/api/**`. |

---

## Database

- **Schema** — Users, follows, posts, likes, replies (see `init.sql` and `Documents/SQL/blodged.ddl`).
- **Seed data** — Demo users (e.g. `alice`, `mike`, `sophia`, `jordan`, `admin`) with documented passwords in `init.sql` **for local development only**. Change or remove these in any shared or production environment.

---

## Requirements & documentation

- [User stories](Documents/Requirements/User-Stories.md)
- [Functional requirements](Documents/Requirements/FunctionalRequirements.md)
- [Non-functional requirements](Documents/Requirements/NonFunctionalRequirements.md)

---

## Roadmap (from project goals)

- Continue polishing the UI toward a distinct “coder community” look.
- Modal-based login/register on the home experience (optional UX change).
- Deploy the database (and app) to a cloud provider when ready.
- Broader features (e.g. daily coding challenges, richer real-time updates) as the stack matures.

---

![Blodged Logo](Documents/Images/Logo/Blodged_Trans.png)
