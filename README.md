# Meridian
### Time Management and Goal Setting Tool

Meridian is a small Java desktop app built to make everyday planning a bit easier. The idea is straightforward: keep goals, tasks, due dates, and progress in one place instead of spreading them across notes and reminders.

This is a student project, so the focus is on clear Java code, a working database connection, and features that are easy to demonstrate and explain.

## What it includes

- Create an account and sign in.
- Separate user and administrator views.
- Add goals, set dates, update progress, and remove goals.
- Add tasks, set a priority and due date, link a task to one of your goals, and update its status.
- See a daily planner for open tasks due today or earlier.
- View dashboard counts and simple task/goal charts.
- Open an admin user list and a basic system report.

**Please test these workflows on your own machine before submitting.** The dashboard and login were previously run locally; the latest goal/task screens and charts should be compiled and checked after pulling the newest commit.

## Built with
- Java 17 or newer
- Java Swing for the desktop screens
- JDBC and MySQL for saving data
- Maven to build and run the project

## Set it up on Windows

### 1. Get the project
Clone the repository or download it from GitHub, then open the `meridian` folder in VS Code.

### 2. Prepare the database
Open MySQL Workbench, connect to your local MySQL Server, and run the script at `database/schema.sql`. It creates the `meridian_db` database and its three tables: `users`, `goals`, and `tasks`.

### 3. Set your database login
In PowerShell, set the username and password for the current terminal. Replace the example values with your local MySQL details:

```powershell
$env:MERIDIAN_DB_USER = "root"
$env:MERIDIAN_DB_PASSWORD = "your_local_mysql_password"
```

The default connection URL is:

```text
jdbc:mysql://localhost:3306/meridian_db?useSSL=false&serverTimezone=UTC
```

If your MySQL server uses a different host or port, set `MERIDIAN_DB_URL` too. Keep your real database password out of source files and GitHub.

### 4. Build and start the app
From the project folder, run:

```powershell
mvn clean compile
mvn exec:java
```

Maven downloads the project dependencies on the first run. If `mvn` is not on your PATH, run the `mvn.cmd` file from your local Maven `bin` folder instead.

## First login
On a fresh, empty database, the first registered account is assigned the `ADMIN` role. Later registrations get the `USER` role. This makes the initial classroom demo easy to set up, but it is not a suitable admin-provisioning method for a public deployment.

Use a demo password that you do not use for any other account.

## Project layout
```text
src/main/java/com/meridian/
  dao/       Database operations for goals and tasks
  model/     User, Goal, and Task data records
  service/   Registration and login logic
  ui/        Login and dashboard screens
  util/      Database connection and password hashing
database/schema.sql
docs/architecture.md
docs/SUBMISSION_CHECKLIST.md
pom.xml
```

## A few implementation notes
- Passwords are stored as PBKDF2-HMAC-SHA256 hashes with random salts, not as plain text.
- SQL values are passed using prepared statements.
- Goal and task queries are scoped to the signed-in user's ID.
- The dashboard uses lightweight Swing-drawn charts, so there is no extra chart library to install.

## Before handing it in
1. Run `mvn clean compile` and then launch the app.
2. Sign in as a normal user, create a goal and task, change their statuses, and confirm the dashboard counts/charts update.
3. Sign in with the admin account and check the user list and system report.
4. Capture screenshots from the real running app. Do not label a mockup as an actual screenshot.
5. Check the GitHub repository for passwords, local paths, or private user information before submitting.

## Repository
https://github.com/ashiishanand/meridian