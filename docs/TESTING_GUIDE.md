# Testing Guide - WeChat Authentication Phase 2

This guide covers testing the WeChat authentication implementation locally and in production.

## Local Development Setup

### Prerequisites

- Java 17+
- MySQL 8.0+
- Maven 3.8+
- curl or Postman

### 1. Database Setup

```bash
# Create the database
mysql -u root -p -e "CREATE DATABASE core_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Grant privileges
mysql -u root -p -e "GRANT ALL PRIVILEGES ON core_db.* TO 'root'@'localhost';"
```

### 2. Backend Startup

```bash
# Clone the repository and navigate to the project
cd /path/to/project

# Update application.properties with your database credentials
# Default: mysql://localhost:3306/core_db with user root and password

# Start the application
mvn spring-boot:run

# Or compile and run
mvn clean package
java -jar target/core-db-schema-1.0.0.jar
```

The application should start on `http://localhost:8080`

### 3. Verify Database Migrations

Check that Flyway migrations ran successfully:

```bash
mysql -u root -p core_db -e "SELECT * FROM flyway_schema_history;"
```

You should see:
- V1__Create_initial_schema.sql - Created tables
- V2__Insert_seed_data.sql - Inserted seed data

Check that the customers table exists:

```bash
mysql -u root -p core_db -e "DESCRIBE customers;"
```

## Testing Endpoints

### 1. Mock WeChat Login (Without Real WeChat Code)

**For testing purposes, you can mock the WeChat code2session response:**

```bash
# Test login with mock code
curl -X POST http://localhost:8080/api/auth/wechat/login \
  -H "Content-Type: application/json" \
  -d '{
    "code": "test_code_12345",
    "source": "weixin"
  }' 2>/dev/null | jq .
```

**Expected Response:**
```json
{
  "customerId": 1,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "openid": "oIuweFX_mock_openid_12345",
  "name": "",
  "phone": null,
  "source": "weixin",
  "isNewCustomer": true
}
```

### 2. Verify Customer Created in Database

```bash
mysql -u root -p core_db -e "SELECT * FROM customers WHERE source = 'weixin';"
```

You should see the newly created customer record.

### 3. Complete Profile

```bash
# Use the token from the login response
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -X POST http://localhost:8080/api/auth/profile/complete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "name": "张三",
    "phone": "13800138000",
    "source": "weixin"
  }' 2>/dev/null | jq .
```

**Expected Response:**
```json
{
  "id": 1,
  "wechatOpenid": "oIuweFX_mock_openid_12345",
  "name": "张三",
  "phone": "13800138000",
  "source": "weixin",
  "distance": null,
  "firstVisitAt": "2024-01-01T10:00:00",
  "label": null,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:05:00",
  "account": null
}
```

### 4. Get Customer Profile

```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer ${TOKEN}" 2>/dev/null | jq .
```

### 5. Test Subsequent Logins (Same OpenID)

```bash
curl -X POST http://localhost:8080/api/auth/wechat/login \
  -H "Content-Type: application/json" \
  -d '{
    "code": "test_code_12345",
    "source": "weixin"
  }' 2>/dev/null | jq '.isNewCustomer'
```

Should return `false` on the second login.

### 6. Test Authorization Header Middleware

**Request without token:**
```bash
curl -X GET http://localhost:8080/api/auth/profile 2>/dev/null | jq .
```

**Request with invalid token:**
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer invalid_token_xyz" 2>/dev/null | jq .
```

Both should return an error (not authenticated).

## Using Postman

1. **Create a new POST request** to `http://localhost:8080/api/auth/wechat/login`
2. **Set Body to raw JSON:**
   ```json
   {
     "code": "test_code",
     "source": "weixin"
   }
   ```
3. **Send request** and save the token from response
4. **Create new GET request** to `http://localhost:8080/api/auth/profile`
5. **Set Authorization header** in the Headers tab: `Bearer <token>`
6. **Send request** to verify authentication works

## WeChat Sandbox Testing

### Setup Sandbox Account

1. In WeChat Mini Program Admin Console, go to Development -> Sandbox
2. Scan QR code with personal WeChat account
3. You'll get a sandbox AppID and test account

### Get Real WeChat Code

1. In your Mini Program code, call `wx.login()`
2. This returns a real code
3. Use this code with your backend

### Using Sandbox

```javascript
// In your Mini Program
wx.login({
  success: (res) => {
    // Use res.code with your backend
    const code = res.code;
    
    // For sandbox testing, you can use:
    // code = "mock_code_for_testing"
  }
});
```

## Integration Tests

Create a test file:

**WechatAuthIntegrationTest.java**
```java
package com.example.core;

import com.example.core.entity.Customer;
import com.example.core.repository.CustomerRepository;
import com.example.core.service.CustomerAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "wechat.miniapp.appid=test_appid",
    "wechat.miniapp.secret=test_secret",
    "jwt.secret=test_secret_key"
})
class WechatAuthIntegrationTest {

    @Autowired
    private CustomerAuthService authService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testNewCustomerLogin() {
        CustomerAuthService.LoginResponse response = 
            authService.handleWechatLogin("test_code", "weixin");

        assertNotNull(response.getToken());
        assertTrue(response.isNewCustomer());
        assertNotNull(response.getCustomerId());
        
        Customer customer = customerRepository.findById(response.getCustomerId())
            .orElse(null);
        assertNotNull(customer);
        assertEquals("weixin", customer.getSource());
    }

    @Test
    void testExistingCustomerLogin() {
        // First login
        CustomerAuthService.LoginResponse firstLogin = 
            authService.handleWechatLogin("test_code_2", "weixin");
        assertTrue(firstLogin.isNewCustomer());

        // Second login with same code
        CustomerAuthService.LoginResponse secondLogin = 
            authService.handleWechatLogin("test_code_2", "weixin");
        assertFalse(secondLogin.isNewCustomer());
        assertEquals(firstLogin.getCustomerId(), secondLogin.getCustomerId());
    }

    @Test
    void testCompleteProfile() {
        // Login first
        CustomerAuthService.LoginResponse loginResponse = 
            authService.handleWechatLogin("test_code_3", "weixin");

        // Complete profile
        Customer updated = authService.completeProfile(
            loginResponse.getCustomerId(),
            "Test User",
            "13800138000",
            "weixin"
        );

        assertEquals("Test User", updated.getName());
        assertEquals("13800138000", updated.getPhone());
    }
}
```

Run the test:
```bash
mvn test -Dtest=WechatAuthIntegrationTest
```

## Load Testing

Test the login endpoint under load:

```bash
# Using Apache Bench
ab -n 100 -c 10 -p payload.json -T application/json \
  http://localhost:8080/api/auth/wechat/login

# Where payload.json contains:
# {
#   "code": "test_code",
#   "source": "weixin"
# }
```

## Debugging

### Enable Debug Logging

In `application.properties`:
```properties
logging.level.com.example.core=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Check Logs

```bash
# View real-time logs
tail -f target/logs/application.log

# Search for errors
grep -i error target/logs/application.log
```

### Database Inspection

```bash
# Check customers created
mysql -u root -p core_db -e \
  "SELECT id, wechat_openid, name, phone, source, created_at FROM customers;"

# Check account table
mysql -u root -p core_db -e "SELECT * FROM accounts;"

# Check transactions
mysql -u root -p core_db -e "SELECT * FROM transactions LIMIT 5;"
```

## Acceptance Criteria Checklist

- [ ] Backend can be started with `mvn spring-boot:run`
- [ ] Database migrations run automatically
- [ ] `/api/auth/wechat/login` endpoint accepts code and source
- [ ] New customer record is created on first login
- [ ] JWT token is returned in login response
- [ ] Customer can call `/api/auth/profile/complete` with token
- [ ] Profile is updated in database
- [ ] Subsequent logins update existing customer (not new)
- [ ] Authorization header is checked in subsequent requests
- [ ] Invalid tokens are rejected
- [ ] Customer data is correctly persisted and retrieved

## Common Issues and Solutions

### Issue: "WeChat login failed: invalid code"
**Solution:** Make sure you're using the code from `wx.login()`, not a random string. For testing, you need the real WeChat code or setup proper mocking.

### Issue: Database connection refused
**Solution:** Verify MySQL is running and credentials are correct in `application.properties`

### Issue: Migrations not running
**Solution:** Check Flyway logs and ensure migration files are in `src/main/resources/db/migration/`

### Issue: Token validation fails
**Solution:** Verify JWT secret is consistent and token hasn't expired

### Issue: CORS errors on frontend
**Solution:** Add CORS configuration to Spring Boot (if frontend is on different domain)

## Performance Metrics

Expected response times (local development):
- Login: < 500ms
- Profile Complete: < 200ms  
- Profile Retrieval: < 100ms
- Token Validation: < 10ms

## Security Testing

1. **Token Expiration**: Verify tokens expire after configured time
2. **Secret Rotation**: Test with different JWT secrets
3. **Rate Limiting**: (TODO) Implement on production
4. **Input Validation**: Test with malformed JSON
5. **SQL Injection**: Verify all queries use parameterized statements

## Continuous Integration

These tests will be run automatically via GitHub Actions/GitLab CI:
- Unit tests with `mvn test`
- Integration tests with database
- Linting and code quality checks
- Security scanning
