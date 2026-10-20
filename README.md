# Meridian
### Time Management and Goal Setting Tool

Meridian is a Java desktop app for keeping personal goals and everyday tasks together. It has a Swing interface, saves records in MySQL, and includes separate user and administrator screens.

## Features
- Account registration and sign-in
- User and administrator dashboards
- Goal and task tracking with status updates
- Due dates and task priorities
- Daily planner for due and overdue tasks
- Dashboard charts for task and goal status
- Basic admin user list and system summary

## Tech used
Java 17, Swing, JDBC, MySQL, and Maven.

## Run from source on Windows
1. Install Java 17+ and Maven.
2. Open MySQL Workbench and run `database/schema.sql`.
3. In the same PowerShell terminal, set your local database details:

```powershell
$env:MERIDIAN_DB_URL = "jdbc:mysql://localhost:3306/meridian_db?useSSL=false&serverTimezone=UTC"
$env:MERIDIAN_DB_USER = "root"
$env:MERIDIAN_DB_PASSWORD = Read-Host "MySQL password"
```

4. From the project folder, run:

```powershell
mvn clean compile
mvn exec:java
```

## Build a downloadable JAR
Run `mvn clean package`. The runnable JAR is created at `target/meridian-1.0.0.jar` and includes the MySQL JDBC driver.

To launch it from PowerShell after setting the same environment variables:

```powershell
java -jar target/meridian-1.0.0.jar
```

## Database requirement
The app needs access to a MySQL server with `meridian_db` created from `database/schema.sql`. A JAR alone does not include or host the database. Keep database passwords out of source code and GitHub.

## First account
On a fresh database, the first registered account is assigned the ADMIN role; later registrations receive USER. This is intended for a local classroom demo, not for public deployment.

## Before submission
Please test registration, login, goal/task changes, chart updates, and the admin screens in your own environment. Capture screenshots from the actual running application.

## Repository
https://github.com/Ashiishanand/meridian