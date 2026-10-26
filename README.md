# CIRMS – Campus Item Recovery & Management System
> **B.Tech Final-Year Project**

A modern full-stack web application designed for automated, verified, and secure lost & found management across college campuses.

---

## 1. System Architecture

```
CIRMS/
├── backend/                              # Spring Boot 3.3.4 REST API
│   ├── src/main/java/com/cirms/
│   │   ├── config/                       # Security, CORS, Database Seeder
│   │   ├── controller/                   # REST Controllers (Auth, Items, Claims, etc.)
│   │   ├── dto/                          # Request/Response Data Transfer Objects
│   │   ├── entity/                       # JPA Entities (User, Item, Claim, etc.)
│   │   ├── exception/                    # Global Exception Handler & Custom Errors
│   │   ├── repository/                   # Spring Data JPA Repositories
│   │   ├── security/                     # Security Components
│   │   ├── service/                      # Business Logic & Service Interfaces
│   │   └── CirmsApplication.java         # Spring Boot Entry Point
│   ├── src/main/resources/
│   │   └── application.properties       # MySQL and environment config
│   ├── pom.xml                           # Maven dependencies & plugins
│   └── README.md
│
├── frontend/                             # React 18 + Vite Web App
│   ├── src/
│   │   ├── assets/                       # Static media assets
│   │   ├── components/                   # Reusable UI (Navbar, Footer, Cards)
│   │   ├── context/                      # AuthContext & State management
│   │   ├── hooks/                        # Custom React hooks
│   │   ├── layouts/                      # App layouts
│   │   ├── pages/                        # Home, Search, Report, Dashboards
│   │   ├── services/                     # Axios API clients
│   │   ├── utils/                        # Formatters and helpers
│   │   ├── App.jsx                       # Client-side router configuration
│   │   ├── index.css                     # Design tokens and styles
│   │   └── main.jsx                      # Vite entry point
│   ├── package.json                      # Frontend dependencies
│   ├── vite.config.js                    # Vite dev proxy configuration
│   └── README.md
│
├── .env.example                          # Environment template
└── README.md                             # Main project documentation
```

---

## 2. Technology Stack

- **Backend**: Java 17+ (JDK 26 runtime), Spring Boot 3.3.4, Spring Data JPA, Spring Security, MySQL Connector/J, Bean Validation, JJWT.
- **Frontend**: React 18, Vite, React Router 6, Axios, Lucide Icons, Modern CSS.
- **Database**: MySQL 8.0 (`cirms_db`).

---

## 3. Database Entities & Relationships

| Entity | Table | Key Fields | Relationships |
|---|---|---|---|
| **User** | `users` | `id`, `fullName`, `studentId`, `email`, `phone`, `password`, `role` (`STUDENT`, `ADMIN`), `profileImage` | `1 → N` Items, `1 → N` Claims, `1 → N` Notifications |
| **Category** | `categories` | `id`, `name`, `description` | `1 → N` Items |
| **Item** | `items` | `id`, `itemName`, `description`, `color`, `brand`, `location`, `dateLostOrFound`, `itemType` (`LOST`, `FOUND`), `status`, `imageUrl` | `N → 1` User, `N → 1` Category, `1 → N` Claims |
| **Claim** | `claims` | `id`, `reason`, `identifyingInformation`, `proofImage`, `status` (`PENDING`, `APPROVED`, `REJECTED`), `adminComment` | `N → 1` Item, `N → 1` User (Claimant) |
| **Notification** | `notifications` | `id`, `title`, `message`, `type`, `isRead`, `createdAt` | `N → 1` User |

---

## 4. How to Run Locally

### Prerequisites
1. **Java**: JDK 17+ installed (`java -version`)
2. **Maven**: 3.9+ installed (`mvn -v`)
3. **Node.js**: v18+ installed (`node -v`)
4. **MySQL**: 8.0+ running on port `3306`

### Database Setup
Ensure MySQL is running and create the database if not present:
```sql
CREATE DATABASE IF NOT EXISTS cirms_db;
```

### Backend Startup
```powershell
cd backend
mvn spring-boot:run
```
The backend will launch at `http://localhost:8080`.
The application automatically creates all tables and seeds the 11 campus categories on first launch.

### Frontend Startup
```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```
The frontend will start at `http://localhost:5173`.
API calls to `/api` are automatically proxied to `http://localhost:8080`.

### Default Development Credentials
| Role | Email | Password | Access Level |
|---|---|---|---|
| **Admin** | `admin@cirms.com` | `ChangeThisAdminPassword123!` | Full Administration (`/api/admin/**`) |
| **Student** | Registered via `/register` | Registered password | Student Portal (`/api/**`) |

---

## 5. Development Phases

- **Phase 1 (Completed)**: Root project structure, Spring Boot 3.x setup, React/Vite frontend, MySQL configuration, all 5 JPA entities, repositories, service interfaces, controller skeletons, DTOs, global exception handling, database auto-generation and category seeder.
- **Phase 2 (Completed)**: Complete Authentication System:
  - Student registration (`POST /api/auth/register`) with field validations, duplicate email/student ID prevention, and BCrypt password encryption.
  - User login (`POST /api/auth/login`) with credential verification and signed JWT token issuance.
  - JWT Authentication Filter and stateless Spring Security configuration.
  - Role-based authorization (`ROLE_STUDENT` vs `ROLE_ADMIN`) with 401 Unauthorized / 403 Forbidden handling.
  - Current user endpoint (`GET /api/auth/me`).
  - Automated development admin account initialization via `DataInitializer`.
  - Frontend authentication state (`AuthContext`), Protected Route component (`ProtectedRoute`), dynamic Navbar, Login, Register, Profile, and Dashboard routing.
  - 17/17 backend tests verified.
- **Phase 3 (Completed)**: Lost/Found item management, multipart image uploads, safe filename generator, full item CRUD with ownership checks.
- **Phase 4 (Completed)**: Advanced search with `GET /api/items/search?type&status&categoryId&query&location&color&brand&fromDate&toDate` with pagination and sorting.
- **Phase 5 (Completed)**: Claim submission lifecycle:
  - `POST /api/claims` — students submit ownership claims for FOUND items.
  - `GET /api/claims/my` — retrieve own claim submissions.
  - Full `ClaimController` with authentication guards and duplicate-claim prevention.
  - Frontend `MyClaimsPage` with status tracking (Pending/Approved/Rejected), admin comments, and filterable tabs.
  - `ItemDetailsPage` with a full claim submission modal (validation, success state, error handling).
- **Phase 6 (Completed)**: Admin dashboard and claim verification desk:
  - `GET /api/admin/stats` — live analytics (total users, items, claims by status, recovered count).
  - `GET /api/admin/claims/pending` — pending claim queue.
  - `PUT /api/admin/claims/{id}/approve` and `PUT /api/admin/claims/{id}/reject` — approve/reject with admin comment.
  - `GET /api/admin/users` — full user list.
  - Frontend `AdminDashboard` wired to live API with expandable claim details, approve/reject modals with comment fields, and refresh button.
  - `MyReportsPage` with item list, delete confirmation, and filter by LOST/FOUND.
  - `StudentDashboard` with live KPI stats from API and unread notification count.
- **Phase 7 (Completed)**: In-app notifications:
  - `NotificationsPage` with unread badge, type-based icons, click-to-mark-read, and bulk mark-all-as-read.
  - Navbar Bell icon with real-time unread count badge.
- **Phase 8 (Completed)**: Smart attribute-based matching algorithm:
  - 100-point composite scoring engine (Category 30, Name 25, Color 15, Brand 15, Location 10, Date 5).
  - `GET /api/matches/item/{id}` — ranked matches for a specific item.
  - `GET /api/matches/my` — aggregated matches across all user's active items.
  - Async `triggerMatchingAndNotify` fires in-app notifications on ≥40 score matches after item creation.
  - Frontend `MatchesPage` with SVG score gauges, attribute score pills, and confidence badges.
  - `ItemDetailsPage` embedded "Possible Matches" section.
- **Phase 9 (Completed)**: UI design polish, responsiveness, and animations:
  - Comprehensive `index.css` design system — design tokens, form classes, skeleton loaders, CSS animations.
  - Responsive Navbar with hamburger drawer (mobile), scroll-aware shadow, active route highlighting.
  - Premium `HomePage` with animated hero blobs, IntersectionObserver stat counters, recent items preview, category chips with hover effects.
  - Enhanced `SearchPage` with skeleton loaders, expandable advanced filters, active filter chips, image zoom, pagination.
  - `ReportItemPage` with gradient header, icon-labeled form fields, smart matching tip alert.
  - Premium `Footer` with 4-column layout, animated system status indicator.
- **Phase 10 (Completed)**: Testing, seed dataset, and project wrap-up:
  - `DataInitializer` expanded with 3 demo student accounts and 12 realistic campus items across 5 intentional high-confidence matching pairs (laptop, watch, backpack, ID card, earphones).
  - All demo credentials: `aryan@student.campus.edu`, `priya@student.campus.edu`, `rahul@student.campus.edu` (password: `Demo@12345`).
  - README updated to document all phases.
