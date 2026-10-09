# Meridian Architecture

Meridian is a Java Swing desktop application backed by MySQL through JDBC.

## Layers
- UI: Swing login, registration, user dashboard, and administrator dashboard.
- Service: Authentication, validation, and role assignment.
- Model: User domain record.
- Utility: JDBC connection management and PBKDF2 password hashing.
- Database: MySQL tables for users, goals, and tasks.

## Initial account rule
The first account registered in a fresh database is assigned the ADMIN role. Subsequent registrations receive USER. For a shared or production deployment, initial administrator provisioning should be restricted to a controlled setup process.

## Data relationships
- One user can own many goals.
- One user can own many tasks.
- A goal can have many tasks.
- Deleting a user cascades to their goals and tasks.
- Deleting a goal detaches its tasks rather than deleting them.
