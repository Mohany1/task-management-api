# Task Management REST API

Spring Boot 3 · Java 17 · Spring Data JPA · MySQL · JWT · Swagger/OpenAPI

REST API for managing **users, projects and tasks** with JWT authentication and role-based authorization (ADMIN / USER).

## Run

```bash
docker compose up -d        # MySQL on :3306 (or use your own MySQL)
./mvnw spring-boot:run      # or: mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Default admin (created on first start): `admin` / `admin123`
- Config via env vars: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (32+ chars)

## Endpoints (19)

| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register | public |
| POST | /api/auth/login | public |
| GET | /api/users/me | any user |
| GET | /api/users | ADMIN |
| GET | /api/users/{id} | ADMIN |
| PUT | /api/users/{id}/role | ADMIN |
| DELETE | /api/users/{id} | ADMIN |
| POST | /api/projects | any user |
| GET | /api/projects | own projects (ADMIN: all) |
| GET | /api/projects/{id} | owner / ADMIN |
| PUT | /api/projects/{id} | owner / ADMIN |
| DELETE | /api/projects/{id} | owner / ADMIN |
| POST | /api/projects/{projectId}/tasks | owner / ADMIN |
| GET | /api/projects/{projectId}/tasks?status= | owner / ADMIN |
| GET | /api/tasks/my?status= | tasks assigned to me |
| GET | /api/tasks/{id} | owner / ADMIN |
| PUT | /api/tasks/{id} | owner / ADMIN |
| PATCH | /api/tasks/{id}/status | owner / ADMIN |
| DELETE | /api/tasks/{id} | owner / ADMIN |

## Quick test

```bash
# register + login
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"mohamed","email":"m@x.com","password":"123456"}'
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"mohamed","password":"123456"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')

# create project, then task
curl -X POST localhost:8080/api/projects -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"name":"Website","description":"Redesign"}'
curl -X POST localhost:8080/api/projects/1/tasks -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"title":"Design homepage","dueDate":"2026-12-01"}'
curl -X PATCH localhost:8080/api/tasks/1/status -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"status":"DONE"}'
```

## Structure

```
config/      SecurityConfig, OpenApiConfig, DataInitializer (seed admin)
security/    JwtService, JwtAuthFilter, CustomUserDetailsService
model/       User, Project, Task + enums (3NF schema, auto-generated)
repository/  Spring Data JPA repositories (+ custom JPQL queries)
service/     business logic + ownership checks
controller/  REST controllers
exception/   global error handling (400/401/403/404/409)
```
