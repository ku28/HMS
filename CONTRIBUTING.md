# Contributing — Hotel Management System

> Enterprise Engineering Handbook | 7-Member Development Team

---

## Table of Contents

1. [Team Structure & Ownership](#team-structure--ownership)
2. [Strict Development Rules](#strict-development-rules)
3. [GitHub Workflow](#github-workflow)
4. [Branch Naming Conventions](#branch-naming-conventions)
5. [Commit Message Standards](#commit-message-standards)
6. [Pull Request Protocol](#pull-request-protocol)
7. [7-Day Execution Plan](#7-day-execution-plan)
8. [Team Lead Responsibilities](#team-lead-responsibilities)
9. [Project Management](#project-management)
10. [Scalability Rules](#scalability-rules)
11. [Final Delivery Checklist](#final-delivery-checklist)

---

## Team Structure & Ownership

### Member 1 — Admin Dashboard + DevOps + CI/CD (Team Lead)

**Owns:**
- `admin/` (controller, DTOs)
- `config/` (Redis, Swagger)
- `common/` (ApiResponse, PagedResponse)
- `exception/` (GlobalExceptionHandler)
- `docker-compose.yml`, `Dockerfile` (both)
- `.github/workflows/ci-cd.yml`
- `deployment/nginx.conf`
- `.env`, `.env.example`
- `README.md`, `CONTRIBUTING.md`

**Must NOT modify:** `hotel/`, `room/`, `reservation/`, `payment/`, `review/`, `auth/`, `security/`

---

### Member 2 — Hotels + Hotel Amenities

**Owns:**
- `hotel/entity/Hotel.java`, `hotel/entity/Amenity.java`
- `hotel/repository/HotelRepository.java`, `hotel/repository/AmenityRepository.java`
- `hotel/dto/` (all Hotel and Amenity DTOs)
- `hotel/service/` (HotelService, HotelServiceImpl, AmenityService, AmenityServiceImpl)
- `hotel/controller/` (HotelController, AmenityController, HotelAmenityController)
- `mapper/HotelMapper.java`

**Must NOT modify:** `room/`, `reservation/`, `payment/`, `review/`, `auth/`, `security/`, `admin/`, `config/`

---

### Member 3 — Rooms + Room Types + Room Amenities

**Owns:**
- `room/entity/Room.java`, `room/entity/RoomType.java`
- `room/repository/` (RoomRepository, RoomTypeRepository)
- `room/dto/` (all Room and RoomType DTOs)
- `room/service/` (all Room and RoomType services)
- `room/controller/` (RoomController, RoomTypeController, RoomAmenityController)
- `mapper/RoomMapper.java`

**Must NOT modify:** `hotel/`, `reservation/`, `payment/`, `review/`, `auth/`, `security/`, `admin/`, `config/`

---

### Member 4 — Booking + Payment Module

**Owns:**
- `reservation/entity/Reservation.java`
- `reservation/repository/ReservationRepository.java`
- `reservation/dto/` (ReservationRequestDto, ReservationResponseDto)
- `reservation/service/` (ReservationService, ReservationServiceImpl — **create/booking logic only**)
- `reservation/controller/ReservationController.java` — POST/booking endpoints
- `payment/` (all entity, repository, dto, service, controller)
- `mapper/PaymentMapper.java`

**Must NOT modify:** `hotel/`, `room/`, `review/`, `auth/`, `security/`, `admin/`, `config/`

---

### Member 5 — Reservation Viewing + Updating Module

**Owns:**
- `reservation/controller/ReservationController.java` — GET/PUT/DELETE endpoints
- `reservation/service/` — read/update/delete logic
- `mapper/ReservationMapper.java`

**Must NOT modify:** `hotel/`, `room/`, `payment/`, `review/`, `auth/`, `security/`, `admin/`, `config/`

> **Coordination:** Members 4 and 5 share the `reservation/` module. Member 4 owns create/booking flow; Member 5 owns read/update/delete. Coordinate through PR reviews.

---

### Member 6 — Reviews Module

**Owns:**
- `review/entity/Review.java`
- `review/repository/ReviewRepository.java`
- `review/dto/` (ReviewRequestDto, ReviewResponseDto)
- `review/service/` (ReviewService, ReviewServiceImpl)
- `review/controller/ReviewController.java`
- `mapper/ReviewMapper.java`

**Must NOT modify:** `hotel/`, `room/`, `reservation/`, `payment/`, `auth/`, `security/`, `admin/`, `config/`

---

### Member 7 — Authentication + Authorization

**Owns:**
- `auth/` (entity, repository, dto, service, controller)
- `security/` (JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService, SecurityConfig)
- `mapper/UserMapper.java`
- `db/create_users.sql`

**Must NOT modify:** `hotel/`, `room/`, `reservation/`, `payment/`, `review/`, `admin/`, `config/`

---

### Code Review Rules

| Rule | Policy |
|------|--------|
| Self-merge | **Prohibited** — all code must be reviewed |
| Min reviewers | **1 reviewer** (module owner or Team Lead) |
| Cross-module changes | Requires **both** module owners to approve |
| `common/`, `config/`, `exception/` | Only Team Lead (Member 1) may modify |
| Merge target | Always `develop`, never `main` |

---

## Strict Development Rules

These rules are **non-negotiable**. PRs violating any rule will be rejected.

| # | Rule | Rationale |
|---|------|-----------|
| 1 | **No `findAll()`** — use paginated queries only | Prevents memory issues at scale |
| 2 | **No `@Query`** — use Spring Data derived methods only | Maintains consistency, prevents SQL injection risk |
| 3 | **DTOs mandatory** for all request/response payloads | Entities must never be exposed to the API layer |
| 4 | **Pagination mandatory** on all list endpoints | Enterprise scalability requirement |
| 5 | **Service layer mandatory** | No business logic in controllers |
| 6 | **`ResponseEntity<ApiResponse<T>>`** on all endpoints | Consistent API contract |
| 7 | **Environment variables only** — no hardcoded values | Security and deployment flexibility |
| 8 | **No `localhost` hardcoding** | Use `${DB_URL}`, `${REDIS_HOST}` |
| 9 | **No entity exposure** in REST responses | Always map to DTOs via Mapper classes |
| 10 | **No broken builds** pushed to any remote branch | Run `mvn compile` locally before pushing |
| 11 | **All PRs must pass GitHub Actions** before merge | CI gate enforcement |
| 12 | **Every module must include MockMvc tests** | Minimum 1 controller test per module |
| 13 | **LAZY fetch only** — no EAGER relationships | Prevents N+1 query issues |
| 14 | **Constructor injection** — no `@Autowired` on fields | Testability and immutability |
| 15 | **`ddl-auto=validate`** — schema managed by SQL files | Hibernate must never modify the schema |

---

## GitHub Workflow

### Repository Structure

```
main          ← Production-ready code (protected)
  └── develop ← Integration branch (protected)
       ├── feature/auth-jwt
       ├── feature/hotel-module
       ├── feature/room-management
       └── ...
```

### Developer Workflow

1. **Fork** the repository to your personal GitHub account
2. **Clone** your fork locally
3. **Create feature branch** from `develop`:
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout -b feature/hotel-module
   ```
4. **Commit** daily with descriptive messages
5. **Push** to your fork: `git push origin feature/hotel-module`
6. **Create PR** from your fork's feature branch → upstream `develop`
7. **Address review** comments and update the PR
8. **Squash merge** into `develop` after approval

### Protected Branches

| Branch | Direct Push | Force Push | Delete | Merge Via |
|--------|:-----------:|:----------:|:------:|-----------|
| `main` | ❌ | ❌ | ❌ | PR from `develop` only (Team Lead) |
| `develop` | ❌ | ❌ | ❌ | PR from feature branches |

---

## Branch Naming Conventions

Format: `<type>/<short-description>`

| Type | Usage | Example |
|------|-------|---------|
| `feature/` | New functionality | `feature/hotel-crud-apis` |
| `fix/` | Bug fixes | `fix/reservation-date-overlap` |
| `refactor/` | Code improvements | `refactor/payment-service-caching` |
| `test/` | Test additions | `test/review-controller-mockmvc` |
| `docs/` | Documentation | `docs/swagger-annotations` |
| `devops/` | Infrastructure | `devops/docker-compose-redis` |

### Per-Member Branch Examples

| Member | Branch Name |
|--------|------------|
| 1 | `devops/docker-compose-setup`, `feature/admin-dashboard` |
| 2 | `feature/hotel-module`, `feature/amenity-crud` |
| 3 | `feature/room-management`, `feature/room-type-service` |
| 4 | `feature/booking-flow`, `feature/payment-module` |
| 5 | `feature/reservation-viewing`, `feature/reservation-update` |
| 6 | `feature/review-system`, `feature/review-rating-filter` |
| 7 | `feature/auth-jwt`, `feature/security-config` |

---

## Commit Message Standards

Format: `<type>(<scope>): <description>`

| Type | When to Use |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code restructuring |
| `test` | Adding/updating tests |
| `docs` | Documentation changes |
| `chore` | Build/config changes |
| `style` | Formatting (no logic change) |

### Realistic Commit Examples by Day

**Day 1:**
```
chore(project): initialize spring boot project structure
chore(docker): configure docker-compose with mysql and redis
feat(common): create ApiResponse and PagedResponse wrappers
feat(exception): implement global exception handler
feat(config): configure redis caching with custom serializer
chore(ci): setup github actions ci/cd pipeline
feat(entity): create Hotel and Amenity JPA entities
feat(entity): create Room and RoomType JPA entities
```

**Day 2:**
```
feat(auth): implement User entity and UserRepository
feat(security): create JwtTokenProvider with jjwt 0.12
feat(security): implement JwtAuthenticationFilter
feat(security): configure SecurityConfig with CORS and RBAC
feat(auth): implement registration and login endpoints
feat(hotel): create HotelRepository with derived queries
feat(hotel): implement HotelService with pagination
feat(hotel): create HotelController REST endpoints
```

**Day 3:**
```
feat(room): implement RoomService with availability checks
feat(room): create RoomController with paginated endpoints
feat(room-type): implement RoomTypeService and controller
feat(amenity): create AmenityService with hotel associations
feat(mapper): implement RoomMapper for DTO conversions
feat(hotel): add search filtering to hotel listing
refactor(room): add room amenity join table management
```

**Day 4:**
```
feat(reservation): implement ReservationService with date validation
feat(reservation): add check-in/check-out overlap detection
feat(reservation): create ReservationController POST endpoint
feat(payment): implement PaymentService with revenue calculation
feat(payment): create PaymentController CRUD endpoints
feat(mapper): implement ReservationMapper and PaymentMapper
test(reservation): add date overlap validation unit tests
```

**Day 5:**
```
feat(review): implement ReviewService with rating filtering
feat(review): create ReviewController with recent reviews endpoint
feat(admin): implement admin dashboard stats aggregation
feat(admin): create AdminController with dashboard endpoint
feat(mapper): implement ReviewMapper for DTO conversions
test(review): add MockMvc tests for ReviewController
docs(swagger): add OpenAPI annotations to all controllers
```

**Day 6:**
```
test(hotel): add MockMvc tests for HotelController
test(reservation): add integration tests for booking flow
refactor(hotel): optimize caching with eviction on mutations
refactor(payment): add redis caching to revenue queries
fix(security): resolve cors preflight request handling
chore(docker): validate full stack with docker compose up
test(auth): add authentication flow integration tests
```

**Day 7:**
```
feat(frontend): scaffold react app with vite and tailwindcss
feat(frontend): implement auth store with zustand
feat(frontend): create login and signup pages
feat(frontend): implement hotel listing with search
feat(frontend): create booking flow with payment
feat(frontend): implement admin dashboard with recharts
chore(frontend): configure axios interceptors for jwt
test(e2e): validate frontend-backend integration
```

---

## Pull Request Protocol

### PR Title Format

`[TYPE] Short description of changes`

**Examples:**
```
[FEATURE] Implement Hotel CRUD APIs with pagination
[FEATURE] Add JWT authentication and role-based access
[FIX] Resolve reservation date overlap validation
[REFACTOR] Optimize Redis caching for hotel queries
[TEST] Add MockMvc tests for PaymentController
[DEVOPS] Configure Docker Compose with health checks
[DOCS] Update Swagger annotations for Room endpoints
```

### PR Description Template

```markdown
## Summary
Brief description of what this PR does.

## Changes
- Added/Modified/Removed [specific files or features]
- Implemented [specific business logic]

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing performed
- [ ] All existing tests pass

## Checklist
- [ ] DTOs used for all request/response payloads
- [ ] Pagination implemented on list endpoints
- [ ] No `findAll()` usage
- [ ] No `@Query` usage
- [ ] Service layer handles all business logic
- [ ] `ResponseEntity<ApiResponse<T>>` used consistently
- [ ] No hardcoded values (uses environment variables)
- [ ] Entities are NOT exposed in API responses
- [ ] LAZY fetch type on all relationships
- [ ] Constructor injection (no `@Autowired` fields)
- [ ] Swagger/OpenAPI annotations added
- [ ] Redis caching applied where appropriate
- [ ] MockMvc controller tests included
- [ ] Code compiles without warnings
- [ ] GitHub Actions CI passes

## Related Issues
Closes #XX

## Screenshots (if applicable)
```

---

## 7-Day Execution Plan

---

### DAY 1 — Foundation + Architecture

**Goal:** Project skeleton, infrastructure, shared modules, entities.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | Docker Compose, CI/CD pipeline, .env, Nginx config, README | 4-5 | `[DEVOPS] Configure Docker and CI/CD pipeline` |
| **1** | `common/ApiResponse`, `common/PagedResponse`, `exception/GlobalExceptionHandler` | 2-3 | `[FEATURE] Create shared response wrappers and exception handling` |
| **2** | `Hotel` entity, `Amenity` entity, `HotelRepository`, `AmenityRepository` | 3-4 | `[FEATURE] Create Hotel and Amenity entities and repositories` |
| **3** | `Room` entity, `RoomType` entity, `RoomRepository`, `RoomTypeRepository` | 3-4 | `[FEATURE] Create Room and RoomType entities and repositories` |
| **4** | `Reservation` entity, `ReservationRepository`, `Payment` entity, `PaymentRepository` | 3-4 | `[FEATURE] Create Reservation and Payment entities` |
| **5** | Review `Reservation` entity (coordinate with Member 4), create DTOs | 2-3 | — (prep work, no PR) |
| **6** | `Review` entity, `ReviewRepository`, Review DTOs | 3 | `[FEATURE] Create Review entity and repository` |
| **7** | `User` entity, `UserRepository`, `create_users.sql`, JWT dependencies | 3-4 | `[FEATURE] Create User entity and auth repository` |

**Review Process:** Team Lead reviews all entity PRs for naming, annotation, and relationship correctness.

---

### DAY 2 — Authentication + Hotel Module

**Goal:** JWT security fully operational; Hotel CRUD endpoints live.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | `SwaggerConfig`, `RedisConfig`, verify Docker stack | 2-3 | `[FEATURE] Configure Swagger and Redis` |
| **2** | `HotelService`, `HotelServiceImpl`, `HotelMapper`, `HotelController` | 4-5 | `[FEATURE] Implement Hotel CRUD APIs` |
| **2** | `AmenityService`, `AmenityServiceImpl`, `AmenityController` | 3-4 | `[FEATURE] Implement Amenity CRUD APIs` |
| **3** | Hotel/Amenity DTOs review, begin RoomType service | 2-3 | — (prep work) |
| **4** | `ReservationRequestDto`, `ReservationResponseDto` | 2 | — (prep work) |
| **5** | `ReservationMapper`, reservation response structure | 2 | — (prep work) |
| **6** | `ReviewRequestDto`, `ReviewResponseDto`, `ReviewMapper` | 3 | `[FEATURE] Create Review DTOs and mapper` |
| **7** | `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`, `AuthService`, `AuthController` | 5-6 | `[FEATURE] Implement JWT authentication and security` |

**Review Process:** Member 7's security PR must be reviewed by Team Lead before merging.

---

### DAY 3 — Rooms + Room Types + Amenities

**Goal:** Room management endpoints operational; amenity associations working.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | Review PRs, resolve merge conflicts, validate CI pipeline | 1-2 | — |
| **2** | `HotelAmenityController`, add search filtering to hotels | 2-3 | `[FEATURE] Add hotel amenity associations and search` |
| **3** | `RoomTypeService`, `RoomTypeController`, `RoomService`, `RoomController`, `RoomMapper` | 5-6 | `[FEATURE] Implement Room and RoomType CRUD APIs` |
| **3** | `RoomAmenityController` | 2 | `[FEATURE] Implement Room amenity management` |
| **4** | Begin `ReservationService` — create booking logic | 3 | — (WIP) |
| **5** | Begin reservation GET/list endpoints | 2 | — (WIP) |
| **6** | `ReviewService`, `ReviewServiceImpl` | 3 | `[FEATURE] Implement Review service layer` |
| **7** | `CustomUserDetailsService`, refine security rules, `UserMapper` | 2-3 | `[REFACTOR] Refine auth and add UserMapper` |

---

### DAY 4 — Reservation + Booking + Payment

**Goal:** Complete booking flow from reservation creation through payment.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | Review PRs, verify Redis caching works in Docker | 1-2 | — |
| **2** | Add Redis `@Cacheable` to Hotel queries, optimize | 2 | `[REFACTOR] Add caching to Hotel service` |
| **3** | Add Redis caching to Room queries, availability filter | 2 | `[REFACTOR] Add caching to Room service` |
| **4** | `ReservationService` (create with date overlap check), `ReservationController` POST | 4-5 | `[FEATURE] Implement booking flow with date validation` |
| **4** | `PaymentService`, `PaymentServiceImpl`, `PaymentController`, `PaymentMapper` | 4-5 | `[FEATURE] Implement Payment CRUD and revenue calculation` |
| **5** | `ReservationController` GET/PUT/DELETE, pagination | 4 | `[FEATURE] Implement reservation viewing and management` |
| **6** | `ReviewController` with rating filter and recent reviews | 3 | `[FEATURE] Implement Review controller endpoints` |
| **7** | Test auth flow end-to-end, add role-based endpoint protection | 2-3 | `[TEST] Validate authentication flow` |

---

### DAY 5 — Review System + Admin Analytics

**Goal:** Review module complete; admin dashboard API operational.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | `AdminController` — dashboard stats endpoint, aggregate data | 3-4 | `[FEATURE] Implement admin dashboard API` |
| **2** | Swagger annotations, final hotel module polish | 2 | `[DOCS] Add Swagger annotations to Hotel module` |
| **3** | Swagger annotations, final room module polish | 2 | `[DOCS] Add Swagger annotations to Room module` |
| **4** | Swagger annotations, payment revenue endpoint | 2 | `[DOCS] Add Swagger annotations to Payment module` |
| **5** | Swagger annotations, reservation module polish | 2 | `[DOCS] Add Swagger annotations to Reservation module` |
| **6** | Complete ReviewController, Swagger annotations, edge cases | 3-4 | `[FEATURE] Finalize Review module with documentation` |
| **7** | Security audit, verify all admin endpoints are protected | 2 | `[REFACTOR] Security audit and endpoint protection` |

---

### DAY 6 — Testing + Optimization + Docker Validation

**Goal:** All modules tested; Docker stack fully validated; caching optimized.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | Docker Compose full validation, Nginx proxy test, CI run | 3-4 | `[DEVOPS] Validate Docker stack and CI pipeline` |
| **2** | MockMvc tests for HotelController, AmenityController | 3 | `[TEST] Add Hotel module controller tests` |
| **3** | MockMvc tests for RoomController, RoomTypeController | 3 | `[TEST] Add Room module controller tests` |
| **4** | MockMvc tests for ReservationController (booking), PaymentController | 3 | `[TEST] Add Booking and Payment controller tests` |
| **5** | MockMvc tests for ReservationController (viewing/updating) | 2 | `[TEST] Add Reservation management tests` |
| **6** | MockMvc tests for ReviewController | 2 | `[TEST] Add Review controller tests` |
| **7** | MockMvc tests for AuthController, security filter tests | 3 | `[TEST] Add Authentication controller tests` |

**All members:** Run `mvn test` locally, fix failures, push fixes.

---

### DAY 7 — Frontend Development

**Goal:** React frontend scaffold with core pages integrated against live API.

| Member | Tasks | Expected Commits | Expected PR |
|--------|-------|-------------------|-------------|
| **1** | Admin Dashboard page (Recharts graphs, stats cards, tables) | 4-5 | `[FEATURE] Implement admin dashboard frontend` |
| **2** | Hotels listing page with search + Hotel detail page | 3-4 | `[FEATURE] Implement hotel browsing UI` |
| **3** | Room display within Hotel detail, availability indicators | 2-3 | `[FEATURE] Implement room display components` |
| **4** | Booking page (form + payment integration) | 3-4 | `[FEATURE] Implement booking flow UI` |
| **5** | My Reservations page (list, view details) | 3 | `[FEATURE] Implement reservation management UI` |
| **6** | Review display and submission components | 2-3 | `[FEATURE] Implement review system UI` |
| **7** | Login/Signup pages, Axios JWT interceptor, auth store, route guards | 4-5 | `[FEATURE] Implement authentication UI and state` |

**Shared:**
- `App.jsx` (routing) — Team Lead coordinates
- `PublicLayout.jsx`, `AdminLayout.jsx` — Team Lead owns
- `src/services/api.js` — Member 7 owns
- `src/store/authStore.js` — Member 7 owns

---

## Team Lead Responsibilities

| Responsibility | Details |
|----------------|---------|
| **PR Reviews** | Review every PR within 4 hours; prioritize security and shared module PRs |
| **Architecture Enforcement** | Reject PRs that use `findAll()`, `@Query`, or expose entities |
| **Merge Conflict Resolution** | Resolve conflicts on `develop` branch, especially between Members 4 & 5 |
| **CI/CD Maintenance** | Keep GitHub Actions pipeline green; fix build failures immediately |
| **Scalability Validation** | Verify all list endpoints use pagination; verify caching is applied |
| **Code Quality** | Enforce constructor injection, DTO patterns, ResponseEntity usage |
| **Daily Sync** | Lead 15-minute standup; track blockers |
| **Release Management** | Merge `develop` → `main` for releases; tag versions |

---

## Project Management

### Daily Standups (15 minutes max)

**Schedule:** Every day at the start of work session

**Format:**
1. **Yesterday:** What did you complete?
2. **Today:** What will you work on?
3. **Blockers:** What is preventing progress?

### Blocker Reporting

- Report blockers **immediately** in team chat — do not wait for standup
- If blocked by another member's module, coordinate directly with them
- If blocked by infrastructure (Docker, DB), escalate to Team Lead

### Syncing with `develop`

**Every morning before starting work:**
```bash
git fetch upstream
git checkout develop
git pull upstream develop
git checkout feature/your-branch
git rebase develop
```

### Merge Conflict Resolution

1. The developer whose PR has conflicts is responsible for resolving them
2. Rebase your feature branch onto latest `develop`
3. Test locally after resolving: `mvn compile && mvn test`
4. Force-push to your fork: `git push origin feature/your-branch --force-with-lease`
5. If conflicts span multiple modules, coordinate with the other module owner

---

## Scalability Rules

| Rule | Implementation |
|------|----------------|
| **40+ concurrent users** | HikariCP pool: 20 max connections, 5 min idle |
| **Caching strategy** | Redis cache on all GET list/detail endpoints; evict on CREATE/UPDATE/DELETE |
| **Pagination strategy** | All list endpoints return `Page<T>` with configurable `page` and `size` params |
| **Lazy loading** | All JPA relationships use `FetchType.LAZY` to prevent N+1 issues |
| **Query optimization** | Only Spring Data derived methods; no raw SQL; indices on FK columns |
| **Connection pooling** | HikariCP with 20s connection timeout, 5min idle timeout, 10min max lifetime |
| **Stateless sessions** | JWT tokens — no server-side session storage |
| **Cache TTL** | 10 minutes for general queries; evict immediately on mutations |

---

## Final Delivery Checklist

### Backend Checklist

- [ ] All 8 entities created and mapped correctly
- [ ] All repositories use derived query methods only
- [ ] All DTOs created (request + response for each module)
- [ ] All mappers implemented (manual entity↔DTO)
- [ ] All services implemented with `@Transactional`
- [ ] All controllers return `ResponseEntity<ApiResponse<T>>`
- [ ] Pagination on every list endpoint
- [ ] Redis caching on GET endpoints with eviction on mutations
- [ ] JWT authentication working (register + login)
- [ ] Role-based access (ADMIN vs USER) enforced
- [ ] Admin dashboard endpoint returning aggregate stats
- [ ] Global exception handling with proper HTTP status codes
- [ ] Swagger UI accessible at `/swagger-ui.html`
- [ ] MockMvc tests for every controller
- [ ] No `findAll()` usage anywhere
- [ ] No `@Query` annotation anywhere
- [ ] No entity exposed in API responses
- [ ] `ddl-auto=validate` — no auto schema changes
- [ ] `mvn test` passes with zero failures
- [ ] Application starts successfully with `mvn spring-boot:run`

### Frontend Checklist

- [ ] React + Vite project builds without errors
- [ ] TailwindCSS styling applied to all pages
- [ ] Landing page with hero, stats, features, CTA sections
- [ ] Hotel listing with search and pagination
- [ ] Hotel detail page with amenities and rooms
- [ ] Booking page with form and date selection
- [ ] Login and Signup pages with form validation
- [ ] My Reservations page with booking history
- [ ] Admin Dashboard with charts and data tables
- [ ] JWT token stored and sent via Axios interceptor
- [ ] Role-based route protection (admin routes guarded)
- [ ] Responsive design on mobile and desktop
- [ ] `npm run build` completes without errors

### Deployment Checklist

- [ ] `docker compose up -d` starts all 5 services
- [ ] MySQL container healthy and seeded with data
- [ ] Redis container running and accessible
- [ ] Backend container connects to MySQL and Redis
- [ ] Frontend container serves static files via Nginx
- [ ] Nginx reverse proxy routes `/api` to backend and `/` to frontend
- [ ] `.env` file has all required variables configured
- [ ] No hardcoded `localhost` in any configuration
- [ ] Health checks passing for all containers
- [ ] `docker compose logs` shows no errors

### Testing Checklist

- [ ] MockMvc tests exist for all controllers
- [ ] Authentication flow tested (register → login → access protected route)
- [ ] Date overlap validation tested for reservations
- [ ] Pagination verified on all list endpoints
- [ ] CORS working from frontend to backend
- [ ] Admin-only endpoints return 403 for regular users
- [ ] Redis caching verified (cache hit/miss logs)
- [ ] Error responses match the expected format

### Presentation Checklist

- [ ] Live demo prepared with working Docker stack
- [ ] Sample data loaded (hotels, rooms, reservations, reviews)
- [ ] Admin account ready (`admin@hms.com` / `admin123`)
- [ ] User registration flow demonstrated
- [ ] Booking flow demonstrated end-to-end
- [ ] Admin dashboard shown with charts
- [ ] Swagger UI shown with all endpoints
- [ ] Architecture diagram prepared
- [ ] GitHub repository clean with proper branching
- [ ] README.md comprehensive and up-to-date
