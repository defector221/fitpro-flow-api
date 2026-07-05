# IronCore Architecture

## System Overview

```
┌─────────────────┐     HTTPS/REST      ┌──────────────────────┐
│  fitflow-pro    │ ◄──────────────────► │  fitpro-flow-api     │
│  React + TS     │     JWT Bearer       │  Spring Boot 3.x     │
│  TanStack Start │                      │  Java 17/21          │
└─────────────────┘                      └──────────┬───────────┘
                                                    │
                              ┌─────────────────────┼─────────────────────┐
                              ▼                     ▼                     ▼
                        PostgreSQL              Redis                 S3 (future)
                        (primary DB)           (cache/sessions)
```

## Multi-Branch Design

Every operational entity is scoped by `gym_id` and optionally `branch_id`:

- **Gym** — tenant root (single gym today, multi-gym SaaS tomorrow)
- **Branch** — physical location within a gym
- **User** — linked to gym + branch, carries RBAC roles

## Security Model

```
User ──► user_roles ──► roles ──► role_permissions ──► permissions
```

- JWT access tokens (15 min) + refresh tokens (7 days)
- Permission codes: `members:read`, `staff:write`, etc.
- Audit logs on create/update operations
- Login history tracked per session

## Database ER (Core Entities)

```
gyms ──┬── branches
       ├── users ── user_roles ── roles ── role_permissions ── permissions
       ├── employees ── employee_attendance
       │              └── leave_requests ── leave_types
       ├── members ── memberships ── membership_plans
       │           └── member_check_ins
       ├── payments / expenses
       ├── leads (CRM)
       ├── inventory_items
       └── gym_settings
```

## API Conventions

- Base path: `/api/v1`
- Pagination: `?page=0&size=20`
- Search: `?search=query`
- Filter: `?status=ACTIVE`
- Errors: `{ timestamp, status, error, message, path }`

## Deployment

See `docker-compose.yml` for local stack. Production checklist:

1. Set strong `JWT_SECRET` (256+ bits)
2. Configure managed PostgreSQL
3. Enable Redis for session/cache
4. Set `CORS_ORIGINS` to production frontend URL
5. Disable `SEED_ENABLED`
6. Configure S3 for document storage
7. Wire Razorpay/Stripe payment keys in `gym_settings.payment_config`
