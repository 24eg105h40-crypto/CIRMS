# CIRMS Backend - Spring Boot REST API

Campus Item Recovery & Management System (CIRMS) Backend Service.

## Technology Stack
- Java 17+ (running with JDK 26)
- Spring Boot 3.3.4
- Spring Data JPA / Hibernate
- Spring Security
- MySQL Connector/J
- JJWT 0.12.5
- Bean Validation

## Database Configuration
The backend connects to MySQL on port 3306.
- Database: `cirms_db`
- Username: `root`
- Password: `${DATABASE_PASSWORD:root}`

Default categories are automatically seeded on initial application startup:
`Electronics`, `Documents`, `Wallet`, `ID Card`, `Books`, `Keys`, `Bags`, `Clothing`, `Jewellery`, `Accessories`, `Other`.

Default development admin account is automatically created on startup:
- Email: `admin@cirms.com`
- Password: `ChangeThisAdminPassword123!`
- Role: `ADMIN`

## Authentication API Endpoints
- `POST /api/auth/register` — Student registration (validates fields, prevents duplicate email/student ID, BCrypt hashes password, returns JWT & UserDTO)
- `POST /api/auth/login` — Authentication (verifies BCrypt credentials, returns JWT & UserDTO)
- `GET /api/auth/me` — Authenticated user details (Bearer JWT required)
- `GET /api/auth/status` — Health check
- `GET /api/admin/ping` — Admin-only endpoint (Role: ADMIN required; returns 403 Forbidden for STUDENT)

## Running Backend
To compile and test:
```powershell
mvn clean test
```

To run the application:
```powershell
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
