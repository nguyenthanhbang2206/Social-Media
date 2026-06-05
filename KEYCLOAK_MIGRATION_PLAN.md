# Keycloak Migration Plan for Social Media Platform

## Overview
Migrate from custom JWT authentication to Keycloak for enhanced security, scalability, and user management.

## Current Architecture
- Custom JWT with symmetric key (HS512)
- Refresh token in httpOnly cookie
- Internal service communication with X-Internal-Secret header
- Role-based authorization (ROLE_ADMIN only)
- Invalid token blacklist in database

## Target Architecture
- Keycloak as identity provider
- OAuth2/OIDC with RS256 (asymmetric keys)
- JWKS for token validation
- Resource-based permissions
- OAuth2 client credentials for service-to-service
- Short-lived access tokens (5 minutes)

## Migration Roadmap

### Phase 1: Setup & Planning (2 weeks)
- [ ] Infrastructure Setup
- [ ] Architecture Review
- [ ] Team Training

### Phase 2: Backend Migration (4 weeks)
- [ ] Keycloak Configuration
- [ ] API Gateway Integration
- [ ] User Service Integration
- [ ] Other Services Integration

### Phase 3: Frontend Migration (2 weeks)
- [ ] Keycloak JS Adapter
- [ ] API Integration

### Phase 4: Testing & Optimization (2 weeks)
- [ ] Security Testing
- [ ] Performance Testing
- [ ] Monitoring & Logging

### Phase 5: Migration & Cutover (1 week)
- [ ] Data Migration
- [ ] Cutover Strategy
- [ ] Post-Migration

### Phase 6: Post-Migration (Ongoing)
- [ ] Maintenance
- [ ] Enhancements

## Keycloak Configuration

### Realm Design
- **Realm Name**: social-media
- **SSL Required**: external requests
- **Registration**: allow (with email verification)
- **Forgot Password**: allow
- **Verify Email**: required
- **Login with Email**: allow
- **Internationalization**: Vietnamese, English

### Client Design

#### Client 1: web-app (Frontend SPA)
- **Client ID**: social-media-web
- **Protocol**: openid-connect
- **Access Type**: public (SPA)
- **Standard Flow Enabled**: true
- **Implicit Flow Enabled**: false
- **Direct Access Grants Enabled**: false
- **Valid Redirect URIs**: http://localhost:3000/*
- **Web Origins**: http://localhost:3000

#### Client 2: mobile-app (Future)
- **Client ID**: social-media-mobile
- **Protocol**: openid-connect
- **Access Type**: public
- **Standard Flow Enabled**: true
- **Direct Access Grants Enabled**: true

#### Client 3: service-account (Backend Services)
- **Client ID**: user-service
- **Protocol**: openid-connect
- **Access Type**: confidential
- **Service Accounts Enabled**: true
- **Client Authenticator**: client secret + JWT
- **Standard Flow Enabled**: false
- **Direct Access Grants Enabled**: false
- **Authorization Enabled**: false

Similar for: post-service, group-service, interaction-service, notification-service

### Role & Permission Design

#### High-level Roles
- `USER` - Regular user
- `ADMIN` - System administrator
- `MODERATOR` - Content moderator

#### Client Roles (per service)

**user-service:**
- `user:read` - Read user profile
- `user:write` - Update own profile
- `user:admin` - Manage any user (admin only)

**post-service:**
- `post:read` - Read posts
- `post:create` - Create posts
- `post:update` - Update own posts
- `post:delete` - Delete own posts
- `post:moderate` - Moderate any post (moderator/admin)

**group-service:**
- `group:read` - Read groups
- `group:create` - Create groups
- `group:join` - Join groups
- `group:admin` - Admin any group (admin only)
- `group:member:manage` - Manage group members (group admin)

**interaction-service:**
- `comment:create` - Create comments
- `comment:delete` - Delete own comments
- `reaction:create` - React to posts/comments

### Token Strategy

#### Access Token
- **Algorithm**: RS256 (asymmetric)
- **Issuer**: Keycloak
- **Audience**: Each service (audience claim)
- **Expiration**: 5 minutes

#### Refresh Token
- **Expiration**: 7 days
- **Rotation**: Enable
- **Reuse detection**: Enable
- **Storage**: httpOnly, secure, same-site cookie

#### Service Account Token
- **Flow**: Client credentials
- **Expiration**: 5 minutes
- **Use**: Service-to-service communication

## Progress Tracking

### Completed
- [x] Phase 1: Setup & Planning
  - [x] Infrastructure Setup (docker-compose.keycloak.yml)
  - [x] Keycloak realm configuration (keycloak-realm-config.json)
  - [x] Migration plan documentation

- [x] Phase 2: Backend Migration
  - [x] API Gateway Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java
    - [x] UserContextFilter.java
    - [x] gateway-config-keycloak.yml
    - [x] Updated pom.xml with OAuth2 dependencies
  - [x] User Service Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java
    - [x] Updated pom.xml with OAuth2 dependencies
  - [x] Post Service Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java
  - [x] Group Service Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java
  - [x] Interaction Service Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java
  - [x] Notification Service Integration
    - [x] application-keycloak.yml configuration
    - [x] SecurityConfigurationKeycloak.java

- [x] Phase 3: Frontend Migration
  - [x] Keycloak configuration (keycloak.js)
  - [x] Keycloak utility functions (keycloak.js)
  - [x] Axios interceptor with Keycloak (axiosWithKeycloak.js)
  - [x] KeycloakProvider component (KeycloakProvider.jsx)
  - [x] Silent check SSO HTML file (silent-check-sso.html)

### In Progress
- [ ] Phase 4: Testing & Optimization
  - [ ] Security Testing
  - [ ] Performance Testing
  - [ ] Monitoring & Logging

### Pending
- [ ] Phase 5: Migration & Cutover
  - [ ] Data Migration
  - [ ] Cutover Strategy
  - [ ] Post-Migration
- [ ] Phase 6: Post-Migration
  - [ ] Maintenance
  - [ ] Enhancements
