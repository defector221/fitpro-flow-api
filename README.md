# fitpro-flow-api

IronCore Gym Management REST API — Spring Boot 3.x backend for [fitflow-pro](../fitflow-pro).

## Quick Start

### Prerequisites
- Java 17+ (Java 21 recommended for production)
- Maven 3.9+
- **PostgreSQL** and **Redis** installed locally (Docker optional)

### 1. One-time database setup (local PostgreSQL)

Ensure PostgreSQL is running (`pg_isready -h localhost -p 5432`).

```bash
psql -h localhost -U postgres -d postgres -f scripts/init-local-db.sql
```

This creates database `fitpro` and user `fitpro` / password `fitpro`.

If you use a different PostgreSQL user, copy env template and edit credentials:

```bash
cp .env.example .env
# edit DATABASE_USERNAME / DATABASE_PASSWORD / DATABASE_URL
```

Ensure Redis is running locally:

```bash
redis-cli ping   # should return PONG
```

### 2. Run the API

```bash
mvn spring-boot:run
```

Or with the local profile explicitly:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Docker is optional** — only needed if you don't have local PostgreSQL/Redis:

```bash
docker compose up -d postgres redis
```

API: http://localhost:9092  
Swagger: http://localhost:9092/swagger-ui.html  
Health: http://localhost:9092/api/v1/health

### 3. Demo credentials

| Email | Password |
|-------|----------|
| admin@ironcore.fitness | Admin@123 |

## Architecture

```
com.fitpro
├── config/          Security, JWT, CORS, OpenAPI
├── controller/    REST endpoints (/api/v1/*)
├── domain/
│   ├── entity/      JPA entities
│   ├── enums/
│   └── repository/  Spring Data JPA
├── dto/             Request/response DTOs
├── exception/       Global exception handler
├── security/        JWT filter, UserPrincipal
└── service/         Business logic
```

## Implemented Modules (Phase 2 — Complete)

| Module | Endpoints | Status |
|--------|-----------|--------|
| Auth | `/api/v1/auth/login`, `/refresh`, `/me` | ✅ |
| Dashboard | `/api/v1/dashboard` | ✅ |
| Members | `/api/v1/members` CRUD | ✅ |
| Staff | `/api/v1/staff` CRUD | ✅ |
| Plans | `/api/v1/plans` | ✅ |
| Check-in | `/api/v1/checkin` | ✅ |
| Attendance | `/api/v1/attendance` | ✅ |
| Leave | `/api/v1/leave` | ✅ |
| Payroll | `/api/v1/payroll` | ✅ |
| Finance | `/api/v1/finance` | ✅ |
| Inventory | `/api/v1/inventory` | ✅ |
| CRM | `/api/v1/crm` | ✅ |
| Trainers | `/api/v1/trainers` | ✅ |
| Reports | `/api/v1/reports/overview` | ✅ |
| Security | `/api/v1/security/audit-logs` | ✅ |
| Settings | `/api/v1/settings` | ✅ |
| Health | `/api/v1/health` | ✅ |

## Planned Enhancements (Phase 3)

- Razorpay/Stripe payment webhooks
- Email/SMS/WhatsApp notifications
- PDF/Excel report export
- S3 document storage
- Face recognition / biometric attendance
- Multi-language & mobile API

## Database

Flyway migrations in `src/main/resources/db/migration/`:
- `V1__schema.sql` — Full normalized schema (multi-branch ready)
- `V2__seed_data.sql` — Roles, permissions, demo gym, admin user, plans

## Environment Variables

| Variable | Default (local) |
|----------|-----------------|
| `PORT` | `9092` |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/fitpro` |
| `DATABASE_USERNAME` | `fitpro` |
| `DATABASE_PASSWORD` | `fitpro` |
| `REDIS_HOST` | `localhost` |
| `REDIS_PORT` | `6379` |
| `JWT_SECRET` | dev secret (change in production!) |
| `CORS_ORIGINS` | `http://localhost:5173` |
| `SEED_ENABLED` | `true` |

## Docker (full stack)

```bash
docker compose up --build
```

## Frontend Integration

Set in fitflow-pro `.env`:
```
VITE_API_BASE_URL=http://localhost:9092
```
