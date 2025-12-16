# Documentation Index

This directory contains complete documentation for the Core Database Schema and WeChat Mini Program Authentication implementation.

## Quick Links

### For Getting Started
1. **[Quick Start - WeChat Auth](./QUICK_START_WECHAT_AUTH.md)** - 5-minute setup guide
2. **[Database Schema Documentation](./db/README.md)** - Database structure and relationships
3. **[WeChat Auth Setup](./WECHAT_AUTH_SETUP.md)** - Detailed configuration guide

### For Implementation
1. **[WeChat Auth Implementation Overview](./WECHAT_AUTH_IMPLEMENTATION.md)** - Architecture and design
2. **[UniApp Frontend Implementation](./UNIAPP_FRONTEND_IMPLEMENTATION.md)** - Frontend code templates
3. **[Testing Guide](./TESTING_GUIDE.md)** - Testing approaches and examples

### For Reference
- **API Documentation**: See [WECHAT_AUTH_SETUP.md](./WECHAT_AUTH_SETUP.md#api-endpoints)
- **Configuration Reference**: See [WECHAT_AUTH_SETUP.md](./WECHAT_AUTH_SETUP.md#configuration)
- **Troubleshooting**: See [QUICK_START_WECHAT_AUTH.md](./QUICK_START_WECHAT_AUTH.md#troubleshooting)

## File Organization

```
docs/
├── README.md                                    # This file
├── db/
│   └── README.md                               # Database schema ERD and documentation
├── QUICK_START_WECHAT_AUTH.md                  # 5-minute quick start guide
├── WECHAT_AUTH_SETUP.md                        # Backend setup and configuration
├── WECHAT_AUTH_IMPLEMENTATION.md               # Architecture and design overview
├── UNIAPP_FRONTEND_IMPLEMENTATION.md           # Frontend implementation templates
└── TESTING_GUIDE.md                            # Testing approaches and examples
```

## What This Project Includes

### Backend (Spring Boot)
- ✅ WeChat Mini Program authentication via code2session API
- ✅ JWT token generation and validation
- ✅ Customer management and persistence
- ✅ Profile completion functionality
- ✅ Authentication middleware for protected APIs
- ✅ Integration tests with mocked WeChat service
- ✅ Complete configuration management

### Frontend (UniApp)
- ✅ WeChat login page with wx.login() integration
- ✅ Profile completion page
- ✅ Token storage and management
- ✅ API client with automatic auth header injection
- ✅ Vuex store for authentication state
- ✅ H5 fallback for OAuth login

### Database
- ✅ Complete schema with proper indexes
- ✅ Flyway migrations for version control
- ✅ Entity-Relationship Diagram (ERD)
- ✅ Customer records with WeChat openid
- ✅ Account and transaction tracking

## Getting Started - Choose Your Path

### 👤 I want to set up the backend
1. Start with [QUICK_START_WECHAT_AUTH.md](./QUICK_START_WECHAT_AUTH.md)
2. Review [WECHAT_AUTH_SETUP.md](./WECHAT_AUTH_SETUP.md) for detailed configuration
3. Run tests using [TESTING_GUIDE.md](./TESTING_GUIDE.md)

### 📱 I want to build the frontend
1. Read [UNIAPP_FRONTEND_IMPLEMENTATION.md](./UNIAPP_FRONTEND_IMPLEMENTATION.md)
2. Understand the architecture in [WECHAT_AUTH_IMPLEMENTATION.md](./WECHAT_AUTH_IMPLEMENTATION.md#frontend-implementation-uniapp)
3. Test with the backend using [TESTING_GUIDE.md](./TESTING_GUIDE.md)

### 🧪 I want to write tests
1. Review test examples in [TESTING_GUIDE.md](./TESTING_GUIDE.md)
2. Check integration tests in `src/test/java/com/example/core/WechatAuthIntegrationTest.java`
3. See mock configuration in `src/test/java/com/example/core/config/TestWechatConfig.java`

### 🚀 I want to deploy to production
1. Check [WECHAT_AUTH_SETUP.md](./WECHAT_AUTH_SETUP.md#security-considerations)
2. Review environment variables section
3. Set up WeChat Developer Console per the guide

### 🔧 I'm debugging an issue
1. Check [QUICK_START_WECHAT_AUTH.md#troubleshooting](./QUICK_START_WECHAT_AUTH.md#troubleshooting)
2. Review [TESTING_GUIDE.md#debugging](./TESTING_GUIDE.md#debugging)
3. Check logs with debug logging enabled

## Key Features

### Authentication Flow
- User logs in with WeChat Mini Program
- Backend validates code with WeChat API
- JWT token is generated and returned
- Token is stored locally on client
- All subsequent API requests include token
- Protected endpoints validate token before responding

### Database Management
- Automatic database schema creation via Flyway
- Customer records linked to WeChat openid
- Automatic timestamp management
- Proper indexing for performance
- Foreign key constraints and cascades

### Security
- JWT tokens with configurable expiration
- Secure token validation on every request
- HTTPS-only for production
- Input validation on all endpoints
- No sensitive data in logs

### Testing
- Mocked WeChat API for unit and integration tests
- Full database testing with test data
- Integration test suite included
- Manual testing with curl examples
- Real device testing with sandbox

## API Endpoints

### Public Endpoints
- `POST /api/auth/wechat/login` - Login with WeChat code

### Protected Endpoints (require JWT token)
- `POST /api/auth/profile/complete` - Complete user profile
- `GET /api/auth/profile` - Get current user profile

See [WECHAT_AUTH_SETUP.md](./WECHAT_AUTH_SETUP.md#api-endpoints) for detailed request/response formats.

## Environment Variables

```bash
# WeChat Configuration
WECHAT_MINIAPP_APPID=your_app_id
WECHAT_MINIAPP_SECRET=your_app_secret

# JWT Configuration
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Database (if not using defaults)
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
```

## Directory Structure

```
project/
├── src/
│   ├── main/
│   │   ├── java/com/example/core/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business services
│   │   │   ├── security/        # Security and JWT
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data access
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── mapper/          # Entity mappers
│   │   │   ├── enums/           # Enumerations
│   │   │   └── Application.java # Entry point
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Flyway migrations
│   └── test/
│       ├── java/com/example/core/
│       │   ├── WechatAuthIntegrationTest.java
│       │   └── config/
│       └── resources/
│           └── application-test.properties
├── docs/
│   ├── README.md                              # This file
│   ├── db/README.md                           # Database documentation
│   ├── QUICK_START_WECHAT_AUTH.md             # 5-minute quickstart
│   ├── WECHAT_AUTH_SETUP.md                   # Backend setup
│   ├── WECHAT_AUTH_IMPLEMENTATION.md          # Architecture
│   ├── UNIAPP_FRONTEND_IMPLEMENTATION.md      # Frontend
│   └── TESTING_GUIDE.md                       # Testing
├── pom.xml
└── .gitignore
```

## Technologies Used

### Backend
- **Java 17** - Latest LTS Java version
- **Spring Boot 3.1.5** - Modern Spring framework
- **Spring Data JPA** - ORM with Hibernate
- **MySQL 8.0** - Relational database
- **Flyway** - Database migrations
- **JJWT 0.12.3** - JWT library
- **Spring WebFlux** - Reactive HTTP client

### Frontend
- **UniApp** - Multi-platform framework
- **Vue 3** - JavaScript framework
- **Vuex** - State management
- **localStorage** - Token persistence

### Testing
- **JUnit 5** - Testing framework
- **Spring Boot Test** - Integration testing
- **TestContainers** - Database testing
- **Mockito** - Mocking framework

## Common Tasks

### Start the Backend
```bash
mvn spring-boot:run
```

### Build the Backend
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Test a Specific Endpoint
```bash
curl -X POST http://localhost:8080/api/auth/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code","source":"weixin"}'
```

### View Database
```bash
mysql -u root -p core_db -e "SELECT * FROM customers;"
```

### Check Application Logs
```bash
tail -f target/logs/application.log
```

## Support and Help

1. **Quick issues**: Check [Troubleshooting section](./QUICK_START_WECHAT_AUTH.md#troubleshooting)
2. **API questions**: See [API Documentation](./WECHAT_AUTH_SETUP.md#api-endpoints)
3. **Implementation help**: Review [Architecture](./WECHAT_AUTH_IMPLEMENTATION.md) and [Frontend templates](./UNIAPP_FRONTEND_IMPLEMENTATION.md)
4. **Testing**: Consult [Testing Guide](./TESTING_GUIDE.md)
5. **Database**: Check [Database Documentation](./db/README.md)

## Phase 2 Checklist

- ✅ WeChat code2session integration
- ✅ JWT token generation and validation
- ✅ Customer creation and profile management
- ✅ Authentication middleware
- ✅ Protected API endpoints
- ✅ Integration tests with mocking
- ✅ Complete backend implementation
- ✅ Frontend implementation templates
- ✅ Comprehensive documentation
- ✅ Testing guide with examples
- ✅ Quick start guide
- ✅ Configuration management
- ✅ Error handling
- ✅ Security best practices

## Future Enhancements

See [WECHAT_AUTH_IMPLEMENTATION.md#next-steps-future-phases](./WECHAT_AUTH_IMPLEMENTATION.md#next-steps-future-phases) for planned improvements.

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Complete - Phase 2 Implementation
