# rutaseg

Spring Boot backend for port cargo order management (Ruta Segura / GexLog). Powers the
[gexlog-fe](../gexlog-fe) admin SPA. Internal users (port operators) review and process
orders; external clients (freight forwarders / customs brokers) draft orders, attach
documents, and follow status changes.

## Tech stack

- Java 17
- Spring Boot 3.1
- Hibernate 6 / JPA (Criteria API + JPQL; one native SQL query for the CSV report)
- PostgreSQL 14+ (works against any PG-protocol-compatible service — local Docker, RDS, Neon)
- Flyway 9 — schema migrations in production
- AWS S3 or Supabase Storage — document storage, switched via `FILES_PROVIDER` (pluggable: `DummyFileRepository` for local/test)
- Spring Mail — Gmail SMTP for the internal contact form
- BCrypt + JJWT (HS256) — auth, hand-rolled (no Spring Security on the request path)

## Requirements

- Java 17 (`brew install openjdk@17` on macOS)
- Maven 3.6+ (the `mvnw` wrapper in the repo also works)
- A PostgreSQL 14+ database for local runs (Neon, Docker, or system install)

## Tests

```bash
mvn test
```

80 integration + unit tests. Tests run against H2 in PostgreSQL mode with Flyway applying
`V1__init.sql` and Hibernate validating the schema against the entities at startup; a
schema-vs-entities mismatch fails the suite.

## Environment variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SCOPE` | no | Active Spring profile. `local` (default), `integration_test`, or unset for prod. |
| `DB_URL` | prod | JDBC URL, e.g. `jdbc:postgresql://host:5432/dbname?sslmode=require`. |
| `DB_USERNAME` | prod | Database user. |
| `DB_PASSWORD` | prod | Database password. |
| `JWT_SECRET_KEY` | prod | HMAC-SHA256 signing key for issued JWTs. |
| `FILES_PROVIDER` | no | Storage backend for documents in prod: `aws` (default) or `supabase`. |
| `AWS_ACCESS_KEY` | aws | IAM access key for S3 document uploads. |
| `AWS_SECRET_KEY` | aws | IAM secret key. |
| `AWS_REGION` | aws | AWS region of the S3 bucket. |
| `AWS_S3_FILES_BUCKET` | aws | S3 bucket name for `Document.resource` keys. |
| `SUPABASE_URL` | supabase | Supabase project URL, e.g. `https://abcd1234.supabase.co`. Must be HTTPS. |
| `SUPABASE_SERVICE_KEY` | supabase | Service-role key (backend-only, never expose to FE). |
| `SUPABASE_BUCKET` | supabase | Private bucket name where document keys live. |
| `SUPABASE_SIGNED_URL_TTL_SECONDS` | no | Signed-URL lifetime in seconds. Default: `259200` (3 days, matches S3). |
| `EMAIL_USERNAME` | prod | Gmail account for SMTP. |
| `EMAIL_PASSWORD` | prod | Gmail app password. |
| `INTERNAL_EMAIL_TO` | no | Comma-separated recipients for `POST /internal/send-email`. Default: `email@host.com`. |
| `KNOWN_ORIGINS` | no | Comma-separated values accepted in the `x-auth-origin` header. Default: empty. |
| `ALLOW_ALL_ORIGINS` | no | If `true`, skip `x-auth-origin` validation. Default: `true`. |
| `ALLOWED_ORIGINS_PATTERNS` | no | Spring CORS origin patterns. Default: `*`. |
| `FILES_DIRECTORY` | local | Local FS root used by `OnDiskFileRepository` when no remote provider is configured. |

The server listens on **port 5000** (hardcoded in `application.yml`).

## Run locally (with Neon Postgres)

The default `local` profile is configured to let Hibernate auto-create / update the schema
on boot (Flyway is disabled in this profile), so an empty Neon database becomes ready on
the first run.

Convert the Neon connection string from `postgresql://user:pass@host/db?sslmode=require`
to JDBC format: prepend `jdbc:` and keep `?sslmode=require`. Then:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://ep-xxx.neon.tech/gexlog?sslmode=require'
export SPRING_DATASOURCE_USERNAME='your_user'
export SPRING_DATASOURCE_PASSWORD='your_password'
export FILES_DIRECTORY="$PWD/local-files" && mkdir -p "$FILES_DIRECTORY"

mvn spring-boot:run
```

First boot: Hibernate emits `create table ...` for every entity. Subsequent boots only
apply additive changes (Hibernate `update` mode). Smoke check:

```bash
curl http://localhost:5000/    # → "Im healthy"
```

## Run locally (Docker Postgres)

```bash
docker run --name gexlog-pg -e POSTGRES_PASSWORD=gexlog -e POSTGRES_USER=gexlog \
           -e POSTGRES_DB=gexlog -p 5432:5432 -d postgres:18

export FILES_DIRECTORY="$PWD/local-files" && mkdir -p "$FILES_DIRECTORY"
mvn spring-boot:run
```

The local profile already points at `jdbc:postgresql://localhost:5432/gexlog` with
`gexlog/gexlog` credentials, so nothing else needs to be set.

## Schema management

| Profile | Manager | Behaviour |
|---------|---------|-----------|
| `local` | Hibernate (`ddl-auto: update`) | Creates missing tables/columns from entities on boot. Never drops. Flyway disabled. |
| `integration_test` | Flyway + Hibernate `validate` | Applies `V1__init.sql` against H2-PG-mode, then Hibernate validates entities against schema. |
| prod (default) | Flyway + Hibernate `validate` | Same as tests, but against the real Postgres. |

Production migrations live in `src/main/resources/db/migration/V<n>__<description>.sql`.
Once a migration ships, never edit it in place — add the next versioned file.

## API

All non-public endpoints require a `Bearer <jwt>` token in the `x-auth-token` header
(or the `jwtToken` cookie set by `POST /login`). Standard error body:
`{ "code": "...", "message": "..." }`.

### Auth & health

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | public | Health check (`"Im healthy"`). |
| POST | `/login` | public | Body `{email, password}` → `{token, user}` + sets `jwtToken` cookie (`HttpOnly`, MaxAge 7200). |
| POST | `/internal/send-email` | public* | Send mail to `INTERNAL_EMAIL_TO` (used by the FE contact form). Subject + body. |

*Origin-restricted via `x-auth-origin`/`KNOWN_ORIGINS` when `ALLOW_ALL_ORIGINS=false`.

### Users (admin)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | Search users. Filters: `admin`, `nickname`, `email`, `with_client`, `page`, `page_size`. |
| POST | `/users` | Create user. Body: `{nickname, password, email, admin}`. |
| GET | `/users/{id}` | Get a user (admin or self). |
| PUT | `/users/{id}` | Update user. |

### Clients & Consignees

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/clients` | session | Search clients. Non-admin: forced to own client. Filters: `user_id`, `name`, `phone`, `cuit`, `with_user`, `with_consignees`, `page`, `page_size`. |
| POST | `/clients` | admin | Create client. |
| GET | `/clients/{id}` | admin or owner | Get client. |
| PUT | `/clients/{id}` | admin | Update client. |
| GET | `/clients/{id}/consignees` | admin or owner | List the client's consignees. |
| POST | `/clients/{id}/consignees` | admin or owner | Add consignee. Rejects duplicates by name or CUIT. |

### Orders

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/orders` | session | Search. Non-admin scoped to own client. Filters: `code`, `pema`, `transport`, `port`, `creation_date_from/to`, `arrival_date_from/to`, `time_from/to`, `client_id`, `status`, `load_code`, `origin`, `target`, `consignee_cuit`, `destination_code`, `sorts`, `page`, `page_size`. Cached for 10s. |
| POST | `/orders` | session | Create order in `DRAFT`. |
| GET | `/orders/{id}` | admin or owner | Get full order with all sub-resources. |
| PUT | `/orders/{id}` | admin or (owner & DRAFT) | Update order core fields + child collections. |
| PUT | `/orders/{id}/status/{newStatus}` | admin or (owner: DRAFT→REVISION only) | Transition status. Adds a SYSTEM note. |
| POST | `/orders/{id}/returned/{true\|false}` | admin | Set returned flag (sets `finished_at` when true). |
| POST | `/orders/{id}/billed/{true\|false}` | admin | Set billed flag. |
| POST | `/orders/generate-report` | session | CSV report. Body: `{client_id?, date_from, date_to}`. Non-admin: forced own client. |

### Order sub-resources

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/orders/{id}/documents` | admin or owner | Upload `multipart/form-data` (field `file`). Stored in S3 (or disk in local profile). |
| GET | `/orders/{id}/documents/{docId}` | admin or owner | Get document with a 3-day presigned URL (or local path). |
| DELETE | `/orders/{id}/documents/{docId}` | admin or owner | Delete document. |
| GET | `/orders/{id}/notes` | admin or owner | List notes. |
| POST | `/orders/{id}/notes` | session | Add note. Author = ADMIN if admin, else CLIENT. |
| PUT | `/orders/{id}/notes/{noteId}` | admin or (owner: only own CLIENT notes) | Edit note. SYSTEM notes can't be edited. |
| DELETE | `/orders/{id}/notes/{noteId}` | admin or (owner: only own CLIENT notes) | Delete note. SYSTEM notes can't be deleted. |
| GET | `/orders/{id}/costs` | admin or owner | List costs. |
| POST | `/orders/{id}/costs` | admin | Add cost (`amount`, `description`, `type`). |
| PUT | `/orders/{id}/costs/{costId}` | admin | Update cost. |
| DELETE | `/orders/{id}/costs/{costId}` | admin | Delete cost. |

## Deeper documentation

- [`../../docs/RFC.md`](../../docs/RFC.md) — full technical reference (high-level product overview + low-level entity / endpoint / auth details + known debt).
- [`../../docs/mysql_to_postgres.md`](../../docs/mysql_to_postgres.md) — migration plan from MySQL 8 to PostgreSQL.
