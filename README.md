# GHADS — Gaza Humanitarian Aid Distribution System

**Course:** Programming III Lab — CSCI 2108  
**Instructor:** Aya N. Alharazin  
**Student:** Doaa Raed Shehada Shafout — 220213922  
**University:** Islamic University of Gaza  

---

## Overview

GHADS is a JavaFX desktop application that streamlines the distribution of humanitarian aid to families in Gaza. It connects relief organizations, field coordinators, and beneficiary families through a centralized system that tracks aid deliveries, prevents duplicate distributions, and prioritizes the most vulnerable households.

### Problem it solves

Humanitarian organizations operating in Gaza face challenges in coordinating aid distribution:
- No centralized record of which families received what aid and when
- Risk of duplicate aid to the same family within a short period
- Difficulty identifying the most vulnerable families across organizations
- Paper-based or scattered record‑keeping

GHADS replaces this with a structured digital workflow — from family registration to aid distribution with built‑in duplicate checks.

---

## Features

| Feature | Details |
|---|---|
| **Role‑based access** | Admin (full CRUD) and Coordinator (org‑specific operations) |
| **Authentication** | Username/password login with session management |
| **Dashboard** | Real‑time statistics (total families, served/not‑served, org counts) |
| **Organization CRUD** | Manage partner organizations (UNRWA, Red Crescent, WFP, etc.) |
| **User CRUD** | Register coordinators assigned to organizations |
| **Family Registration** | Register families with vulnerability level (HIGH/MEDIUM/LOW), location, family size, national ID |
| **Aid Distribution** | Record distributions by family, organization, aid type, and date |
| **Duplicate Prevention** | Blocks MEDIUM/LOW families from receiving the same aid type within 30 days |
| **Vulnerability Priority** | Lists sorted by HIGH → MEDIUM → LOW vulnerability |
| **Profile Management** | Coordinators can edit their own profile and change password |
| **Filtering** | View distributions by organization |
| **Responsive UI** | JavaFX with CSS theming, Scene Builder FXML layouts |

---

## Architecture

### Patterns Used

| Pattern | Where | Benefit |
|---|---|---|
| **MVC** | `controllers/` (Controller), `views/` (FXML/View), `models/` (Model) | Separates UI from logic; FXML can be redesigned independently |
| **DAO** | `dao/` (Data Access Object) | Isolates SQL in dedicated classes; switching DB requires changing only DAOs |
| **Singleton** | `DBConnection`, `SessionManager` | Single connection pool, single source of truth for logged‑in user |
| **Service Layer** | `services/AuthService`, `services/DistributionService` | Encapsulates business rules outside controllers |

### Layered Structure

```
src/
├── app/
│   └── Main.java              ← Entry point (launches Login.fxml)
├── config/
│   ├── DBConnection.java      ← Singleton JDBC connection manager
│   └── SessionManager.java    ← Singleton session state
├── controllers/               ← 10 controllers, one per screen
├── dao/                       ← 4 DAOs (User, Family, Organization, AidDistribution)
├── models/                    ← 4 POJO entities
├── services/                  ← AuthService, DistributionService
├── styles/                    ← 4 CSS files (Login, Dashboard, Form, DarkTheme)
└── views/                     ← 10 FXML files (Scene Builder)
```

### Data Flow

```
User Action → Controller → Service (business rules) → DAO (SQL) → MySQL DB
                                ↑
                    Returns entity / List<Entity>
                                ↓
Controller updates UI (TableView, Labels, Alerts)
```

---

## Database Schema

**Database:** `ghads` (MySQL 8+, InnoDB, utf8mb4)

### Tables

| Table | Purpose |
|---|---|
| `organizations` | Relief organizations (UNRWA, Red Crescent, etc.) |
| `users` | System users (ADMIN + COORDINATOR roles) |
| `families` | Beneficiary families with vulnerability level |
| `aid_distributions` | Distribution records linking family, org, user |

### Key Relationships

```
organizations 1───* users
organizations 1───* aid_distributions
families      1───* aid_distributions
users         1───* aid_distributions
```

### Index

- `idx_dup_check` on `aid_distributions(family_id, aid_type, distribution_date)` — optimizes the duplicate‑detection query.

### Seed Data

- **5 organizations** (UNRWA, Red Crescent, WFP, Qatar Charity, Islamic Relief)
- **6 users** (admin + 5 coordinators, one per org)
- **18 families** (7 HIGH, 6 MEDIUM, 5 LOW vulnerability)
- **7 distributions** demonstrating both allowed and rejected duplicate scenarios

---

## Duplicate Check Rule

Implemented in `DistributionService.checkDuplicate()`:

```
IF vulnerability_level = "HIGH"
    → ALWAYS ALLOWED
ELSE
    → Check: same family + same aid_type within last 30 days?
        Yes → REJECTED (show details: family name, org, date)
        No  → ALLOWED
```

### Why HIGH is exempted

Families with HIGH vulnerability (e.g., displaced, medical emergencies) need repeated aid regardless of frequency. MEDIUM/LOW families are expected to have their basic needs met for at least 30 days.

---

## Setup & Running

### Prerequisites

- Java 8 JDK (1.8.0_111 or later)
- NetBeans 8.2 (or compatible IDE)
- XAMPP (or any MySQL server)
- MySQL Connector/J (included at `lib/mysql-connector-j-9.7.0.jar`)

### Steps

1. **Start MySQL** (via XAMPP Control Panel → Start MySQL)

2. **Create the database** — Open phpMyAdmin or MySQL CLI and run:
   ```sql
   SOURCE path/to/GHADS/database.sql;
   ```

3. **Open the project** in NetBeans:
   - `File → Open Project` → select `GHADS` folder

4. **Build & Run**:
   - `Clean and Build (Shift+F11)`
   - `Run (F6)`

5. **Login** with:
   - **Admin:** `admin` / `admin123`
   - **Coordinators:** `ahmed` / `ahmed123`, `mariam` / `mariam123`, `yousef` / `yousef123`, `fatima` / `fatima123`, `huda` / `huda123`

### Troubleshooting

| Issue | Solution |
|---|---|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Ensure `lib/mysql-connector-j-9.7.0.jar` is on the classpath (already set in `project.properties`) |
| `Access denied for user 'root'@'localhost'` | Update `DBConnection.java` with your MySQL password |
| FXML loading fails | Make sure resource paths use `../views/...` (relative) not `/views/...` |

---

## Screens

| Screen | Role | Description |
|---|---|---|
| Login | All | Username/password authentication |
| Admin Dashboard | Admin | Statistics cards, navigation to all management screens |
| Coordinator Dashboard | Coordinator | Org‑specific stats, profile, family registration, distribution form |
| Organization Management | Admin | CRUD table for organizations |
| User Management | Admin | CRUD table for coordinators (assigned to orgs) |
| Family Management | Both | Register, update, delete families with vulnerability levels |
| Aid Distribution Management | Admin | View/filter all distributions by organization |
| Aid Distribution Form | Coordinator | Record new distribution with duplicate check |
| Profile | Coordinator | View/edit personal info |
| Change Password | Both | Change current password |

---

## Technologies

- **Language:** Java 8
- **UI Framework:** JavaFX 8 with Scene Builder
- **Styling:** CSS (4 theme files)
- **Database:** MySQL 8+ (via XAMPP)
- **JDBC Driver:** MySQL Connector/J 9.7.0
- **Build Tool:** Apache Ant (NetBeans)
- **Patterns:** MVC, DAO, Singleton, Service Layer

---

## Project Structure (for submission)

```
GHADS/
├── README.md
├── database.sql
├── build.xml
├── manifest.mf
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── screenshots/
│   ├── login.png
│   ├── admin-dashboard.png
│   ├── coordinator-dashboard.png
│   ├── organization-management.png
│   ├── user-management.png
│   ├── family-management.png
│   ├── aid-distribution-management.png
│   ├── aid-distribution-form.png
│   ├── profile.png
│   ├── change-password.png
│   └── about-dialog.png
├── src/
│   ├── app/Main.java
│   ├── config/DBConnection.java, SessionManager.java
│   ├── controllers/  (10 files)
│   ├── dao/  (4 files)
│   ├── models/  (4 files)
│   ├── services/  (2 files)
│   ├── styles/  (4 CSS files)
│   └── views/  (10 FXML files)
└── nbproject/
    └── project.properties
```

---

## License

Academic project — Programming III Lab, Islamic University of Gaza, Spring 2026.
