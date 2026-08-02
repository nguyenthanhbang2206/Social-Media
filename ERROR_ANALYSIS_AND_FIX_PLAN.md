# Error Analysis and Fix Plan

## Error Summary - UPDATED

### Initial Errors (404):
1. **GET http://localhost:8080/api/v1/friend-requests/received** - 404 Not Found
2. **GET http://localhost:8080/api/v1/notifications/unread-count** - 404 Not Found
3. **{"status":404,"message":"User not found","data":null}** - User not found error

### Current Errors (500) - After UserContextFilter Fix:
1. **GET http://localhost:8080/api/v1/users/profile** - 500 Internal Server Error
2. **POST http://localhost:8080/api/v1/auth/sync** - 500 Internal Server Error
3. **GET http://localhost:8080/api/v1/posts** - 500 Internal Server Error

## Root Cause Analysis - UPDATED

### Previous Fix Applied

**UserContextFilter** was changed from `AbstractGatewayFilterFactory` to `GlobalFilter` to ensure headers are forwarded to downstream services.

### Current Issue Analysis

The 500 errors indicate that:
- **Headers ARE being forwarded** (UserContextFilter is now working)
- **Downstream services ARE receiving requests** (not 404 anymore)
- **Services are failing internally** (500 errors)

### Potential Causes for 500 Errors

#### 1. RequestHeaderUtil Not Reading Headers Correctly

**Analysis**:
- UserContextFilter (WebFlux) adds headers to the request
- RequestHeaderUtil (Servlet MVC) reads headers from RequestContextHolder
- **Issue**: Spring Cloud Gateway uses WebFlux (reactive), downstream services use Spring MVC (servlet)
- Headers should be forwarded as HTTP headers, but RequestHeaderUtil might not be reading them correctly

**Evidence**:
- `RequestHeaderUtil.getUserEmail()` uses `RequestContextHolder.getRequestAttributes()`
- This relies on the servlet request context
- If headers are not in the servlet request, it returns empty
- `UserService.getUserLogin()` throws EntityNotFoundException when email is empty

#### 2. Database Connection Issues

**Analysis**:
- Services might not be able to connect to PostgreSQL
- Connection pool might be exhausted
- Database credentials might be incorrect

**Evidence**:
- Multiple services failing with 500 errors
- Errors occur on different endpoints (profile, sync, posts)

#### 3. User Not in Database

**Analysis**:
- User logged in via Keycloak but not synced to local database
- `/api/v1/auth/sync` endpoint might be failing to create/update user
- User might not exist in local database when profile is requested

**Evidence**:
- `/api/v1/auth/sync` returns 500 (should create user in database)
- `/api/v1/users/profile` returns 500 (user might not exist)

#### 4. PostService.getNewsFeed() Database Query Failure

**Analysis**:
- `PostServiceImpl.getNewsFeed()` calls `postRepository.findAll()`
- This might fail due to database connection or table issues
- Or `populatePostTotals()` might be failing due to missing data

**Evidence**:
- `GET /api/v1/posts` returns 500
- `getNewsFeed()` does simple query but might fail on database

## Detailed Investigation Plan

### Phase 1: Add Logging to Debug Header Propagation

**Step 1.1**: Add logging to UserContextFilter
```java
// In UserContextFilter.java
log.info("UserContextFilter executing");
log.info("Adding headers: X-User-Id={}, X-User-Email={}", userId, email);
```

**Step 1.2**: Add logging to RequestHeaderUtil
```java
// In RequestHeaderUtil.java
public static Optional<String> getUserEmail() {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
    if (!(attrs instanceof ServletRequestAttributes)) {
        log.warn("RequestAttributes is not ServletRequestAttributes: {}", attrs);
        return Optional.empty();
    }
    ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
    HttpServletRequest request = servletAttrs.getRequest();
    String email = request.getHeader("X-User-Email");
    log.info("X-User-Email header value: {}", email);
    return Optional.ofNullable(email);
}
```

**Step 1.3**: Add logging to UserService.getUserLogin()
```java
// In UserServiceImpl.java
@Override
public User getUserLogin() {
    String email = RequestHeaderUtil.getUserEmail()
            .orElseThrow(() -> {
                log.error("X-User-Email header is missing or empty");
                return new EntityNotFoundException("User not found - X-User-Email header missing");
            });
    log.info("Getting user by email: {}", email);
    User user = this.getUserByEmail(email);
    return user;
}
```

### Phase 2: Check Database Connection

**Step 2.1**: Verify PostgreSQL is running
```bash
# Check if PostgreSQL is accessible
psql -U postgres -d social_media_user -c "SELECT 1"
```

**Step 2.2**: Check application.properties for database config
```bash
# Check user-service database config
cat backend/user-service/src/main/resources/application.properties
```

**Step 2.3**: Test database connection from service
- Check service logs for database connection errors
- Look for "Connection refused", "Authentication failed", etc.

### Phase 3: Verify User Sync Flow

**Step 3.1**: Check AuthController.syncUser() implementation
- Expects @RequestBody CreateUserRequest
- Calls userService.syncUserFromKeycloak(request)
- Should create user if not exists, update if exists

**Step 3.2**: Check UserService.syncUserFromKeycloak()
- Uses keycloakUserId to find user
- Creates new user if not found
- Updates existing user if found

**Step 3.3**: Test sync endpoint manually
```bash
# Test sync endpoint with sample data
curl -X POST http://localhost:8080/api/v1/auth/sync \
  -H "Content-Type: application/json" \
  -d '{
    "keycloakUserId": "test-keycloak-id",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "USER"
  }'
```

### Phase 4: Check PostService Implementation

**Step 4.1**: Review PostServiceImpl.getNewsFeed()
- Calls postRepository.findAll()
- Calls populatePostTotals() for each post
- populatePostTotals() might be calling other services that fail

**Step 4.2**: Check if PostService depends on other services
- Uses UserServiceClient, InteractionClient, GroupClient
- These Feign clients might be failing due to missing headers or service unavailability

## Expected Root Cause

Based on analysis, the most likely cause is:

**RequestHeaderUtil is not reading headers correctly because the headers added by UserContextFilter (WebFlux) are not being propagated to the servlet request context in downstream services.**

This is a common issue when mixing WebFlux (Gateway) with Servlet MVC (downstream services).

## Proposed Fix

### Option 1: Use HTTP Headers Directly (Recommended)

Instead of relying on RequestContextHolder, ensure headers are forwarded as standard HTTP headers and read them directly from HttpServletRequest.

**Verification**: Check if headers are actually in the HTTP request by adding logging.

### Option 2: Add Global Filter to Downstream Services

Add a filter in downstream services to copy HTTP headers to RequestContextHolder before the request reaches controllers.

### Option 3: Use Spring Cloud Gateway ModifyResponseBody

Use Gateway filters to modify the request body instead of headers for user context propagation.

## Next Steps

1. **Add logging** to UserContextFilter, RequestHeaderUtil, and UserService.getUserLogin()
2. **Restart API Gateway** with logging enabled
3. **Test endpoints** and check logs to verify header propagation
4. **Based on logs**, implement the appropriate fix
5. **Test authentication flow** end-to-end

## Priority

1. **HIGH**: Add logging to debug header propagation
2. **HIGH**: Restart API Gateway and test with logging
3. **HIGH**: Analyze logs to identify exact failure point
4. **MEDIUM**: Implement fix based on log analysis
5. **MEDIUM**: Test fix with full authentication flow
