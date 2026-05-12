# 🏨 Hotel Management System

Production-grade Hotel Management System built with **Spring Boot 3**, **React + Vite**, **TailwindCSS**, **MySQL**, **Redis**, and **Docker**.

## Architecture

```
hotel-management-system/
├── Hms Backend/           # Spring Boot REST API
│   ├── admin/             # Admin dashboard module
│   ├── auth/              # JWT Authentication
│   ├── hotel/             # Hotels + Amenities
│   ├── room/              # Rooms + Room Types
│   ├── reservation/       # Booking management
│   ├── payment/           # Payment processing
│   ├── review/            # Guest reviews
│   ├── common/            # Shared DTOs, responses
│   ├── config/            # Redis, Swagger configs
│   ├── exception/         # Global error handling
│   ├── mapper/            # Entity-DTO mappers
│   └── security/          # JWT + Spring Security
├── Hms Frontend/          # React + Vite + TailwindCSS
├── db/                    # SQL schemas + ER diagrams
├── deployment/            # Nginx configs
├── .github/workflows/     # CI/CD pipelines
└── docker-compose.yml     # Full stack orchestration
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.4.5, Spring Security, Spring Data JPA |
| Frontend | React 19, Vite, TailwindCSS v4, Zustand, Recharts |
| Database | MySQL 8.4 |
| Cache | Redis 7.2 |
| Auth | JWT (JJWT 0.12) |
| API Docs | SpringDoc OpenAPI 2.8 |
| Testing | JUnit 5, MockMvc, Mockito, Spring Security Test |
| DevOps | Docker, Docker Compose, GitHub Actions, Nginx |

## Quick Start

### Prerequisites
- Java 21+
- Node.js 20+
- Docker & Docker Compose
- MySQL 8.x (or use Docker)

### 1. Docker (Recommended)

```bash
# Clone and start everything
cp .env.example .env
# Edit .env with your settings
docker compose up -d
```

The system will be available at:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 2. Local Development

**Backend:**
```bash
cd "Hms Backend"
# Set environment variables (or create application-dev.yml)
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd "Hms Frontend"
npm install
npm run dev
```

### 3. Database Setup

```bash
# Run SQL files against your MySQL instance
mysql -u root -p hotel < db/create_hotel.sql
mysql -u root -p hotel < db/create_users.sql
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |

### Hotels
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/hotels/all` | Public |
| GET | `/api/hotels/{id}` | Public |
| POST | `/api/hotels/post` | Admin |
| PUT | `/api/hotels/update/{id}` | Admin |
| DELETE | `/api/hotels/delete/{id}` | Admin |

### Rooms, Reservations, Payments, Reviews
Full API documentation available at `/swagger-ui.html`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://mysql:3306/hotel` |
| `DB_USERNAME` | Database username | `hotel_user` |
| `DB_PASSWORD` | Database password | `hotel_pass` |
| `REDIS_HOST` | Redis hostname | `redis` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT signing secret | — |
| `SERVER_PORT` | Backend port | `8080` |

## Default Admin Credentials

- **Email**: `admin@hms.com`
- **Password**: `admin123`

> **Note:** These credentials are seeded via `db/create_users.sql` which runs during Docker initialization. If you're running for the first time, ensure the database is initialized with both `create_hotel.sql` and `create_users.sql`. If credentials show as invalid, you may need to recreate the MySQL volume: `docker compose down -v && docker compose up -d`

## Testing

### Running Tests

```bash
cd "Hms Backend"
./mvnw test
```

### Test Architecture

The project includes a comprehensive enterprise-grade test suite:

| Test Type | Count | Description |
|-----------|-------|-------------|
| Service Tests | 5 suites | Mockito-based unit tests for all service implementations |
| Controller Tests | 6 suites | MockMvc integration tests for all REST controllers |
| Security Tests | 1 suite | JWT authentication and role-based access tests |
| Validation Tests | Embedded | DTO validation tests within controller test suites |
| Pagination Tests | Embedded | Page size, sorting, and empty page tests |

### Test Conventions

- Enterprise naming: `shouldSaveHotelSuccessfully()`, `shouldRejectInvalidReservationDates()`
- No H2 database — MySQL test configuration only
- `@WebMvcTest` with MockMvc for controller isolation
- `@ExtendWith(MockitoExtension.class)` for service unit tests
- Comprehensive `@BeforeEach` setup methods

### Test Configuration

| File | Purpose |
|------|---------|
| `src/test/resources/application-test.yml` | MySQL test database config |
| `TestSecurityConfig.java` | Mock JWT/Security for WebMvcTest slices |
| `TestMockMvcConfig.java` | Shared MockMvc builder configuration |

## CI/CD Pipeline

### GitHub Actions Workflows

The project includes two CI/CD workflow files:

#### 1. `backend-ci.yml` — Backend CI (Triggered on Push/PR)

**What it does:**
1. Spins up MySQL 8.4 and Redis 7.2 as service containers
2. Initializes the database schema using SQL files from `db/`
3. Runs all JUnit 5 tests against a real MySQL instance
4. **Only builds the JAR if all tests pass** (fail-fast)
5. Uploads test reports and the built JAR as artifacts

**Triggers:** Push or PR to `develop` or `master` branches (when backend files change)

#### 2. `ci-cd.yml` — Full CI/CD Pipeline

**What it does:**
1. **Backend Build & Test** — Same as above: MySQL + Redis services, test execution, JAR packaging
2. **Frontend Build** — Installs npm dependencies and builds the React production bundle
3. **Docker Build** — Builds all Docker images (only on `master` branch merges, after both backend and frontend succeed)

**Key behavior:**
- The JAR is **never generated** if tests fail
- Docker images are **only built** when merging to `master`
- Test reports are **always uploaded**, even on failure

### How GitHub Actions Works

GitHub Actions automatically runs these workflows when you push code or create pull requests. No manual setup is needed beyond having the workflow files in `.github/workflows/`.

**The CI pipeline ensures:**
- ✅ All tests must pass before a JAR is produced
- ✅ Backend and frontend build independently in parallel
- ✅ Docker build only runs after both succeed
- ✅ Test reports are always available for debugging

## Deployment Guide (AWS EC2)

### Prerequisites

1. **AWS Account** with EC2 access
2. **Docker** and **Docker Compose** installed on the EC2 instance
3. **Security Group** configured with inbound rules for ports 80, 443, 8080, 3000

### Step-by-Step EC2 Deployment

#### Step 1: Launch an EC2 Instance

1. Go to **AWS Console** → **EC2** → **Launch Instance**
2. Choose **Amazon Linux 2023** or **Ubuntu 22.04 LTS**
3. Select instance type: **t3.medium** (recommended minimum: 2 vCPU, 4 GB RAM)
4. Configure security group:
   - SSH (port 22) — Your IP
   - HTTP (port 80) — Anywhere
   - HTTPS (port 443) — Anywhere
   - Custom TCP (port 8080) — Anywhere (API)
   - Custom TCP (port 3000) — Anywhere (Frontend dev)
5. Create or select a key pair for SSH access
6. Launch the instance

#### Step 2: Connect and Install Dependencies

```bash
# Connect to EC2
ssh -i your-key.pem ec2-user@<ec2-public-ip>

# Install Docker (Amazon Linux 2023)
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Git
sudo yum install -y git

# Re-login to apply Docker group
exit
ssh -i your-key.pem ec2-user@<ec2-public-ip>
```

#### Step 3: Clone and Deploy

```bash
# Clone the repository
git clone https://github.com/<your-username>/hms.git
cd hms

# Create environment file
cp .env.example .env

# Edit with production values
nano .env
# Change:
#   JWT_SECRET=<generate-a-strong-64-char-secret>
#   MYSQL_ROOT_PASSWORD=<strong-password>
#   MYSQL_PASSWORD=<strong-password>
#   DB_PASSWORD=<same-as-MYSQL_PASSWORD>
#   REDIS_PASSWORD=<strong-password>

# Start all services
docker compose up -d

# Verify all containers are running
docker compose ps

# Check logs
docker compose logs -f backend
```

#### Step 4: Verify Deployment

```bash
# Test backend health
curl http://localhost:8080/swagger-ui.html

# Test from browser
# Navigate to http://<ec2-public-ip>
```

### Production Considerations

- **Use HTTPS**: Set up an ALB (Application Load Balancer) with an ACM SSL certificate
- **Domain**: Point your domain to the ALB or EC2 Elastic IP
- **Monitoring**: Enable CloudWatch for container metrics
- **Backups**: Set up RDS instead of containerized MySQL for production databases
- **Secrets**: Use AWS Secrets Manager instead of `.env` files

## Team Modules

| # | Module | Developer Scope |
|---|--------|----------------|
| 1 | Admin Dashboard + DevOps | `admin/`, Docker, CI/CD |
| 2 | Hotels + Hotel Amenities | `hotel/` |
| 3 | Rooms + Room Types | `room/` |
| 4 | Booking + Payment | `reservation/`, `payment/` |
| 5 | Reservation Management | `reservation/` controllers |
| 6 | Reviews | `review/` |
| 7 | Auth + Security | `auth/`, `security/` |

## License

MIT