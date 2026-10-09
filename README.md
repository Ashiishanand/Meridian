# Meridian
## Time Management and Goal Setting Tool

Meridian is a Java desktop project for organizing goals and tasks. It uses Java Swing for its interface, JDBC for database access, and MySQL for persistent storage. The application includes role-aware user and administrator dashboards.

> **Development status:** Initial foundation committed. Authentication, registration, initial dashboard, administrator user listing/report counts, database schema, and password hashing are present. Goal/task CRUD, account activation controls, and planner workflows are still in development and must not yet be considered complete.

## Technology
- Java 17+
- Maven
- Java Swing
- MySQL 8+
- MySQL Workbench
- MySQL Connector/J (managed by Maven)

## Windows setup (VS Code)

1. Install a JDK (17 or later), Maven, MySQL Server, and Visual Studio Code.
2. In VS Code, install the **Extension Pack for Java**.
3. Open MySQL Workbench and connect to your local MySQL Server.
4. Open and execute `database/schema.sql` in a Workbench SQL tab.
5. Set database credentials in a Windows terminal. Replace the example password with your MySQL password:

   PowerShell:
   ```powershell
   $env:MERIDIAN_DB_USER = "root"
   $env:MERIDIAN_DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
   ```

   The default connection URL is `jdbc:mysql://localhost:3306/meridian_db?useSSL=false&serverTimezone=UTC`. Override it with `MERIDIAN_DB_URL` if your MySQL server uses a different host or port.

6. From the repository root, run:

   ```powershell
   mvn clean compile
   mvn exec:java
   ```

   Maven downloads the JDBC driver and required build plugins.

## First run and roles
- Use **Create account** to register.
- In a newly created, empty database, the first account receives the `ADMIN` role; later accounts receive `USER`.
- Sign in using the credentials you registered.
- Do not use a real or reused password for a student demo.
- The first-account rule is suitable only for a local demo database. A deployed application should provision administrators through a controlled setup procedure.

## Database tables
- `users`: identity, password hash/salt, role, and account status.
- `goals`: personal goals and target dates.
- `tasks`: task deadlines, priorities, statuses, and optional goal association.

## Project structure
```text
src/main/java/com/meridian/
  Main.java
  model/User.java
  service/AuthService.java
  ui/LoginFrame.java
  ui/DashboardFrame.java
  util/DatabaseConnection.java
  util/PasswordUtil.java
database/schema.sql
docs/architecture.md
pom.xml
```

## Security notes
- Passwords are hashed with PBKDF2-HMAC-SHA256 and per-password random salts.
- SQL queries use prepared statements.
- Never commit database passwords or personal account data.
- Admin UI visibility is role-based. Any future admin-changing operations must also enforce authorization in service/database operations, not only by hiding UI controls.

## Current limitations
- Goal and task screens are not implemented yet.
- The admin panel currently provides a user listing and basic aggregate report, but account activation/deactivation is not implemented yet.
- Automated tests and submission presentation materials are still to be added.

## Repository
https://github.com/ashiishanand/meridian
