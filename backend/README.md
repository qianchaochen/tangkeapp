# Backend - Spring Boot Application

## Description

A Spring Boot REST API backend with the following features:
- Spring Web for RESTful APIs
- Spring Data JPA for database operations
- Spring Security for authentication/authorization
- Spring Validation for input validation
- H2 for development, MySQL for production

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Running the Application

### Using Maven Wrapper (Recommended)

```bash
./mvnw spring-boot:run
```

On Windows:
```bash
mvnw.cmd spring-boot:run
```

### Using Maven

```bash
mvn spring-boot:run
```

### With Specific Profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=development
```

## Environment Profiles

- `development` - Uses H2 in-memory database, detailed logging
- `production` - Uses MySQL database, optimized logging

## API Endpoints

### Health Check
- **GET** `/api/health`
- Returns service status and version information
- Example response:
  ```json
  {
    "status": "UP",
    "message": "Backend service is running",
    "timestamp": "2025-12-16T01:32:00",
    "version": "0.0.1-SNAPSHOT"
  }
  ```

## Configuration

Configuration files are located in `src/main/resources/`:
- `application.yml` - Base configuration
- `application-development.yml` - Development settings
- `application-production.yml` - Production settings

## Database

### Development (H2)
- Console available at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:devdb`
- Username: `sa`
- Password: (empty)

### Production (MySQL)
Update the following in `application-production.yml` or use environment variables:
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- Update the JDBC URL with your MySQL server details

## Building

```bash
./mvnw clean package
```

The JAR file will be created in the `target/` directory.

## Running the JAR

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

## Testing

```bash
./mvnw test
```
