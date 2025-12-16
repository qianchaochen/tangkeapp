# Project Name

A full-stack application with UniApp frontend and Spring Boot backend.

## Project Structure

```
.
├── frontend/          # UniApp frontend application
├── backend/           # Spring Boot backend application
├── .editorconfig      # Editor configuration
├── .gitignore         # Git ignore rules
└── README.md          # This file
```

## Prerequisites

### Frontend
- Node.js >= 16.x
- npm >= 8.x or yarn >= 1.x

### Backend
- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd <project-directory>
```

### 2. Backend Setup

Navigate to the backend directory and run:

```bash
cd backend
./mvnw spring-boot:run
```

Or on Windows:

```bash
cd backend
mvnw.cmd spring-boot:run
```

The backend server will start on `http://localhost:8080`

**Health Check Endpoint:** `http://localhost:8080/api/health`

### 3. Frontend Setup

Navigate to the frontend directory, install dependencies, and run:

```bash
cd frontend
npm install
npm run dev:h5
```

For other platforms:
- **H5 (Web):** `npm run dev:h5`
- **MP-WeChat:** `npm run dev:mp-weixin`
- **App:** `npm run dev:app`

The frontend will be available at `http://localhost:5173` (or another port if specified)

## Environment Configuration

### Backend

Environment-specific configuration files are located in `backend/src/main/resources/`:

- `application.yml` - Base configuration
- `application-development.yml` - Development environment
- `application-production.yml` - Production environment

To switch environments, use the `spring.profiles.active` property:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=development
```

### Frontend

Environment files are located in the `frontend/` directory:

- `.env.development` - Development environment variables
- `.env.production` - Production environment variables

Variables are automatically loaded based on the build mode.

## Configuration Placeholders

### Backend (`application-{env}.yml`)
- Database connection: `spring.datasource.*`
- WeChat configuration: `wechat.app-id`, `wechat.app-secret`
- Alipay configuration: `alipay.app-id`, `alipay.private-key`

### Frontend (`.env.{mode}`)
- `VITE_API_BASE_URL` - Backend API base URL
- `VITE_WECHAT_APP_ID` - WeChat Mini Program App ID
- `VITE_ALIPAY_APP_ID` - Alipay Mini Program App ID

## API Documentation

Once the backend is running, API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (if configured)

## Build for Production

### Backend

```bash
cd backend
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

### Frontend

```bash
cd frontend
npm run build:h5
```

Built files will be in `frontend/dist/build/h5/`

## Troubleshooting

### Backend Issues

- **Port 8080 already in use:** Change the port in `application.yml` by setting `server.port`
- **Database connection errors:** Verify database configuration in `application-{env}.yml`

### Frontend Issues

- **Dependencies not installing:** Try deleting `node_modules` and `package-lock.json`, then run `npm install` again
- **Build errors:** Clear the cache with `npm run clean` (if available) or delete the `unpackage` directory

## Contributing

1. Create a feature branch from `main`
2. Make your changes
3. Submit a pull request

## License

[License Type] - See LICENSE file for details
