# Quick Start - WeChat Authentication Implementation

## For Developers

Get up and running with WeChat authentication in 5 minutes.

## Prerequisites

- Java 17+
- MySQL 8.0+ (running locally or accessible)
- Maven 3.8+
- curl or Postman (for testing)

## Step 1: Setup Database

```bash
# Create database
mysql -u root -p -e "CREATE DATABASE core_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Grant privileges (if using non-root user)
mysql -u root -p -e "GRANT ALL PRIVILEGES ON core_db.* TO 'your_user'@'localhost' IDENTIFIED BY 'your_password';"
```

## Step 2: Configure Backend

Edit `src/main/resources/application.properties`:

```properties
# Database (update as needed)
spring.datasource.url=jdbc:mysql://localhost:3306/core_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password

# WeChat (for testing, use mock values)
wechat.miniapp.appid=test_appid
wechat.miniapp.secret=test_secret

# JWT (use secure value in production)
jwt.secret=my-super-secret-jwt-key-change-in-production
jwt.expiration=86400000
```

## Step 3: Start Backend

```bash
# Terminal 1: Start the backend
mvn spring-boot:run

# Or after building
mvn clean package
java -jar target/core-db-schema-1.0.0.jar
```

You should see:
```
Started Application in 5.123 seconds
Application running at http://localhost:8080
```

## Step 4: Test Login Endpoint

```bash
# Terminal 2: Test WeChat login
curl -X POST http://localhost:8080/api/auth/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code","source":"weixin"}' | jq .
```

**Expected Response:**
```json
{
  "customerId": 1,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "openid": "mock_openid_test_code_0",
  "name": "",
  "phone": null,
  "source": "weixin",
  "isNewCustomer": true
}
```

## Step 5: Test Protected Endpoint

```bash
# Save the token
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Get profile (requires token)
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer $TOKEN" | jq .
```

## Step 6: Complete Profile

```bash
# Complete profile information
curl -X POST http://localhost:8080/api/auth/profile/complete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "John Doe",
    "phone": "13800138000",
    "source": "weixin"
  }' | jq .
```

## Step 7: Verify in Database

```bash
# Check customer was created
mysql -u root -p core_db -e "SELECT id, wechat_openid, name, phone, source FROM customers LIMIT 5;"
```

You should see your created customer.

## Key Files to Review

1. **Backend Logic**:
   - `src/main/java/com/example/core/service/CustomerAuthService.java` - Main login logic
   - `src/main/java/com/example/core/controller/AuthController.java` - API endpoints
   - `src/main/java/com/example/core/security/JwtTokenProvider.java` - Token management

2. **Configuration**:
   - `src/main/resources/application.properties` - Config values
   - `src/main/java/com/example/core/config/` - Config classes

3. **Testing**:
   - `src/test/java/com/example/core/WechatAuthIntegrationTest.java` - Integration tests
   - `docs/TESTING_GUIDE.md` - Testing approaches

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=WechatAuthIntegrationTest

# Run with coverage
mvn test jacoco:report
```

## Common Commands

```bash
# Clean build
mvn clean install

# Run with debug logging
LOGGING_LEVEL_COM_EXAMPLE_CORE=DEBUG mvn spring-boot:run

# Package for deployment
mvn clean package -DskipTests

# View logs
tail -f target/logs/application.log
```

## Frontend Integration

For the UniApp frontend, implement the login flow as shown in `docs/UNIAPP_FRONTEND_IMPLEMENTATION.md`:

1. Call `wx.login()` to get code
2. POST code to `/api/auth/wechat/login`
3. Store returned token in localStorage
4. Include token in all subsequent requests: `Authorization: Bearer <token>`
5. If new customer, show profile completion page
6. On profile completion, redirect to app home

## Real WeChat Integration

To test with real WeChat Mini Program:

1. Get AppID and Secret from WeChat Developer Console
2. Update `application.properties`:
   ```properties
   wechat.miniapp.appid=your_real_appid
   wechat.miniapp.secret=your_real_secret
   ```
3. Build and deploy to production server with HTTPS
4. Add backend domain to WeChat Developer Console authorized domains
5. Build Mini Program with your production backend URL
6. Test with real WeChat sandbox

## Troubleshooting

**Database connection refused:**
```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1;"

# Check database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'core_db';"
```

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081

# Or kill existing process
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

**Token validation fails:**
- Verify the Authorization header format: `Bearer <token>`
- Check that JWT secret in properties matches what's used to generate token
- Verify token hasn't expired (default 24 hours)

**Customer not created:**
- Check database connection and migrations ran
- Look for errors in logs: `grep -i error target/logs/application.log`
- Verify `customers` table exists: `mysql -u root -p core_db -e "SHOW TABLES;"`

## Next Steps

1. Read `docs/WECHAT_AUTH_SETUP.md` for detailed configuration
2. Review `docs/TESTING_GUIDE.md` for comprehensive testing
3. Check `docs/UNIAPP_FRONTEND_IMPLEMENTATION.md` for frontend code
4. Implement profile page in your Mini Program UI
5. Test with real WeChat sandbox

## Architecture Overview

```
User Device (WeChat Mini Program)
    ↓
    ├─ Call wx.login() → get code
    ├─ Send code to backend /api/auth/wechat/login
    └─ Receive JWT token
    
Backend (Spring Boot)
    ├─ Receive code
    ├─ Call WeChat code2session API
    ├─ Get user openid
    ├─ Find or create customer
    ├─ Generate JWT token
    └─ Return token to client
    
Subsequent API Calls
    ├─ Frontend sends: Authorization: Bearer <jwt_token>
    ├─ Backend validates token via JwtAuthenticationFilter
    ├─ AuthenticationContext retrieves current user ID
    └─ Business logic executes for authenticated user

Database
    └─ Customer records persisted with wechat_openid
```

## File Structure

```
src/main/java/com/example/core/
├── controller/
│   └── AuthController.java         # API endpoints
├── service/
│   ├── CustomerAuthService.java    # Login logic
│   └── WechatService.java          # WeChat API
├── security/
│   ├── JwtTokenProvider.java       # Token generation
│   ├── JwtAuthenticationFilter.java # Token validation
│   └── AuthenticationContext.java  # Get current user
├── config/
│   ├── JwtProperties.java          # JWT config
│   ├── WechatMiniappProperties.java# WeChat config
│   └── SecurityConfig.java         # Spring config
└── entity/
    └── Customer.java               # (existing)

docs/
├── QUICK_START_WECHAT_AUTH.md     # This file
├── WECHAT_AUTH_SETUP.md            # Detailed setup
├── WECHAT_AUTH_IMPLEMENTATION.md   # Architecture
├── UNIAPP_FRONTEND_IMPLEMENTATION.md # Frontend
└── TESTING_GUIDE.md                # Testing
```

## Support

- Check detailed docs in `docs/` folder
- Review integration tests in `src/test/java/`
- Check logs with debug logging enabled
- Verify database with MySQL command line

## What's Implemented ✅

- ✅ WeChat code2session integration
- ✅ JWT token generation and validation
- ✅ Customer creation and persistence
- ✅ Profile completion API
- ✅ Authentication middleware
- ✅ Integration tests with mocked WeChat
- ✅ Complete documentation
- ✅ Frontend implementation templates
- ✅ Testing guide with examples
- ✅ Configuration management

## Ready to Go!

You now have a working WeChat authentication backend. Implement the frontend according to `UNIAPP_FRONTEND_IMPLEMENTATION.md` and you're ready to ship!
