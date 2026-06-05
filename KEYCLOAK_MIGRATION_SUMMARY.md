# Keycloak Migration Summary

## Overview
This document summarizes the Keycloak migration implementation for the Social Media Platform. The migration follows a production-ready architecture with hybrid JWT validation (gateway + service-level) using JWKS caching.

## Files Created

### Infrastructure
1. **docker-compose.keycloak.yml** - Docker compose for Keycloak with PostgreSQL database
2. **keycloak-realm-config.json** - Keycloak realm configuration with clients, roles, and permissions

### API Gateway
1. **backend/api-gateway/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/api-gateway/src/main/java/com/nguyenthanhbang/Social_media/gateway/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation
3. **backend/api-gateway/src/main/java/com/nguyenthanhbang/Social_media/gateway/filter/UserContextFilter.java** - Filter to extract user info from JWT and forward to services
4. **backend/api-gateway/src/main/resources/gateway-config-keycloak.yml** - Gateway route configuration with UserContextFilter
5. **backend/api-gateway/pom.xml** - Updated with OAuth2 client dependency

### User Service
1. **backend/user-service/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/user-service/src/main/java/com/nguyenthanhbang/Social_media/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation
3. **backend/user-service/pom.xml** - Updated with OAuth2 client dependency

### Post Service
1. **backend/post-service/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/post-service/src/main/java/com/nguyenthanhbang/Social_media/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation

### Group Service
1. **backend/group-service/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/group-service/src/main/java/com/nguyenthanhbang/Social_media/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation

### Interaction Service
1. **backend/interaction-service/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/interaction-service/src/main/java/com/nguyenthanhbang/Social_media/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation

### Notification Service
1. **backend/notification-service/src/main/resources/application-keycloak.yml** - Keycloak OAuth2 configuration
2. **backend/notification-service/src/main/java/com/nguyenthanhbang/Social_media/config/SecurityConfigurationKeycloak.java** - Security config with Keycloak JWT validation

### Frontend
1. **frontend/src/config/keycloak.js** - Keycloak configuration
2. **frontend/src/utils/keycloak.js** - Keycloak utility functions and service
3. **frontend/src/utils/axiosWithKeycloak.js** - Axios interceptor with automatic token refresh
4. **frontend/src/components/KeycloakProvider.jsx** - React context provider for Keycloak
5. **frontend/public/silent-check-sso.html** - Silent check SSO HTML file

## Architecture

### Keycloak Configuration
- **Realm**: social-media
- **Clients**:
  - social-media-web (Frontend SPA)
  - social-media-mobile (Future mobile app)
  - user-service (Backend service account)
  - post-service (Backend service account)
  - group-service (Backend service account)
  - interaction-service (Backend service account)
  - notification-service (Backend service account)

### Roles
- **Realm Roles**: USER, ADMIN, MODERATOR
- **Client Roles** (per service):
  - user-service: user:read, user:write, user:admin
  - post-service: post:read, post:create, post:update, post:delete, post:moderate
  - group-service: group:read, group:create, group:join, group:admin, group:member:manage
  - interaction-service: comment:create, comment:delete, reaction:create
  - notification-service: notification:read, notification:send

### Token Strategy
- **Access Token**: RS256, 5 minutes expiration
- **Refresh Token**: 7 days expiration with rotation
- **Service Account Token**: Client credentials flow, 5 minutes expiration

## Next Steps

### 1. Deploy Keycloak
```bash
docker-compose -f docker-compose.keycloak.yml up -d
```

### 2. Import Realm Configuration
1. Access Keycloak Admin Console: http://localhost:8080/admin
2. Login with admin/admin123
3. Import realm configuration from keycloak-realm-config.json

### 3. Enable Keycloak Profile
For each service, add the following to application.properties:
```
spring.profiles.active=keycloak
```

Or set environment variable:
```
SPRING_PROFILES_ACTIVE=keycloak
```

### 4. Update Frontend
1. Install keycloak-js:
```bash
npm install keycloak-js
```

2. Wrap App with KeycloakProvider:
```jsx
import { KeycloakProvider } from './components/KeycloakProvider';

function App() {
  return (
    <KeycloakProvider>
      <YourApp />
    </KeycloakProvider>
  );
}
```

3. Replace existing API calls with axiosWithKeycloak:
```javascript
import axiosWithKeycloak from './utils/axiosWithKeycloak';

// Instead of axios.get(...)
axiosWithKeycloak.get('/api/v1/users/profile')
```

### 5. Remove Old Authentication Code
- Remove custom JWT encoder/decoder
- Remove InternalAuthFilter
- Remove InvalidToken table
- Remove custom auth endpoints (login, register, refresh, logout)

### 6. Data Migration
Migrate existing users to Keycloak:
1. Export users from database
2. Import to Keycloak using Admin API or CSV import
3. Map existing roles to Keycloak roles

### 7. Testing
- Test authentication flow
- Test authorization with roles
- Test token refresh
- Test service-to-service communication
- Test logout

## Important Notes

### Security
- Change default passwords in production
- Use HTTPS for Keycloak in production
- Enable SSL required in Keycloak realm
- Rotate client secrets regularly
- Use environment variables for secrets

### Performance
- JWKS endpoint is cached by Spring Security
- Gateway validates first, services validate independently
- Token refresh happens automatically in frontend
- Service-to-service uses short-lived tokens

### Monitoring
- Monitor Keycloak health endpoint
- Log authentication events
- Monitor token usage
- Set up alerts for suspicious activity

## Rollback Plan
If migration fails, rollback by:
1. Disable keycloak profile
2. Enable original security configuration
3. Restore old authentication endpoints
4. Update frontend to use old API

## Support
For issues, check:
- Keycloak logs: `docker logs keycloak`
- Service logs for JWT validation errors
- Browser console for frontend authentication errors
- Keycloak Admin Console for user/role issues
