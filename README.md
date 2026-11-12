# AR Denial Analytics

## 1. Project overview and features

AR Denial Analytics is a claims dashboard for reviewing denied healthcare claims. It provides:

- Denied claim counts and billed amounts grouped by payer.
- Denied billed amounts grouped by denial reason.
- Search filters for payer, denial reason, and denied-date range.
- Pagination with most recently denied claims shown first.

## 2. Technology stack

- **Backend:** Java, Spring Boot, Spring Web, Spring Data JPA, Maven
- **Database:** MySQL
- **Frontend:** React, Vite, Axios, Recharts, Lucide

## 3. Project folder structure

```text
backend/                         # Project root
├── backend/                     # Spring Boot application
│   ├── src/main/java/           # Java source
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── schema.sql
│   │   └── data.sql
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/                    # React application
│   ├── src/
│   ├── package.json
│   └── package-lock.json
└── README.md
```

## 4. Prerequisites

- Java 17 or later
- Node.js 20.19+ or 22.12+ (required by the Vite version in this project)
- MySQL 8 or later
- MySQL command-line client or MySQL Workbench

## 5. Create and configure the MySQL database

Create the database in MySQL:

```sql
CREATE DATABASE ar_denial_analytics;
```

The datasource is configured in `backend/src/main/resources/application.properties` to use `localhost:3306`, database `ar_denial_analytics`, and username `root`. Set the `DB_PASSWORD` environment variable to the password for that account before running the backend. Do not put database passwords in source control.

For PowerShell, set it for the current terminal session (replace the placeholder with your local password):

```powershell
$env:DB_PASSWORD = "<your-mysql-password>"
```

## 6. Install and start the backend

Open PowerShell at the project root and run:

```powershell
cd .\backend
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`.

## 7. Install and start the frontend

In a second terminal at the project root:

```powershell
cd .\frontend
npm ci
npm run dev
```

Open the Vite URL printed in the terminal, usually `http://localhost:5173`. The frontend calls the backend at `http://localhost:8080/api`; the backend allows this development origin.

## 8. API endpoints and example requests

All endpoints use `GET` and return JSON.

| Endpoint | Purpose |
|---|---|
| `/api/analytics/payers` | Denied claim count and total billed amount per payer, sorted by amount descending |
| `/api/analytics/reasons` | Total denied billed amount per denial reason, sorted by amount descending |
| `/api/claims` | Filtered and paginated denied claims |

Example requests:

```text
http://localhost:8080/api/analytics/payers
http://localhost:8080/api/analytics/reasons
http://localhost:8080/api/claims?payer=BlueCross&page=0&size=5
http://localhost:8080/api/claims?reason=Missing%20Documentation&startDate=2026-01-01&endDate=2026-02-28&page=0&size=10
```

The claims endpoint accepts optional `payer`, `reason`, `startDate`, and `endDate` parameters. Dates must use `YYYY-MM-DD`. `page` is zero-based (default `0`); `size` defaults to `10` and must be between `1` and `100`.

## 9. Database schema and seed data

The table definition is in `backend/src/main/resources/schema.sql`. It creates `claims` with an auto-incrementing ID, payer, provider, CPT code, billed amount, denial reason, denied date, and status. A check constraint requires paid claims to have no denial reason and non-paid claims to have one.

Sample rows are in `backend/src/main/resources/data.sql`. Apply the schema first, then the seed script, using MySQL Workbench or the MySQL client. For example, from the project root in Command Prompt:

```cmd
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ar_denial_analytics"
mysql -u root -p ar_denial_analytics < backend\src\main\resources\schema.sql
mysql -u root -p ar_denial_analytics < backend\src\main\resources\data.sql
```

The current `spring.sql.init.mode=embedded` setting does not automatically run these scripts against MySQL, so apply them manually. `data.sql` inserts sample rows; run it only when you intend to add another copy of the sample data.

## 10. Database index

The schema currently contains a comment describing an index for payer, status, and denied date, but it does not contain a `CREATE INDEX` statement. The composite index below is intended to help MySQL narrow rows for payer/status/date filtering and ordering. The filter query applies `LOWER()` to payer and status, so check the query plan before expecting this regular index to speed up those comparisons; a functional index or normalized/case-insensitive data strategy may be needed. To create the documented index, run this once in the `ar_denial_analytics` database:

```sql
CREATE INDEX idx_claims_payer_status_date
    ON claims (payer, status, denied_at);
```

## 11. Validation and error handling

The claims API validates that the page number is non-negative, page size is from `1` to `100`, and the end date is not earlier than the start date. Invalid date or numeric query parameter values are also rejected. These errors return HTTP `400 Bad Request` with a JSON body containing `status`, `error`, `message`, and `timestamp`.

