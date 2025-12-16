# WeChat Mini Program Authentication Setup

## Phase 2 Implementation: Backend WeChat Mini Program Login

This document covers the implementation of WeChat authentication in the backend, including configuration, API endpoints, and testing approaches.

## Backend Components

### 1. Configuration

#### Application Properties
The following properties need to be configured in `application.properties` or via environment variables:

```properties
# WeChat Mini Program Configuration
wechat.miniapp.appid=${WECHAT_MINIAPP_APPID:wx00000000}
wechat.miniapp.secret=${WECHAT_MINIAPP_SECRET:secret}
wechat.miniapp.code2session-url=https://api.weixin.qq.com/sns/jscode2session

# JWT Configuration
jwt.secret=${JWT_SECRET:your-super-secret-key-please-change-in-production}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

#### Environment Variables
For production, set these environment variables:

```bash
export WECHAT_MINIAPP_APPID=your_appid
export WECHAT_MINIAPP_SECRET=your_secret
export JWT_SECRET=your_jwt_secret_key
export JWT_EXPIRATION=86400000  # 24 hours in milliseconds
```

### 2. API Endpoints

#### POST /api/auth/wechat/login
Authenticates a user via WeChat Mini Program login.

**Request:**
```json
{
  "code": "the_code_from_wx.login",
  "source": "weixin"  // optional, defaults to "weixin"
}
```

**Response (Success):**
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

**Response (Error):**
```json
{
  "error": "WeChat login failed: ..."
}
```

#### POST /api/auth/profile/complete
Completes the profile information for a newly logged-in customer.

**Request:**
```json
{
  "name": "User Name",
  "phone": "13800138000",
  "source": "weixin"
}
```

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "id": 1,
  "wechatOpenid": "oIuweFX_...",
  "name": "User Name",
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

#### GET /api/auth/profile
Retrieves the current authenticated customer's profile.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "id": 1,
  "wechatOpenid": "oIuweFX_...",
  "name": "User Name",
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

### 3. Authentication Middleware

The `JwtAuthenticationFilter` intercepts all HTTP requests and:
1. Extracts the JWT token from the `Authorization` header (format: `Bearer <token>`)
2. Validates the token signature and expiration
3. Extracts the customer ID from the token
4. Sets the customer ID in the request attribute for later retrieval

#### Using Authentication in Controllers

```java
@RestController
public class YourController {
    
    private final AuthenticationContext authenticationContext;
    
    @GetMapping("/protected-endpoint")
    public ResponseEntity<?> protectedEndpoint() {
        Long customerId = authenticationContext.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
        }
        // Your logic here
    }
}
```

## WeChat Developer Console Setup

### 1. Create a Mini Program
1. Go to https://mp.weixin.qq.com/
2. Sign in with your WeChat account
3. Click "Create" and select "Mini Program"
4. Fill in the required information (company name, verification, etc.)

### 2. Get Your AppID and Secret
1. In the Mini Program admin console, go to Settings -> Developer Settings
2. You'll see:
   - **AppID (小程序ID)**: Your unique identifier
   - **AppSecret (小程序密钥)**: Your secret key (keep this confidential!)

### 3. Configure Authorized Domains
1. Go to Settings -> Development Settings
2. In "Server Domain Configuration", add your backend server URL:
   - Request URL: `https://your-domain.com`
   - Upload URL: (if needed)
   - Download URL: (if needed)
   - Socket URL: (if needed)

### 4. Testing with WeChat Sandbox
1. In the Mini Program console, go to Development -> Sandbox
2. Scan the QR code with WeChat to access the sandbox environment
3. Use the sandbox AppID and Secret for development/testing
4. Note: In sandbox mode, you can use any code for testing (it won't call the real code2session endpoint)

## Testing Approaches

### 1. Mock Testing (Without Real WeChat)

For development and testing without accessing the real WeChat API, create a mock implementation:

```java
// In your test configuration
@Configuration
@Profile("test")
public class MockWechatConfig {
    
    @Bean
    public WechatService mockWechatService() {
        return new WechatService(wechatProperties, webClientBuilder, objectMapper) {
            @Override
            public Code2SessionResponse code2session(String code) {
                Code2SessionResponse response = new Code2SessionResponse();
                response.setOpenid("mock_openid_" + System.currentTimeMillis());
                response.setSession_key("mock_session_key");
                return response;
            }
        };
    }
}
```

### 2. Integration Testing

Use TestContainers with MySQL to test the full flow:

```java
@DataJpaTest
@Import({WechatService.class, CustomerAuthService.class, JwtTokenProvider.class})
class WechatAuthIntegrationTest {
    
    @Autowired
    private CustomerAuthService authService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    void testLoginCreatesNewCustomer() {
        LoginResponse response = authService.handleWechatLogin("test_code", "weixin");
        
        assertThat(response.isNewCustomer()).isTrue();
        assertThat(response.getToken()).isNotNull();
        
        Customer customer = customerRepository.findByWechatOpenid(response.getOpenid()).orElseThrow();
        assertThat(customer.getName()).isEmpty();
    }
}
```

### 3. Manual Testing with cURL

Test the login endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code","source":"weixin"}'
```

Test the profile endpoint with token:

```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer <jwt_token>"
```

### 4. Real Device Testing

1. Deploy your backend to a production-like environment with HTTPS
2. Add the domain to WeChat's authorized domains
3. Build and deploy your Mini Program
4. Use the real login flow with `wx.login()` to test

## Database

When a user logs in via WeChat:
1. A new `Customer` record is created with:
   - `wechat_openid`: The unique WeChat openid
   - `name`: Empty initially (filled during profile completion)
   - `phone`: Null initially
   - `source`: "weixin" (or custom source)
   - `first_visit_at`: Current timestamp

2. On subsequent logins with the same openid, the existing record is updated

## Security Considerations

1. **JWT Secret**: Change the default secret in production
2. **HTTPS Only**: Always use HTTPS for the backend in production
3. **Code Storage**: Never log or expose the WeChat code
4. **Token Expiration**: Adjust `jwt.expiration` based on your security requirements
5. **Rate Limiting**: Consider implementing rate limiting on the login endpoint
6. **Input Validation**: Always validate the WeChat code and user input

## Frontend Integration

The frontend (UniApp) will:
1. Call `wx.login()` to get a code
2. Send the code to `/api/auth/wechat/login`
3. Receive a JWT token
4. Store the token locally
5. Include the token in all subsequent API requests via `Authorization` header
6. Handle token refresh/renewal as needed

## Troubleshooting

### WeChat API Returns Error

Check the error code in the response:
- `40001`: Invalid AppSecret
- `40002`: Invalid grant_type
- `40029`: Invalid code
- `45011`: API frequency limit exceeded

### Token Validation Fails

1. Verify the JWT secret matches between signing and validation
2. Check that the token hasn't expired
3. Ensure the Authorization header format is correct: `Bearer <token>`

### Customer Not Created

1. Check database connection
2. Verify Flyway migrations have run
3. Look for transaction errors in logs

## Future Enhancements

1. **Token Refresh**: Implement refresh tokens for better security
2. **Session Management**: Store WeChat session_key for encrypted data decryption
3. **Mobile Phone Verification**: Use WeChat's phone number API
4. **Two-Factor Authentication**: Add optional 2FA
5. **SSO Integration**: Support other OAuth providers
