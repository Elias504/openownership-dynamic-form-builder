# Open Ownership — Dynamic Form Builder

A configuration-driven engine for defining, rendering, validating, and storing dynamic forms. Workspace owners build forms in a drag-and-drop-free builder, respondents fill them in on a submission page, and all responses (including file attachments stored in MinIO) are persisted and viewable in the same UI.

> **New to the app?** See the [User guide (HOWTO.md)](HOWTO.md) for a walkthrough of workspaces, forms, the builder, and submissions.

> **Live demo:** The app is deployed and available at http://167.233.195.210

---

## Contents

- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Running with Docker Compose](#running-with-docker-compose)
- [Running locally without Docker](#running-locally-without-docker)
- [Environment variables](#environment-variables)
- [Project structure](#project-structure)
- [API reference](#api-reference)
- [Testing](#testing)
- [Code quality](#code-quality)
- [CI/CD](#cicd)
- [Built with Claude Code](#built-with-claude-code)

---

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.1.0 · Kotlin 2.3.21 · Java 23 · JPA / Hibernate |
| Database | PostgreSQL 17 |
| File storage | MinIO (S3-compatible object store) |
| Frontend | React 19 · TypeScript · Vite · react-hook-form · Zustand · react-router-dom v7 |
| E2E tests | Playwright 1.61 |
| Container | Docker Compose |

---

## Prerequisites

| Tool | Minimum version |
|---|---|
| Docker + Docker Compose | Docker 24, Compose v2 |
| Java (for local backend dev) | 23 |
| Node.js (for local frontend dev) | 20 |

---

## Running with Docker Compose

This is the recommended way to run the full stack.

**1. Copy the environment file**

```bash
cp .env.example .env
```

The defaults work out of the box. Edit `.env` if you need different ports or credentials.

**2. Start all services**

```bash
docker compose up -d
```

This starts four containers:

| Service | Port | Description |
|---|---|---|
| `db` | 5432 | PostgreSQL database |
| `minio` | 9000 / 9001 | Object storage (API / console) |
| `backend` | 8081 | Spring Boot REST API |
| `frontend` | 3000 | React SPA served via nginx |

**3. Open the app**

- App: http://localhost:3000
- MinIO console: http://localhost:9001 (credentials from `.env`: `minioadmin` / `minioadmin`)

**4. Stop**

```bash
docker compose down
```

Data is persisted in named Docker volumes (`postgres_data`, `minio_data`) and survives restarts.

---

## Running locally without Docker

### Backend

The backend requires a running PostgreSQL instance and MinIO instance.

```bash
# Start only the infrastructure services
docker compose up -d db minio

# Run the backend
cd backend
./mvnw spring-boot:run
```

The backend reads `src/main/resources/application.yaml`. Override any value with an environment variable — for example, to point at a different database:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydb ./mvnw spring-boot:run
```

### Frontend

The frontend Vite dev server proxies `/api` requests to the backend.

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

> **Note:** `vite.config.ts` proxies `/api` to `http://localhost:8080`. If your backend is on a different port, update the `proxy` target in `vite.config.ts`.

---

## Environment variables

### Main stack (`.env`)

Copy `.env.example` → `.env`.

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_DB` | `formbuilder` | Database name |
| `POSTGRES_USER` | `postgres` | Database user |
| `POSTGRES_PASSWORD` | `changeme` | Database password |
| `MINIO_ROOT_USER` | `minioadmin` | MinIO admin username |
| `MINIO_ROOT_PASSWORD` | `minioadmin` | MinIO admin password |
| `DB_PORT` | `5432` | Host port for PostgreSQL |
| `BACKEND_PORT` | `8081` | Host port for the backend |
| `FRONTEND_PORT` | `3000` | Host port for the frontend |
| `MINIO_PORT` | `9000` | Host port for MinIO API |
| `MINIO_CONSOLE_PORT` | `9001` | Host port for MinIO console |

### E2E test stack (`.env.e2e`)

Copy `.env.e2e.example` → `.env.e2e`. This stack uses a separate `formbuilder_test` database and a separate MinIO volume so it never touches app data.

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_DB` | `formbuilder_test` | Isolated test database |
| `POSTGRES_USER` | `postgres` | Database user |
| `POSTGRES_PASSWORD` | `changeme` | Database password |
| `MINIO_ROOT_USER` | `minioadmin` | MinIO admin username |
| `MINIO_ROOT_PASSWORD` | `minioadmin` | MinIO admin password |

### Backend environment overrides

The backend reads standard Spring Boot environment variable conventions. Any `application.yaml` key can be overridden:

| Variable | Default (non-Docker) | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/formbuilder` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | _(empty)_ | DB password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Schema strategy (`update` / `create-drop`) |
| `MINIO_ENDPOINT` | `http://localhost:9000` | MinIO API endpoint |
| `MINIO_ACCESS_KEY` | `minioadmin` | MinIO access key |
| `MINIO_SECRET_KEY` | `minioadmin` | MinIO secret key |
| `MINIO_BUCKET` | `form-submissions` | Bucket for file uploads |

---

## Project structure

```
.
├── compose.yaml            # Main Docker Compose stack
├── compose.e2e.yml         # Override for E2E test stack (isolated DB + create-drop)
├── .env.example            # Template for .env
├── .env.e2e.example        # Template for .env.e2e
├── .yamllint.yml           # YAML linting rules
│
├── backend/                # Spring Boot / Kotlin API
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── kotlin/org/openownership/form_builder/
│       │   │   ├── FormBuilderApplication.kt
│       │   │   ├── config/
│       │   │   │   ├── MinioConfig.kt      # MinioClient bean + bucket init
│       │   │   │   └── SecurityConfig.kt   # CORS + permit-all (pre-auth)
│       │   │   ├── controller/
│       │   │   │   ├── WorkspaceController.kt
│       │   │   │   ├── FormController.kt
│       │   │   │   ├── FieldController.kt
│       │   │   │   ├── SubmissionController.kt
│       │   │   │   └── FileController.kt   # /api/files/upload, /api/files/download
│       │   │   ├── model/
│       │   │   │   ├── dao/                # JPA entities
│       │   │   │   └── dto/                # API data transfer objects
│       │   │   ├── repository/             # Spring Data JPA repositories
│       │   │   └── service/                # Business logic interfaces + impls
│       │   └── resources/
│       │       └── application.yaml        # Main configuration
│       └── test/
│           ├── kotlin/                     # Unit + integration tests
│           └── resources/
│               └── application.yaml        # Test overrides (create-drop, fake MinIO config)
│
└── frontend/               # React SPA
    ├── Dockerfile
    ├── nginx.conf          # Serves SPA; proxies /api to backend
    ├── playwright.config.ts
    ├── e2e/                # Playwright end-to-end tests
    │   ├── helpers.ts      # Shared setup helpers (uid, createForm*, etc.)
    │   ├── workspaces.spec.ts
    │   ├── forms.spec.ts
    │   ├── builder.spec.ts
    │   └── submissions.spec.ts
    └── src/
        ├── api/client.ts   # Typed fetch wrappers for every API endpoint
        ├── components/
        │   └── Layout.tsx
        ├── pages/
        │   ├── WorkspacesPage.tsx
        │   ├── FormsPage.tsx
        │   ├── FormBuilderPage.tsx
        │   └── SubmissionsPage.tsx
        ├── store/          # Zustand stores (workspace, form, submission)
        ├── types/index.ts  # Shared TypeScript interfaces
        └── index.css       # Design system (OpenOwnership brand tokens)
```

---

## API reference

All endpoints are under `/api`. Authentication is not yet enforced (permit-all; see `SecurityConfig.kt`).

### Workspaces

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/workspaces` | List all workspaces |
| `POST` | `/api/workspaces` | Create a workspace |
| `GET` | `/api/workspaces/{id}` | Get a workspace |
| `PUT` | `/api/workspaces/{id}` | Update a workspace |
| `DELETE` | `/api/workspaces/{id}` | Soft-delete a workspace |

### Forms

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/workspaces/{workspaceId}/forms` | List forms in a workspace |
| `POST` | `/api/workspaces/{workspaceId}/forms` | Create a form |
| `GET` | `/api/forms/{id}` | Get a form |
| `PUT` | `/api/forms/{id}` | Update a form |
| `DELETE` | `/api/forms/{id}` | Soft-delete a form |

### Fields

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/forms/{formId}/fields` | List fields on a form |
| `POST` | `/api/forms/{formId}/fields` | Add a field |
| `PUT` | `/api/fields/{id}` | Update a field |
| `DELETE` | `/api/fields/{id}` | Remove a field |

Supported field types: `TEXT`, `TEXTAREA`, `NUMBER`, `DATE`, `SELECT`, `CHECKBOX`, `RADIO`, `FILE`.

Fields with options (`SELECT`, `CHECKBOX`, `RADIO`) store their choices in `config.options` (a JSON array of strings).

### Submissions

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/forms/{formId}/submissions` | List submissions for a form |
| `GET` | `/api/submissions/{id}` | Get a submission |
| `POST` | `/api/forms/{formId}/submissions` | Create a submission |

Submission `data` is a freeform JSON object keyed by field ID. For `FILE` fields the value is the MinIO object key returned by the upload endpoint.

### Files

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/files/upload` | Upload a file (`multipart/form-data`, field name `file`) → `{ "key": "..." }` |
| `GET` | `/api/files/download?key=...` | Download a file (streams from MinIO with `Content-Disposition: attachment`) |

Files are stored in MinIO under `submissions/{uuid}/{filename}`. The bucket (`form-submissions` by default) is created automatically on backend startup.

---

## Testing

### Backend tests

```bash
cd backend

# Unit + integration tests (requires PostgreSQL on localhost:5432)
./mvnw test
```

Integration tests use a real PostgreSQL database (`formbuilder`, `create-drop` schema strategy) and a mocked `MinioClient`, so no MinIO instance is needed. Start the database with:

```bash
docker compose up -d db
```

### E2E tests (Playwright)

E2E tests run against the full stack using a dedicated test database (`formbuilder_test`) so they never write to the application database.

**First-time setup**

```bash
cd frontend
npm install
npx playwright install chromium --with-deps
```

**Running**

```bash
# 1. Start the E2E stack (stop the main stack first if it's running)
docker compose down
docker compose --env-file .env.e2e -f compose.yaml -f compose.e2e.yml up -d --build

# 2. Wait for the frontend to be ready, then run tests
cd frontend
npm run test:e2e

# 3. (Optional) interactive UI mode
npm run test:e2e:ui

# 4. View the HTML report after a run
npm run test:e2e:report

# 5. Tear down and restore the main stack
docker compose --env-file .env.e2e -f compose.yaml -f compose.e2e.yml down
docker compose up -d
```

The E2E stack differs from the main stack in two ways:
- PostgreSQL uses the `formbuilder_test` database (separate volume: `postgres_e2e_data`)
- Spring Boot uses `ddl-auto: create-drop`, wiping and recreating the schema on each backend restart

---

## Code quality

### YAML

```bash
yamllint .
```

### TypeScript / React

```bash
cd frontend
npm run lint
```

Uses ESLint 9 flat config with `typescript-eslint`, `eslint-plugin-react-hooks`, and `eslint-plugin-react-refresh`.

### Kotlin

```bash
cd backend
./mvnw spotless:check   # check
./mvnw spotless:apply   # auto-fix
```

Uses Spotless with ktlint 1.5.0. Style rules are in `backend/.editorconfig`.

---

## CI/CD

GitHub Actions runs on every push and pull request to `dev` and `main`.

**Stage 1 — Code quality (parallel)**

| Job | Tool |
|---|---|
| `lint-yaml` | yamllint |
| `lint-typescript` | ESLint |
| `lint-kotlin` | Spotless / ktlint |

**Stage 2 — Tests (run only when all linters pass, parallel)**

| Job | Description |
|---|---|
| `test-backend` | Maven test suite against a PostgreSQL service container |
| `test-e2e` | Playwright suite against the full Docker Compose E2E stack |

Playwright reports are uploaded as artifacts on failure.

---

## Built with Claude Code

This project was built using [Claude Code](https://claude.ai/code) — Anthropic's agentic CLI for software engineering. Claude Code was used throughout the development process, including:

- Scaffolding the Spring Boot / Kotlin backend and React frontend
- Designing and implementing the REST API, JPA entities, and service layer
- Building the form builder UI, submission flow, and MinIO file upload/download integration
- Writing the full backend unit and integration test suite
- Writing the Playwright end-to-end test suite and helpers
- Setting up GitHub Actions CI with linting and test stages
- Authoring this documentation

> Claude Code can read, write, and run code across a full project in a single session. Learn more at [claude.ai/code](https://claude.ai/code).
