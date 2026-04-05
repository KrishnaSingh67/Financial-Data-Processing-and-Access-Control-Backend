# Financial-Data-Processing-and-Access-Control-Backend

# Finance Dashboard Backend

A **Spring Boot 3.x** REST API for finance data management with **JWT authentication** and **role-based access control (RBAC)**.

---
 ## Work Flow Overview

     START
      ↓
    User hits API (/login or /register)
      ↓
    Authentication happens (JWT generated after login)
       ↓
    User role assigned:
    → VIEWER / ANALYST / ADMIN
---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Database | MySQL 8.x |
| ORM | Spring Data JPA (Hibernate) |
| Auth | Spring Security + JWT (jjwt 0.12) |
| Validation | Jakarta Validation |
| Build | Maven |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.x running locally

---

## Quick Start

### 1. Create the database (optional — auto-created via JDBC URL flag)
```sql
CREATE DATABASE finance_db;
```

### 2. Configure credentials

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.username=root
spring.datasource.password=root123   # ←  MySQL password {Sample password}
```

### 3. Build & Run
```bash
mvn spring-boot:run
```

On first startup, the app **automatically seeds** 3 users and 32 sample records.

Server starts at: **http://localhost:8087**

API Documentation URL : **http://localhost:8087/swagger-ui.html**


---

## Seed Credentials

| Email | Password | Role |
|---|---|---|
| admin@fin.dev | password123 | ADMIN |
| analyst@fin.dev | password123 | ANALYST |
| viewer@fin.dev | password123 | VIEWER |

---

## API Reference

### Authentication `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user (default: VIEWER) |
| POST | `/api/auth/login` | Public | Login, receive JWT token |
| GET | `/api/auth/me` | Authenticated | Get current user profile |

**Login example:**
```json
POST /api/auth/login
{
  "email": "admin@fin.dev",
  "password": "password123"
}
```
Returns: `{ "token": "eyJ...", "type": "Bearer", "role": "ADMIN", ... }`

Use the token as: `Authorization: Bearer <token>`

---
    

### User Management `/api/users` — ADMIN only

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}/role` | Assign role |
| PUT | `/api/users/{id}/status` | Toggle active/inactive |
| DELETE | `/api/users/{id}` | Delete user |

**Change role example:**
```json
PUT /api/users/2/role
{ "role": "ANALYST" }
```

---

### Financial Records `/api/records`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/records` | ADMIN | Create record |
| GET | `/api/records` | All roles | List with filters + pagination |
| GET | `/api/records/{id}` | All roles | Get single record |
| PUT | `/api/records/{id}` | ADMIN | Update record |
| DELETE | `/api/records/{id}` | ADMIN | Soft-delete record |

**Query parameters for GET `/api/records`:**

| Param | Type | Example |
|---|---|---|
| type | INCOME / EXPENSE | `?type=EXPENSE` |
| category | string | `?category=Rent` |
| from | ISO date | `?from=2026-01-01` |
| to | ISO date | `?to=2026-03-31` |
| page | int (0-indexed) | `?page=0` |
| size | int | `?size=10` |

**Create record example:**
```json
POST /api/records
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Freelance",
  "date": "2026-03-15",
  "notes": "Website redesign project"
}
```

---

### Dashboard Analytics `/api/dashboard` — ANALYST + ADMIN

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard/summary` | Total income, expense, net balance, record count |
| GET | `/api/dashboard/by-category` | Totals per category (income + expense + net) |
| GET | `/api/dashboard/monthly-trend` | Past 12 months income vs expense |
| GET | `/api/dashboard/recent` | Last 10 transactions |

---

### Health `/health` — Public

```
GET /health
→ { "status": "UP", "service": "Finance Dashboard API" }
```

---

## RBAC Matrix

| Action |             VIEWER | ANALYST | ADMIN |
|---|                  ---|---|---|
| Login / Register       | ✅ | ✅ | ✅ |
| View own profile       | ✅ | ✅ | ✅ |
| Read financial records  | ✅ | ✅ | ✅ |
| Access dashboard analytics | ❌ | ✅ | ✅ |
| Create / Update / Delete records | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

Enforced via Spring Security `@PreAuthorize` annotations at the controller level.

---

## Error Responses

All errors follow a consistent structure:

```json
{ "success": false, "message": "Descriptive error message" }
```

Validation errors include field-level details:
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "amount": "Amount must be greater than 0",
    "type": "Type is required: INCOME or EXPENSE"
  }
}
```

| Status | Scenario |
|---|---|
| 400 | Validation error / bad request |
| 401 | Missing / invalid / expired token |
| 403 | Insufficient role permissions |
| 404 | Resource not found |
| 500 | Internal server error |

---

## Project Structure

```
src/main/java/com/finance/dashboard/
├── config/          # SecurityConfig (Spring Security + CORS)
├── controller/      # AuthController, UserController, FinancialRecordController, DashboardController
├── dto/
│   ├── request/     # LoginRequest, RegisterRequest, RecordRequest, UpdateRoleRequest, UpdateStatusRequest
│   └── response/    # JwtResponse, UserResponse, RecordResponse, DashboardSummary, CategoryTotal, MonthlyTrend
├── entity/          # User, FinancialRecord (JPA entities)
├── enums/           # Role (VIEWER/ANALYST/ADMIN), RecordType (INCOME/EXPENSE)
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException
├── repository/      # UserRepository, FinancialRecordRepository, FinancialRecordSpecification
├── security/        # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── service/         # AuthService, UserService, FinancialRecordService, DashboardService
└── DataLoader.java  # DB seeder (runs on startup if DB is empty)
```

---

## Assumptions & Design Decisions

1. **Soft deletes** — Records are never physically removed; `is_deleted = true` hides them from all queries.
2. **Default role** — New registrations always get `VIEWER` role. Admins promote users via `PUT /api/users/{id}/role`.
3. **Inactive users** — Cannot login (returns 401). Admin can re-activate via `PUT /api/users/{id}/status`.
4. **JWT expiry** — 24 hours (`86400000 ms`). No refresh token implemented (out of scope for assessment).
5. **Pagination** — Record listing is paginated (default: page 0, size 10), sorted by date descending.
6. **Dashboard access** — `VIEWER` cannot access analytics endpoints (returns 403). This is intentional per the RBAC design.
7. **Database creation** — `createDatabaseIfNotExist=true` in the JDBC URL auto-creates the `finance_db` schema.
8. **Schema management** — `spring.jpa.hibernate.ddl-auto=update` lets Hibernate manage table creation/migration.
