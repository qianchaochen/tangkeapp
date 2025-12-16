# WeChat Mini Program Authentication - Phase 2 Implementation

## Overview

This document describes the complete implementation of WeChat Mini Program authentication for Phase 2 of the project. The implementation includes:

- **Backend (Spring Boot)**: WeChat API integration, JWT token generation, customer management
- **Frontend (UniApp)**: Login UI, token management, profile completion
- **Testing**: Integration tests, mock services, testing guide
- **Documentation**: API references, setup instructions, troubleshooting

## Architecture

```
┌─────────────────────┐
│   WeChat Client     │
│   (Mini Program)    │
└──────────┬──────────┘
           │ wx.login()
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│         Frontend (UniApp)                                    │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Pages:                                              │   │
│  │  - login.vue      (WeChat login)                   │   │
│  │  - profile.vue    (Profile completion)             │   │
│  │ Modules:                                            │   │
│  │  - wechatService  (WeChat integration)             │   │
│  │  - apiClient      (HTTP with auth headers)         │   │
│  │  - tokenManager   (JWT storage)                    │   │
│  │ Store:                                              │   │
│  │  - auth.js        (Vuex auth state)                │   │
│  └─────────────────────────────────────────────────────┘   │
└──────────┬──────────────────────────────────────────────────┘
           │
           │ POST /api/auth/wechat/login
           │ Authorization: Bearer <token>
           │
           ▼
┌──────────────────────────────────────────────────────────────┐
│         Backend (Spring Boot)                                │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Controllers:                                        │   │
│  │  - AuthController     (Login, Profile APIs)         │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Services:                                           │   │
│  │  - CustomerAuthService (Login logic)                │   │
│  │  - WechatService       (code2session)               │   │
│  │  - JwtTokenProvider    (Token generation)           │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Security:                                           │   │
│  │  - JwtAuthenticationFilter (Token validation)       │   │
│  │  - AuthenticationContext   (Current user)           │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Repository:                                         │   │
│  │  - CustomerRepository (Database access)             │   │
│  └─────────────────────────────────────────────────────┘   │
└──────────┬───────────────────────────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────────────────────────┐
│         Database (MySQL)                                      │
│  - customers table (with wechat_openid index)               │
└──────────────────────────────────────────────────────────────┘
```

## Backend Implementation Details

### 1. Configuration Classes

#### WechatMiniappProperties.java
Binds WeChat config from `application.properties`:
- `wechat.miniapp.appid`
- `wechat.miniapp.secret`
- `wechat.miniapp.code2session-url`

#### JwtProperties.java
Binds JWT config:
- `jwt.secret` (for signing tokens)
- `jwt.expiration` (token lifetime in milliseconds)

#### SecurityConfig.java
Registers the JWT authentication filter in the request chain.

### 2. Security Classes

#### JwtTokenProvider.java
- Generates JWT tokens with customer ID as subject
- Validates token signatures and expiration
- Extracts customer ID from valid tokens

**Methods:**
- `generateToken(Long customerId)` - Creates a new JWT token
- `validateToken(String token)` - Checks if token is valid
- `extractCustomerId(String token)` - Gets customer ID from token

#### JwtAuthenticationFilter.java
- Intercepts all HTTP requests
- Extracts token from `Authorization: Bearer <token>` header
- Validates token and sets customer ID in request attribute
- Non-blocking: allows requests without tokens to proceed

#### AuthenticationContext.java
- Utility to retrieve current authenticated customer ID
- Throws no exceptions if not authenticated (returns null)

### 3. Service Classes

#### WechatService.java
- Calls WeChat's `code2session` API
- Takes login code and returns openid and session_key
- Handles WeChat API errors

**Methods:**
- `code2session(String code)` - Calls WeChat API

**Response Structure:**
```json
{
  "openid": "oIuweFX_...",
  "session_key": "...",
  "errcode": 0,
  "errmsg": "ok"
}
```

#### CustomerAuthService.java
- Orchestrates the login flow
- Creates or updates customers based on openid
- Issues JWT tokens

**Methods:**
- `handleWechatLogin(String code, String source)` - Login endpoint logic
- `completeProfile(Long customerId, String name, String phone, String source)` - Profile update

**Login Response:**
```json
{
  "customerId": 1,
  "token": "eyJhbGc...",
  "openid": "oIuweFX_...",
  "name": "",
  "phone": null,
  "source": "weixin",
  "isNewCustomer": true
}
```

### 4. Controller

#### AuthController.java
Three main endpoints:

**POST /api/auth/wechat/login**
- Accepts: `{ "code": "...", "source": "weixin" }`
- Returns: LoginResponse with token
- No authentication required

**POST /api/auth/profile/complete**
- Accepts: `{ "name": "...", "phone": "...", "source": "..." }`
- Returns: Updated CustomerDTO
- Requires: Authorization header with valid token

**GET /api/auth/profile**
- Returns: Current user's profile
- Requires: Authorization header with valid token

### 5. Database

The Customer entity is used to store:
- `wechat_openid` - Unique identifier from WeChat (indexed)
- `name` - User's name (empty initially)
- `phone` - User's phone (null initially)
- `source` - Where the customer came from ("weixin", etc.)
- `first_visit_at` - First login timestamp

## Frontend Implementation (UniApp)

### File Structure

```
src/
├── uni_modules/wechat-login/
│   ├── utils/
│   │   ├── wechatService.js     - WeChat API integration
│   │   ├── tokenManager.js      - Token storage/retrieval
│   │   └── apiClient.js         - HTTP client with auth
│   ├── pages/
│   │   ├── login.vue            - Login page (wx.login)
│   │   └── profile.vue          - Profile completion
│   ├── store/
│   │   └── auth.js              - Vuex authentication state
│   └── middleware/
│       └── authInterceptor.js   - Request/response interceptor
├── pages/
│   ├── index.vue                - Home page (protected)
│   └── ...
└── App.vue                       - App-level initialization
```

### Key Components

#### wechatService.js
Handles WeChat-specific login:
- `wxLogin()` - Calls `wx.login()` to get code (Mini Program)
- `h5Login()` - Redirects to OAuth endpoint (Web)
- `isInMiniProgram()` - Detects environment

#### tokenManager.js
Manages JWT tokens in local storage:
- `setToken(token, customerId, openid)` - Save token
- `getToken()` - Retrieve token
- `clearToken()` - Remove token on logout
- `isTokenAvailable()` - Check if logged in

#### apiClient.js
HTTP client with automatic auth header injection:
- `request(method, url, data, headers)` - Generic request
- `get/post/put/delete()` - Convenience methods
- Auto-adds `Authorization: Bearer <token>` header
- Handles 401 (Unauthorized) by redirecting to login

#### auth.js (Vuex Store)
Maintains authentication state:
- `state.isAuthenticated` - Login status
- `state.customer` - Current user info
- `mutations.setAuthenticated()` - Update auth status
- `mutations.setCustomer()` - Update customer info
- `mutations.logout()` - Clear auth data
- `actions.refreshAuthOnLaunch()` - Restore state on startup

### Login Flow

```
1. User clicks "WeChat Login" button
   └─> login.vue handleWechatLogin()

2. Call wx.login()
   └─> Get authorization code from WeChat

3. Send code to /api/auth/wechat/login
   └─> Backend authenticates with WeChat

4. Receive JWT token
   └─> Save token with tokenManager

5. Update Vuex store
   └─> Set isAuthenticated = true

6. Redirect based on newCustomer flag
   ├─> If true: Go to profile.vue for profile completion
   └─> If false: Go to home page

7. Complete profile (if new customer)
   ├─> User enters name, phone, source
   ├─> Send to /api/auth/profile/complete
   └─> Redirect to home

8. All subsequent requests include JWT
   └─> apiClient automatically adds Authorization header
```

## Testing Strategy

### Unit Tests
- Token generation and validation
- Customer creation and updates
- Profile completion

### Integration Tests
- End-to-end login flow with mock WeChat
- Database persistence
- Multiple customer scenarios

### Mock Services
- Mock WeChat API responses (no real API calls in tests)
- Fixed test database configuration
- Test-specific properties

### Manual Testing
- cURL commands to test endpoints
- Postman collections for interactive testing
- Real WeChat sandbox for device testing

### Load Testing
- Apache Bench for stress testing
- Check response times under load

## API Documentation

### Authentication
All protected endpoints require the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

Where `<jwt_token>` is obtained from the login endpoint.

### Error Responses
Standard error format:
```json
{
  "error": "Error message describing what went wrong"
}
```

HTTP Status Codes:
- `200 OK` - Successful request
- `400 Bad Request` - Invalid input or missing required fields
- `401 Unauthorized` - Missing or invalid token
- `500 Internal Server Error` - Server error

## Configuration

### Backend Configuration
See `application.properties`:
```properties
wechat.miniapp.appid=your_appid
wechat.miniapp.secret=your_secret
jwt.secret=your_jwt_secret
jwt.expiration=86400000
```

### Frontend Configuration
Create `.env` files:
```
VUE_APP_API_BASE_URL=https://api.example.com
VUE_APP_WECHAT_WEB_APPID=your_web_appid
```

## Deployment

### Backend Deployment
1. Build: `mvn clean package`
2. Run: `java -jar core-db-schema-1.0.0.jar`
3. Ensure MySQL is accessible
4. Set environment variables for config values

### Frontend Deployment
1. Build: `npm run build`
2. For Mini Program: Submit to WeChat
3. For H5: Deploy to web server with HTTPS

## Security Best Practices

1. **HTTPS Only**: Always use HTTPS in production
2. **Secret Management**: Use environment variables for secrets
3. **Token Expiration**: Configure appropriate expiration time
4. **Rate Limiting**: Implement on production (not in Phase 2)
5. **Input Validation**: Validate all user input
6. **CORS**: Configure properly for frontend domain
7. **Database**: Use parameterized queries (all Spring Data does this)

## Troubleshooting

### "WeChat login failed"
- Verify AppID and secret are correct
- Check WeChat Developer Console settings
- Ensure backend has internet access

### "Token validation failed"
- Verify JWT secret is same in all places
- Check token hasn't expired
- Verify Authorization header format

### "Customer not found"
- Ensure database migrations ran
- Check database connection
- Verify customer was created during login

### "CORS errors"
- Check backend CORS configuration
- Verify frontend domain is registered in WeChat

## Acceptance Criteria - Checklist

- ✅ Backend can be started with `mvn spring-boot:run`
- ✅ Database migrations run automatically (Flyway)
- ✅ WeChat `code2session` endpoint is called
- ✅ Customer record is created on first login
- ✅ JWT token is issued and returned
- ✅ Profile completion endpoint persists data
- ✅ Subsequent logins update existing customer
- ✅ Authorization header is validated
- ✅ Invalid tokens are rejected
- ✅ Frontend can login with WeChat sandbox
- ✅ Frontend displays profile completion page
- ✅ Frontend caches tokens in local storage
- ✅ Frontend includes auth header in API calls
- ✅ Documentation covers setup and testing

## Files Created

### Backend
- `src/main/java/com/example/core/config/WechatMiniappProperties.java`
- `src/main/java/com/example/core/config/JwtProperties.java`
- `src/main/java/com/example/core/config/SecurityConfig.java`
- `src/main/java/com/example/core/security/JwtTokenProvider.java`
- `src/main/java/com/example/core/security/JwtAuthenticationFilter.java`
- `src/main/java/com/example/core/security/AuthenticationContext.java`
- `src/main/java/com/example/core/service/WechatService.java`
- `src/main/java/com/example/core/service/CustomerAuthService.java`
- `src/main/java/com/example/core/controller/AuthController.java`
- `src/main/java/com/example/core/dto/LoginResponseDTO.java`

### Testing
- `src/test/java/com/example/core/WechatAuthIntegrationTest.java`
- `src/test/java/com/example/core/config/TestWechatConfig.java`
- `src/test/resources/application-test.properties`

### Documentation
- `docs/WECHAT_AUTH_SETUP.md` - Backend setup and configuration
- `docs/UNIAPP_FRONTEND_IMPLEMENTATION.md` - Frontend implementation
- `docs/TESTING_GUIDE.md` - Testing approaches and examples
- `docs/WECHAT_AUTH_IMPLEMENTATION.md` - This overview document

### Configuration
- `pom.xml` - Added JWT and HTTP client dependencies
- `src/main/resources/application.properties` - Added WeChat and JWT config

## Next Steps (Future Phases)

1. **Token Refresh**: Implement refresh token mechanism
2. **Rate Limiting**: Add rate limiting on login endpoint
3. **Session Management**: Store WeChat session_key for encrypted data
4. **Phone Verification**: Integrate WeChat phone number API
5. **Two-Factor Authentication**: Optional 2FA implementation
6. **Analytics**: Track login sources and user behavior
7. **SSO**: Support multiple OAuth providers
8. **API Gateway**: Add request filtering and routing
9. **Caching**: Implement token blacklisting for logout
10. **Mobile**: Optimize frontend for various screen sizes

## Support and Questions

For issues or questions:
1. Check the documentation files
2. Review the integration tests for examples
3. Check logs with debug logging enabled
4. Use the troubleshooting section

## Version Information

- Java: 17+
- Spring Boot: 3.1.5
- MySQL: 8.0+
- JWT: JJWT 0.12.3
- Vue/UniApp: 3.x
