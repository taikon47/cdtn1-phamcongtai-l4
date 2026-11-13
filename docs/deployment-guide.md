# Deployment Guide — Smart CRM Mekong Mobile (Flow L4)

- **Module:** L4 – Technician Assignment & Appointment Scheduling
- **Track:** Software Engineering (SE)
- **Author:** Pham Cong Tai (Student ID: 2374802010445)
- **Target Release Tag:** `v1.0`

---

## 1. Requirements

Before deploying the prototype, ensure that the target machine meets the following minimum system requirements:

- **Operating System:** Linux (Ubuntu 22.04+ / Debian 12+), macOS (13+), or Windows 10/11 with WSL2.
- **Docker Engine:** Version 24.0.0 or higher.
- **Docker Compose:** Version 2.20.0 or higher (`docker compose version`).
- **Disk Space:** At least 2.5 GB of free disk space (for base container images and build layers).
- **Available Port:** Host port **8081** must be free (or customizable via `APP_PORT` in `.env`).
- **Runtime note:** You do **NOT** need to install Java, Maven, Node.js, or PostgreSQL on the host machine. Everything runs containerized inside Docker.

---

## 2. Setup Steps

Execute the following exact commands sequentially from a clean terminal workspace:

```bash
# Step 1: Clone the repository at tag v1.0 into a new directory
git clone --branch v1.0 https://github.com/taikon47/cdtn1-phamcongtai-l4.git smartcrm-l4 && cd smartcrm-l4

# Step 2: Initialize the environment configuration file from template
cp .env.example .env

# Step 3: Build and start all services in detached mode (<= 2 commands rule)
docker compose up -d --build
```

*Note:* The initial container build and image downloading take approximately 1.5 to 2.5 minutes depending on your internet connection. Subsequent startups take less than 5 seconds.

---

## 3. Environment Variables

All configuration is externalized from source code into environment variables. The table below outlines all available parameters declared in `.env.example`:

| Variable Name | Description | Sample / Default Value | Required? |
|:---|:---|:---|:---:|
| `APP_PORT` | Host port exposed for the web application & REST API | `8081` | No |
| `DB_NAME` | PostgreSQL database name | `projectl4` | Yes |
| `DB_USER` | PostgreSQL superuser username | `postgres` | Yes |
| `DB_PASSWORD` | PostgreSQL database password | `postgres` | Yes |
| `JWT_SECRET` | Secret key used for signing session/auth tokens ($\ge 32$ chars) | `c21hcnQtY3JtLW1la29uZy1tb2JpbGUtYXV0aC1zZWNyZXQta2V5LTIwMjY=` | Yes |
| `CORS_ORIGINS` | Comma-separated list of allowed HTTP origins | `http://localhost:8081,http://localhost:5173` | Yes |
| `LOG_LEVEL` | Application logging verbosity (`INFO`, `DEBUG`, `WARN`, `ERROR`) | `INFO` | No |

---

## 4. Sample Data (Pre-seeded Fixtures)

The prototype automatically executes initialization scripts in `db/init/` (`001_create_tables.sql` and `002_seed_danh_muc.sql`) upon the first database launch.

### Verification numbers:
Once the system starts, the database contains:
- **6 Technicians (`technician` table):**
  - Nguyen Van An (`Quan 1`, `Hardware`, workload: 1)
  - Tran Thi Bich (`Quan 1`, `Software`, workload: 0)
  - Le Hoang Cuong (`Binh Thanh`, `Hardware`, workload: 1)
  - Pham Minh Duc (`Can Tho`, `Screen`, workload: 0)
  - Vu Thu Ha (`Can Tho`, `Battery`, workload: 1)
  - Dang Quoc Huy (`Quan 1`, `Hardware`, workload: 5 - `BUSY`)
- **5 Warranty Tickets (`warranty_ticket` table):**
  - 4 tickets in `UNASSIGNED` status (`TCK-2026-0001` through `TCK-2026-0004`).
  - 1 ticket in `ASSIGNED` status (`TCK-2026-0005`).
- **1 Active Assignment (`assignment` table):** Ticket 5 assigned to Technician 3.
- **2 Appointments (`appointment` table):** 1 `CONFIRMED` and 1 `PENDING`.

---

## 5. Verify It Works

Run the following commands to confirm that every component of the system operates as expected:

### 5.1. Check Container Health Status
```bash
docker compose ps
```
**Expected Output:** Both `smartcrm_l4_app` and `smartcrm_l4_db` display status `Up` and `(healthy)`.

### 5.2. Query the Healthcheck Endpoint
```bash
curl -s http://localhost:8081/health
```
**Expected Output:**
```json
{"status":"ok","db":"up"}
```

### 5.3. Query Warranty Tickets API
```bash
curl -s "http://localhost:8081/api/v1/warranty-tickets?status=UNASSIGNED"
```
**Expected Output:** JSON array containing 4 unassigned warranty tickets with HTTP status `200 OK`.

### 5.4. Access Dispatcher Web UI
Open your browser and navigate to:
```text
http://localhost:8081/
```
**Expected Result:** The Smart CRM Mekong Mobile Dispatcher Dashboard loads cleanly, displaying live counts for tickets, technicians, and appointments without console errors.

### 5.5. Execute the Automated Test Suite (12 Core Tests)
```bash
docker compose exec app ./mvnw test -Dtest=L4BT3VerificationTests
```
**Expected Output:**
```text
[INFO] Running com.mekongmobile.projectl4.L4BT3VerificationTests
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 6. Production Readiness: HTTPS & Reverse Proxy

In a production environment:
1. Place the application behind an Nginx or Traefik reverse proxy configured with a valid TLS certificate (Let's Encrypt).
2. Configure proxy headers:
   ```nginx
   proxy_set_header X-Forwarded-Proto $scheme;
   proxy_set_header X-Real-IP $remote_addr;
   proxy_set_header Host $host;
   ```
3. Set `NODE_ENV=production` and enforce HTTPS redirects at the proxy gateway level.
