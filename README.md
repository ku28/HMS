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
| Testing | JUnit 5, MockMvc, Testcontainers |
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

## Testing

```bash
cd "Hms Backend"
./mvnw test
```

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