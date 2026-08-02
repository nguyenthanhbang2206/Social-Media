# Social Media Application - Full Source Code Documentation

## Overview
This is a full-stack social media application built with microservices architecture using Spring Boot (backend) and React (frontend). The application implements features similar to Facebook including user authentication, posts, comments, reactions, groups, friendships, notifications, and more.

---

## Backend Architecture

### Technology Stack
- **Java Version**: 17
- **Spring Boot**: 3.3.13
- **Spring Cloud**: 2023.0.5
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **Message Queue**: RabbitMQ
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Observability**: Prometheus, Grafana, Tempo, Loki, OpenTelemetry
- **Security**: JWT (HS512), Spring Security
- **ORM**: Spring Data JPA with Hibernate
- **Mapping**: MapStruct 1.5.5.Final
- **Utility**: Lombok 1.18.30

### Backend Project Structure

```
backend/
├── pom.xml                          # Parent POM with dependency management
├── docker-compose.yml               # Observability stack (Prometheus, Grafana, Tempo, Loki)
├── .mvn/                            # Maven wrapper
├── mvnw, mvnw.cmd                   # Maven wrapper scripts
├── api-gateway/                     # API Gateway Service (Port 8080)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       └── gateway/
│           ├── config/
│           │   ├── SecurityConfiguration.java
│           │   └── JwtHeaderFilter.java
│           └── filter/
│               └── UserContextFilter.java
├── discovery-service/               # Eureka Server (Port 8761)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       └── DiscoveryServiceApplication.java
├── common-library/                  # Shared Library
│   ├── pom.xml
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       └── common/
│           ├── client/
│           │   └── NotificationClient.java
│           ├── config/
│           │   ├── CustomAccessDeniedHandler.java
│           │   ├── CustomAuthenticationEntryPoint.java
│           │   └── RabbitMQConfig.java
│           ├── dto/
│           │   ├── ApiResponse.java
│           │   ├── GroupSummaryResponse.java
│           │   ├── PostInteractionCountResponse.java
│           │   ├── UserSummaryResponse.java
│           │   └── ...
│           ├── enumeration/
│           │   ├── Gender.java
│           │   ├── GroupMembershipStatus.java
│           │   ├── GroupPrivacy.java
│           │   ├── GroupRole.java
│           │   ├── NotificationType.java
│           │   ├── PostType.java
│           │   ├── PrivacyLevel.java
│           │   └── Role.java
│           ├── event/
│           │   ├── CommentEvent.java
│           │   ├── FriendRequestEvent.java
│           │   ├── GroupEvent.java
│           │   └── PostReactedEvent.java
│           ├── model/
│           │   └── BaseEntity.java
│           └── util/
│               └── RequestHeaderUtil.java
├── user-service/                    # User Service (Port 8081)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       ├── UserServiceApplication.java
│       ├── config/
│       │   ├── SecurityConfiguration.java
│       │   ├── InternalAuthFilter.java
│       │   └── CustomUserDetailsService.java
│       ├── controller/
│       │   ├── auth/
│       │   │   └── AuthController.java
│       │   ├── UserController.java
│       │   ├── FriendShipController.java
│       │   ├── BlockController.java
│       │   ├── AdminUserController.java
│       │   └── InternalUserController.java
│       ├── dto/
│       │   ├── request/
│       │   │   ├── CreateUserRequest.java
│       │   │   ├── LoginRequest.java
│       │   │   └── UpdateUserRequest.java
│       │   └── response/
│       │       └── AuthenticationResponse.java
│       ├── event/
│       │   ├── FriendRequestPublisher.java
│       │   └── FriendRequestConsumer.java
│       ├── mapper/
│       │   ├── UserMapper.java
│       │   ├── FriendShipMapper.java
│       │   └── BlockMapper.java
│       ├── model/
│       │   ├── User.java
│       │   ├── FriendShip.java
│       │   ├── Block.java
│       │   └── InvalidToken.java
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── FriendShipRepository.java
│       │   ├── BlockRepository.java
│       │   └── InvalidTokenRepository.java
│       ├── service/
│       │   ├── UserService.java
│       │   ├── impl/
│       │   │   └── UserServiceImpl.java
│       │   ├── FriendShipService.java
│       │   ├── BlockService.java
│       │   └── InternalUserService.java
│       └── util/
│           └── SecurityUtil.java
├── post-service/                    # Post Service (Port 8082)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       ├── PostServiceApplication.java
│       ├── client/
│       │   ├── UserServiceClient.java
│       │   ├── InteractionClient.java
│       │   └── GroupClient.java
│       ├── config/
│       │   ├── SecurityConfiguration.java
│       │   └── FeignHeaderConfig.java
│       ├── controller/
│       │   ├── PostController.java
│       │   ├── FileController.java
│       │   └── PostShareController.java
│       ├── dto/
│       │   ├── request/
│       │   │   ├── CreatePostRequest.java
│       │   │   ├── UpdatePostRequest.java
│       │   │   └── PostMediaRequest.java
│       │   └── response/
│       │       └── PostResponse.java
│       ├── mapper/
│       │   ├── PostMapper.java
│       │   ├── PostMediaMapper.java
│       │   └── PostShareMapper.java
│       ├── model/
│       │   ├── Post.java
│       │   ├── PostMedia.java
│       │   └── PostShare.java
│       ├── repository/
│       │   ├── PostRepository.java
│       │   ├── PostMediaRepository.java
│       │   └── PostShareRepository.java
│       └── service/
│           ├── PostService.java
│           ├── FileService.java
│           ├── PostShareService.java
│           └── impl/
│               ├── PostServiceImpl.java
│               ├── FileServiceImpl.java
│               └── PostShareServiceImpl.java
├── group-service/                   # Group Service (Port 8085)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       ├── GroupServiceApplication.java
│       ├── client/
│       │   └── UserClient.java
│       ├── config/
│       │   ├── SecurityConfiguration.java
│       │   └── FeignHeaderConfig.java
│       ├── controller/
│       │   ├── GroupController.java
│       │   └── GroupMemberController.java
│       ├── dto/
│       │   ├── request/
│       │   │   └── GroupRequest.java
│       │   └── response/
│       │       └── GroupResponse.java
│       ├── event/
│       │   ├── GroupPublisher.java
│       │   └── GroupConsumer.java
│       ├── mapper/
│       │   ├── GroupMapper.java
│       │   └── GroupMemberMapper.java
│       ├── model/
│       │   ├── Group.java
│       │   └── GroupMember.java
│       ├── repository/
│       │   ├── GroupRepository.java
│       │   └── GroupMemberRepository.java
│       └── service/
│           ├── GroupService.java
│           ├── GroupMemberService.java
│           └── impl/
│               ├── GroupServiceImpl.java
│               └── GroupMemberServiceImpl.java
├── interaction-service/             # Interaction Service (Port 8084)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/nguyenthanhbang/Social_media/
│       ├── InteractionServiceApplication.java
│       ├── client/
│       │   ├── PostClient.java
│       │   └── UserClient.java
│       ├── config/
│       │   ├── SecurityConfiguration.java
│       │   └── FeignHeaderConfig.java
│       ├── controller/
│       │   ├── CommentController.java
│       │   ├── PostLikeController.java
│       │   └── CommentLikeController.java
│       ├── dto/
│       │   ├── request/
│       │   │   └── CommentRequest.java
│       │   └── response/
│       │       └── CommentResponse.java
│       ├── event/
│       │   ├── CommentPublisher.java
│       │   └── PostReactedPublisher.java
│       ├── mapper/
│       │   ├── CommentMapper.java
│       │   ├── CommentLikeMapper.java
│       │   └── PostLikeMapper.java
│       ├── model/
│       │   ├── Comment.java
│       │   ├── CommentLike.java
│       │   └── PostLike.java
│       ├── repository/
│       │   ├── CommentRepository.java
│       │   ├── CommentLikeRepository.java
│       │   └── PostLikeRepository.java
│       └── service/
│           ├── CommentService.java
│           ├── CommentLikeService.java
│           ├── PostLikeService.java
│           └── impl/
│               ├── CommentServiceImpl.java
│               ├── CommentLikeServiceImpl.java
│               └── PostLikeServiceImpl.java
└── notification-service/            # Notification Service (Port 8083)
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/com/nguyenthanhbang/Social_media/
        ├── NotificationServiceApplication.java
        ├── client/
        │   └── UserClient.java
        ├── config/
        │   ├── SecurityConfiguration.java
        │   ├── FeignHeaderConfig.java
        │   ├── WebSocketConfig.java
        │   └── RabbitMQConfig.java
        ├── controller/
        │   └── NotificationController.java
        ├── dto/
        │   ├── request/
        │   │   └── NotificationRequest.java
        │   └── response/
        │       └── NotificationResponse.java
        ├── event/
        │   └── NotificationConsumer.java
        ├── mapper/
        │   └── NotificationMapper.java
        ├── model/
        │   └── Notification.java
        ├── repository/
        │   └── NotificationRepository.java
        └── service/
            ├── NotificationService.java
            └── impl/
                └── NotificationServiceImpl.java
```

### Microservices Structure

#### 1. API Gateway (Port 8080)
**Purpose**: Single entry point for all client requests, routing, and authentication

**Key Components**:
- `SecurityConfiguration.java` - JWT validation for gateway
- `JwtHeaderFilter.java` - Extracts JWT from Authorization header
- `UserContextFilter.java` - Extracts user info from JWT and forwards to downstream services via headers (X-User-Id, X-User-Email, X-User-Name, X-User-Roles)

**Routes**:
- `/api/v1/auth/**` → user-service
- `/api/v1/users/**` → user-service
- `/api/v1/friends/**` → user-service
- `/api/v1/friend-requests/**` → user-service
- `/api/v1/blocks/**` → user-service
- `/api/v1/admin/**` → user-service
- `/api/v1/posts/**` → post-service
- `/api/v1/files/**` → post-service
- `/api/v1/groups/**` → group-service
- `/api/v1/notifications/**` → notification-service
- `/ws/notifications/**` → notification-service (WebSocket)
- `/api/v1/posts/*/comments/**` → interaction-service
- `/api/v1/posts/*/react` → interaction-service
- `/api/v1/posts/*/reactions` → interaction-service
- `/api/v1/posts/*/me` → interaction-service
- `/api/v1/posts/*/un-react` → interaction-service
- `/api/v1/comments/**` → interaction-service

**Security**:
- JWT validation using HS512 algorithm
- CORS configuration for http://localhost:3000
- Stateful session management disabled

#### 2. Discovery Service (Port 8761)
**Purpose**: Service registry for microservices

**Technology**: Netflix Eureka Server

#### 3. User Service (Port 8081)
**Purpose**: User management, authentication, friendships, blocking

**Key Components**:
- `AuthController.java` - Login, register, refresh token, logout
- `UserController.java` - User CRUD operations
- `FriendShipController.java` - Friend request management
- `BlockController.java` - User blocking functionality
- `AdminUserController.java` - Admin user management
- `InternalUserController.java` - Internal API for other services
- `SecurityConfiguration.java` - JWT authentication with custom filter
- `InternalAuthFilter.java` - Validates X-Internal-Secret header for internal API calls
- `CustomUserDetailsService.java` - User details for Spring Security
- `UserService.java` - Business logic for user operations
- `UserRepository.java` - Database operations
- `FriendShipRepository.java` - Friendship operations
- `BlockRepository.java` - Blocking operations
- `InvalidTokenRepository.java` - Stores invalidated tokens for logout

**Models**:
- `User.java` - User entity with email, password, fullName, avatar, refreshToken, gender, role, dateOfBirth, coverPhoto
- `FriendShip.java` - Friendship relationships
- `Block.java` - Blocked user relationships
- `InvalidToken.java` - Blacklisted tokens

**Authentication Flow**:
1. Login: Returns JWT access token + sets httpOnly refresh token cookie
2. Refresh: Validates refresh token cookie, returns new access token
3. Logout: Invalidates access token in database, clears refresh token cookie

**Internal Communication**:
- Other services call `/api/v1/internal/users/{id}` with X-Internal-Secret header to get user info

#### 4. Post Service (Port 8082)
**Purpose**: Post management, file uploads, post reactions, post sharing

**Key Components**:
- `PostController.java` - Post CRUD operations
- `FileController.java` - File upload/download
- `PostShareController.java` - Post sharing functionality
- `PostService.java` - Business logic
- `FileService.java` - File handling
- `PostShareService.java` - Sharing logic
- `FeignHeaderConfig.java` - Adds X-User-Id, X-User-Email headers to Feign requests
- `UserClient.java` - Feign client to user-service
- `InteractionClient.java` - Feign client to interaction-service
- `GroupClient.java` - Feign client to group-service

**Resilience Patterns**:
- Circuit Breaker (Resilience4j) for user-service calls
- Retry mechanism for failed calls
- TimeLimiter for timeout handling

**Models**:
- `Post.java` - Post entity with content, privacy, totalReactions, totalComments, totalShares, userId, ownerName, groupId, postType, isApproved, isPinned
- `PostMedia.java` - Media attachments for posts
- `PostShare.java` - Post shares

**Features**:
- Create, update, delete posts
- Upload files (images/videos)
- React/unreact to posts
- Get post reactions
- Group posts with approval workflow
- Pin/unpin group posts

#### 5. Group Service (Port 8085)
**Purpose**: Group management, group membership

**Key Components**:
- `GroupController.java` - Group CRUD operations
- `GroupMemberController.java` - Member management
- `GroupService.java` - Business logic
- `GroupMemberService.java` - Member operations
- `FeignHeaderConfig.java` - Headers for Feign requests
- `UserClient.java` - Feign client to user-service
- `RabbitMQConfig.java` - Event publishing
- `GroupPublisher.java` - Publishes group events

**Models**:
- `Group.java` - Group entity with name, description, groupImage, coverImage, privacy, creatorId
- `GroupMember.java` - Group membership with role (ADMIN, MODERATOR, MEMBER)

**Features**:
- Create, update, delete groups
- Public/private groups
- Join/leave groups
- Approve/reject membership requests
- Member role management
- Group posts integration

#### 6. Interaction Service (Port 8084)
**Purpose**: Comments, comment reactions, post reactions

**Key Components**:
- `CommentController.java` - Comment CRUD operations
- `CommentLikeController.java` - Comment reactions
- `PostLikeController.java` - Post reactions
- `CommentService.java` - Comment business logic
- `CommentLikeService.java` - Comment reaction logic
- `PostLikeService.java` - Post reaction logic
- `FeignHeaderConfig.java` - Headers for Feign requests
- `PostClient.java` - Feign client to post-service
- `UserClient.java` - Feign client to user-service
- `RabbitMQConfig.java` - Event publishing
- `CommentPublisher.java` - Publishes comment events
- `PostReactedPublisher.java` - Publishes reaction events

**Models**:
- `Comment.java` - Comment entity with content, userId, postId, parentCommentId (for replies)
- `CommentLike.java` - Comment reactions
- `PostLike.java` - Post reactions

**Features**:
- Create, update, delete comments
- Reply to comments (nested)
- React/unreact to comments
- React/unreact to posts
- Get reactions for posts/comments

#### 7. Notification Service (Port 8083)
**Purpose**: Real-time notifications via WebSocket

**Key Components**:
- `NotificationController.java` - Notification CRUD operations
- `NotificationService.java` - Business logic
- `WebSocketConfig.java` - WebSocket configuration
- `NotificationConsumer.java` - Consumes RabbitMQ events
- `FeignHeaderConfig.java` - Headers for Feign requests
- `UserClient.java` - Feign client to user-service
- `RabbitMQConfig.java` - Event consumption

**Models**:
- `Notification.java` - Notification entity with recipientId, actorId, type, referenceId, message, isRead

**WebSocket**:
- Endpoint: `/ws/notifications`
- Real-time push of new notifications to connected clients

**Notification Types**:
- FRIEND_REQUEST
- FRIEND_ACCEPTED
- POST_REACTED
- COMMENT_ON_POST
- COMMENT_REPLY
- GROUP_INVITATION
- GROUP_POST_APPROVED

#### 8. Common Library
**Purpose**: Shared code across all services

**Components**:
- `BaseEntity.java` - Base entity with id, createdAt, updatedAt, active
- `ApiResponse.java` - Standard API response wrapper
- `CustomAccessDeniedHandler.java` - Custom 403 handler
- `CustomAuthenticationEntryPoint.java` - Custom 401 handler
- `RequestHeaderUtil.java` - Utility to extract request headers
- `SecurityUtil.java` - JWT creation and validation utilities
- Enums: `Role`, `Gender`, `PostType`, `PrivacyLevel`, `GroupPrivacy`, `NotificationType`

### Database Schema

**User Service Database**:
- `users` - User accounts
- `friendships` - Friend relationships
- `blocks` - Blocked users
- `invalid_tokens` - Blacklisted tokens

**Post Service Database**:
- `posts` - Posts
- `post_media` - Post media attachments
- `post_shares` - Post shares

**Group Service Database**:
- `groups` - Groups
- `group_members` - Group memberships

**Interaction Service Database**:
- `comments` - Comments
- `post_likes` - Post reactions
- `comment_likes` - Comment reactions

**Notification Service Database**:
- `notifications` - Notifications

### Security Architecture

**JWT Authentication**:
- Algorithm: HS512
- Secret: Base64 encoded (configured in application.properties)
- Access token validity: 86400 seconds (24 hours)
- Refresh token validity: 86400 seconds (24 hours)
- Token stored in httpOnly cookie for refresh
- Access token sent in Authorization header

**Internal Service Communication**:
- Services communicate via Feign clients
- Headers forwarded: X-User-Id, X-User-Email, X-User-Name, X-User-Roles
- Internal API protected by X-Internal-Secret header
- Internal endpoints: `/api/v1/internal/**`

**CORS Configuration**:
- Allowed origin: http://localhost:3000
- Allowed methods: All
- Allowed headers: All
- Credentials: enabled

### Event-Driven Architecture

**RabbitMQ Events**:
- Friend request events (user-service → notification-service)
- Comment events (interaction-service → notification-service)
- Post reaction events (interaction-service → notification-service)
- Group events (group-service → notification-service)

### Observability Stack

**Docker Compose Services**:
- Prometheus (port 9090) - Metrics collection
- Grafana (port 3001) - Metrics visualization
- Tempo (port 3200) - Distributed tracing
- Loki (port 3100) - Log aggregation
- Promtail - Log shipping
- OTEL Collector (port 4319) - OpenTelemetry tracing

**Metrics**:
- Spring Boot Actuator endpoints
- Prometheus metrics registry
- Custom business metrics

**Tracing**:
- OpenTelemetry bridge
- OTLP exporter
- Distributed tracing across microservices

---

## Frontend Architecture

### Technology Stack
- **Framework**: React 19.1.0
- **State Management**: Redux 5.0.1 + Redux Thunk 3.1.0
- **Routing**: React Router DOM 7.6.3
- **HTTP Client**: Axios 1.10.0
- **UI Library**: Material UI (MUI) 7.2.0
- **Styling**: Tailwind CSS 3.4.17
- **WebSocket**: @stomp/stompjs 7.3.0, sockjs-client 1.6.1
- **Build Tool**: Create React App

### Project Structure

```
src/
├── api/                    # Redux modules for each API
│   ├── Auth/              # Authentication
│   ├── Post/              # Posts
│   ├── User/              # User operations
│   ├── FriendShip/        # Friend relationships
│   ├── Block/             # Blocking
│   ├── Comment/           # Comments
│   ├── CommentLike/       # Comment reactions
│   ├── PostShare/         # Post sharing
│   ├── Group/             # Groups
│   ├── GroupMember/       # Group membership
│   ├── Notification/      # Notifications
│   ├── AdminUser/         # Admin user management
│   └── store.js           # Redux store configuration
├── components/
│   ├── layout/            # Layout components
│   │   ├── AppLayout.jsx
│   │   ├── TopNavigation.jsx
│   │   ├── LeftSidebar.jsx
│   │   └── RightSidebar.jsx
│   ├── CommentItem.jsx
│   ├── CommentList.jsx
│   ├── Header.jsx
│   ├── NotificationBell.jsx
│   └── PostModal.jsx
├── config/
│   ├── api.js             # Axios configuration
│   └── useNotificationWebSocket.js  # WebSocket hook
├── layout/
│   ├── AdminLayout.jsx
│   └── UserLayout.jsx
├── pages/
│   ├── Home.jsx           # News feed
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── UserProfile.jsx
│   ├── UserSearch.jsx
│   ├── FriendList.jsx
│   ├── FriendRequest.jsx
│   ├── SuggestionFriends.jsx
│   ├── BlockedUsers.jsx
│   ├── Notifications.jsx
│   ├── GroupList.jsx
│   ├── GroupCreate.jsx
│   ├── GroupDetail.jsx
│   ├── GroupEdit.jsx
│   └── AdminDashBoard.jsx
├── router/
│   └── router.js          # Route configuration
├── utils/
│   └── profileNavigation.js
├── App.js                 # Main app component
├── index.js               # Entry point
└── index.css
```

### Redux Architecture

**State Management Pattern**:
- Each API module has: ActionType.js, Action.js, Reducer.js
- Actions are async thunks using Redux Thunk
- Reducers handle state updates
- Store combines all reducers

**Redux Modules**:
1. **Auth** - User authentication state
2. **Post** - Posts state
3. **User** - User data state
4. **FriendShip** - Friend relationships state
5. **Block** - Blocked users state
6. **Comment** - Comments state
7. **CommentLike** - Comment reactions state
8. **PostShare** - Post shares state
9. **Group** - Groups state
10. **GroupMember** - Group membership state
11. **Notification** - Notifications state
12. **AdminUser** - Admin user management state

**API Caching**:
- Friendship API calls cached with 60s TTL
- Block list API calls cached with 60s TTL
- Reducers store fetch timestamps for cache logic

### Key Components

**AppLayout Component**:
- Wraps authenticated pages
- Includes TopNavigation, LeftSidebar, RightSidebar
- Props: children, showRightSidebar (default: true)

**TopNavigation Component**:
- Logo, search bar, navigation icons
- Notification bell with unread count
- User profile avatar with dropdown
- Direct logout button

**LeftSidebar Component**:
- User profile summary
- Navigation menu (News Feed, My Profile, Friends, Groups)

**RightSidebar Component**:
- Sponsored content section
- Friend requests section (using Redux data)
- Contacts/Online friends section

### Authentication Flow

**Login Flow**:
1. User enters credentials
2. POST `/api/v1/auth/login`
3. Store access token in localStorage
4. Store user info in localStorage
5. Redirect based on role (ADMIN → /admin, USER → /)
6. Set up auto-logout on token expiration

**Token Management**:
- Access token stored in localStorage
- Refresh token stored in httpOnly cookie (backend)
- Auto-refresh on 401 errors
- Auto-logout when token expires
- Token expiration checked from JWT payload

**Axios Interceptors**:
- Request interceptor: Adds Authorization header
- Response interceptor: Handles 401 errors, clears token, triggers logout

### WebSocket Integration

**Notification WebSocket**:
- STOMP over SockJS
- Endpoint: `/ws/notifications`
- Subscribes to user-specific notifications
- Real-time notification updates
- Auto-reconnection on disconnect

### API Integration Pattern

**Each API Module Structure**:
```
api/[ModuleName]/
├── ActionType.js    # Action type constants
├── Action.js        # Async action creators (thunks)
└── Reducer.js       # Redux reducer
```

**Example: Post/Action.js**:
- `getPosts()` - Fetch all posts
- `getPostsByUser(userId)` - Fetch user posts with fallback
- `createPost(postData)` - Create post
- `updatePost(id, postData)` - Update post
- `deletePost(id)` - Delete post
- `uploadFiles(files, folder)` - Upload files
- `reactPost(postId, reactionType)` - React to post
- `unreactPost(postId)` - Unreact post
- `getReactionsOfPost(postId)` - Get post reactions
- `getReactPostByMeAndPostId(postId)` - Get my reaction
- Group post operations

**Error Handling**:
- Try-catch in all async actions
- Dispatch failure actions
- User-friendly error messages
- Fallback mechanisms (e.g., user posts fallback to feed filtering)

### Routing Structure

**Public Routes**:
- `/login` - Login page
- `/register` - Registration page

**Protected Routes**:
- `/` - Home (news feed)
- `/search` - User search
- `/profile` - Redirects to `/users/{currentUserId}`
- `/users/:userId` - User profile
- `/friend-list` - Friend list
- `/friend-requests` - Friend requests
- `/suggestion-friends` - Friend suggestions
- `/blocked-users` - Blocked users
- `/notifications` - Notifications
- `/groups` - Group list
- `/groups/create` - Create group
- `/groups/:id` - Group detail
- `/groups/:id/edit` - Edit group

**Admin Routes**:
- `/admin` - Admin dashboard

### Key Pages

**Home.jsx**:
- News feed with posts
- Create post with media upload
- React to posts with emoji picker
- View post reactions
- Comment on posts
- Share posts

**UserProfile.jsx**:
- User profile view
- Edit own profile
- View user posts
- Friend request buttons (Send, Accept, Refuse, Unfriend)
- Block/unblock user
- Optimized to avoid unnecessary API calls when viewing own profile

**GroupDetail.jsx**:
- Group information
- Group posts feed
- Member management (for admins)
- Post approval workflow (for private groups)
- Pin/unpin posts

**Notifications.jsx**:
- Notification list
- Mark as read
- Mark all as read
- Real-time updates via WebSocket

### UI Patterns

**Reaction System**:
- 6 reaction types: LIKE, LOVE, HAHA, WOW, SAD, ANGRY
- Emoji mapping for display
- Reaction count display
- Modal to view all reactions

**Post Rendering**:
- Owner name from PostResponse (preferred)
- Fallback to createdBy/createdByName
- Media display with full URL construction
- Privacy level indicators

**Error Handling**:
- Global error handling in axios interceptor
- Component-level try-catch
- User-friendly error messages
- Loading states for async operations

### Performance Optimizations

**API Call Reduction**:
- Cache friendship data (60s TTL)
- Cache block list (60s TTL)
- Avoid redundant profile fetches
- Home.jsx doesn't call getFriends
- UserProfile.jsx skips friend/block APIs for own profile

**Token Management**:
- Auth reducer hydrates from localStorage on startup
- Reduces unnecessary `/users/profile` calls
- Auto-logout on token expiration

**Post Fetching**:
- Fallback mechanism for user posts (filter from feed)
- Cooldown after failures to prevent repeated 500 calls
- Client-side filtering when endpoint fails

---

## Key Learnings and Patterns

### Backend Patterns

1. **Microservices Architecture**:
   - Clear separation of concerns
   - Each service owns its database
   - Communication via Feign clients and RabbitMQ
   - API Gateway as single entry point

2. **Security Pattern**:
   - JWT for authentication
   - Internal secret for service-to-service communication
   - User context propagation via headers
   - Token invalidation on logout

3. **Resilience Patterns**:
   - Circuit breaker for external service calls
   - Retry mechanism with cooldown
   - Fallback strategies
   - Timeouts

4. **Event-Driven Architecture**:
   - RabbitMQ for async events
   - Decoupled services
   - Event publishers and consumers
   - Notification system driven by events

5. **Observability**:
   - Comprehensive monitoring stack
   - Distributed tracing
   - Centralized logging
   - Metrics collection

### Frontend Patterns

1. **Redux Pattern**:
   - Modular Redux structure
   - Async actions with thunks
   - Clear separation of concerns
   - Centralized state management

2. **API Integration**:
   - Consistent API module structure
   - Axios interceptors for auth
   - Error handling at multiple levels
   - Caching strategies

3. **Component Architecture**:
   - Layout components for consistency
   - Reusable UI components
   - Page components for routes
   - Props for configuration

4. **Performance**:
   - API call caching
   - Avoiding redundant calls
   - Fallback mechanisms
   - Optimized re-renders

5. **Real-time Updates**:
   - WebSocket integration
   - STOMP protocol
   - Auto-reconnection
   - User-specific subscriptions

### Best Practices Observed

1. **Code Organization**:
   - Clear package structure
   - Separation of concerns
   - Consistent naming conventions
   - Modular architecture

2. **Error Handling**:
   - Global exception handlers
   - Custom error responses
   - User-friendly messages
   - Graceful degradation

3. **Security**:
   - JWT with strong algorithm
   - HttpOnly cookies for refresh tokens
   - Internal API protection
   - CORS configuration

4. **Database Design**:
   - Soft delete with active flag
   - Indexed columns for performance
   - Proper relationships
   - Audit fields (createdAt, updatedAt)

5. **API Design**:
   - RESTful endpoints
   - Consistent response format
   - Proper HTTP methods
   - Versioned API (/api/v1)

---

## Configuration Files

### Backend Configuration

**application.properties** (common across services):
- Database connection (PostgreSQL)
- JWT secret and validity
- Eureka service URL
- RabbitMQ configuration
- Internal auth secret
- Observability endpoints

**docker-compose.yml**:
- Observability stack (Prometheus, Grafana, Tempo, Loki)
- Network configuration
- Volume management

### Frontend Configuration

**package.json**:
- React 19.1.0
- Redux ecosystem
- Material UI
- Tailwind CSS
- WebSocket libraries

**api.js**:
- Axios instance with base URL
- Request interceptor for auth
- Response interceptor for error handling
- Token expiration checking

---

## Development Workflow

### Backend Development

1. **Adding a new microservice**:
   - Create service module in backend/
   - Add to parent pom.xml modules
   - Configure application.properties
   - Implement security configuration
   - Add Feign clients for inter-service communication
   - Add to API Gateway routes

2. **Adding a new API endpoint**:
   - Create DTOs (Request/Response)
   - Create/Update entity
   - Create repository
   - Create service interface and implementation
   - Create controller
   - Add mapper (MapStruct)
   - Add to API Gateway routes

3. **Adding event publishing**:
   - Create RabbitMQ configuration
   - Create event publisher
   - Publish event in service
   - Create consumer in notification-service

### Frontend Development

1. **Adding a new API module**:
   - Create api/[ModuleName]/ directory
   - Create ActionType.js with constants
   - Create Action.js with async thunks
   - Create Reducer.js with state management
   - Add to store.js combineReducers

2. **Adding a new page**:
   - Create page component in pages/
   - Add route in router.js
   - Wrap in AppLayout if authenticated
   - Implement API calls using Redux actions
   - Add error handling and loading states

3. **Adding a new component**:
   - Create component in components/
   - Make it reusable with props
   - Add styling (Tailwind/MUI)
   - Integrate with Redux if needed

---

## Deployment Considerations

### Backend Deployment
- Each service runs in separate container
- PostgreSQL database per service
- RabbitMQ for messaging
- Eureka for service discovery
- API Gateway as entry point
- Observability stack for monitoring

### Frontend Deployment
- Static build from Create React App
- Serve via nginx or similar
- Configure API base URL for production
- WebSocket endpoint configuration
- Environment variables for configuration

---

## Known Issues and Limitations

1. **Post Service Resilience**:
   - User posts endpoint sometimes returns 500
   - Fallback mechanism filters from feed
   - Cooldown period to prevent repeated failures

2. **Frontend Optimizations**:
   - Some API calls could be further optimized
   - Pagination not implemented for large datasets
   - Image optimization could be improved

3. **Security**:
   - JWT secret should be in environment variables
   - Internal auth secret should be rotated
   - Rate limiting not implemented

---

## Future Improvements

1. **Backend**:
   - Implement rate limiting
   - Add API versioning strategy
   - Implement pagination for all endpoints
   - Add comprehensive logging
   - Implement caching with Redis
   - Add API documentation (Swagger/OpenAPI)

2. **Frontend**:
   - Implement lazy loading for components
   - Add comprehensive error boundaries
   - Implement optimistic updates
   - Add offline support
   - Improve mobile responsiveness
   - Add unit and integration tests

3. **Infrastructure**:
   - Add CI/CD pipeline
   - Implement blue-green deployment
   - Add load balancing
   - Implement database backups
   - Add monitoring alerts

---

## Detailed Service Implementation Examples

### Post Service Implementation Details

**PostServiceImpl.java** - Key Business Logic:

```java
// Post creation with group integration
public Post createPost(Long groupId, CreatePostRequest request) {
    Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserSummaryResponse userSummaryResponse = userServiceClient.getUserById(userId).join();
    
    Post post = new Post();
    post.setContent(request.getContent());
    post.setUserId(userId);
    post.setOwnerName(userSummaryResponse.getFullName());
    
    // Media handling
    List<PostMedia> postMediaList = new ArrayList<>();
    for(PostMediaRequest item : request.getMedia()) {
        PostMedia postMedia = PostMedia.builder()
                .mediaUrl(item.getMediaUrl())
                .mediaType(item.getMediaType())
                .uploadOrder(item.getUploadOrder())
                .post(post)
                .build();
        postMediaList.add(postMedia);
    }
    post.setMedia(postMediaList);
    
    // Group post logic
    if(groupId != null && request.getPostType() == PostType.GROUP_POST) {
        GroupSummaryResponse group = groupClient.getGroupById(groupId).getData();
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        
        if (status != GroupMembershipStatus.APPROVED) {
            throw new EntityNotFoundException("You have to join this group to create new post");
        }
        
        post.setGroupId(groupId);
        post.setPostType(PostType.GROUP_POST);
        
        // Private groups require admin approval
        if(group.getPrivacy() == GroupPrivacy.PRIVATE){
            boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
            post.setIsApproved(isAdmin);
        } else {
            post.setIsApproved(true);
        }
    }
    
    return postRepository.save(post);
}

// Populate post totals from interaction service
private void populatePostTotals(Post post) {
    PostInteractionCountResponse counts = interactionClient.getPostCounts(post.getId()).getData();
    long totalComments = counts == null || counts.getTotalComments() == null ? 0L : counts.getTotalComments();
    long totalReactions = counts == null || counts.getTotalReactions() == null ? 0L : counts.getTotalReactions();
    post.setTotalComments(totalComments);
    post.setTotalReactions(totalReactions);
    post.setTotalShares(postShareRepository.countByPostId(post.getId()));
}
```

**Key Patterns**:
- Feign client calls with `.join()` for synchronous blocking
- RequestHeaderUtil for extracting user context from headers
- Group membership validation before operations
- Cross-service data aggregation (interaction counts)
- Soft delete pattern (setActive(false))

### Group Service Implementation Details

**GroupServiceImpl.java** - Group Creation with Auto-Membership:

```java
@Override
public Group createGroup(GroupRequest request) {
    Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
    
    Group newGroup = new Group();
    newGroup.setName(request.getName());
    newGroup.setDescription(request.getDescription());
    newGroup.setGroupImage(request.getGroupImage());
    newGroup.setCoverImage(request.getCoverImage());
    newGroup.setPrivacy(request.getPrivacy());
    newGroup.setCreatorId(userId);
    
    // Auto-add creator as admin
    GroupMember groupMember = GroupMember.builder()
            .userId(userId)
            .groupId(null)  // Set after group save
            .role(GroupRole.ADMIN)
            .joinedAt(LocalDateTime.now())
            .status(GroupMembershipStatus.APPROVED)
            .isApproved(true)
            .build();
    
    Group savedGroup = groupRepository.save(newGroup);
    groupMember.setGroupId(savedGroup.getId());
    groupMemberRepository.save(groupMember);
    
    return savedGroup;
}
```

**Key Patterns**:
- Creator automatically becomes admin
- Two-step save (group first, then member with groupId)
- Transaction management with @Transactional
- Permission checks (creator-only updates)

### Comment Service Implementation Details

**CommentServiceImpl.java** - Comment with Event Publishing:

```java
@Override
public Comment comment(Long postId, CommentRequest request) {
    Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserSummaryResponse userSummaryResponse = userClient.getUserById(userId).getData();
    
    PostSummaryResponse post = postClient.getPostById(postId).getData();
    if (post == null) {
        throw new EntityNotFoundException("Post not found");
    }
    
    // Block check
    boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, post.getUserId()).getData());
    if (blocked) {
        throw new EntityNotFoundException("Blocked user");
    }
    
    Comment comment = new Comment();
    comment.setContent(request.getContent());
    comment.setUserId(userId);
    comment.setPostId(postId);
    comment = commentRepository.save(comment);
    
    // Event publishing for notification
    String commentPreview = comment.getContent().length() < 50 
            ? comment.getContent() 
            : comment.getContent().substring(0,50) + "...";
    
    CommentEvent event = CommentEvent.builder()
            .actorId(userId)
            .actorName(userSummaryResponse.getFullName())
            .commentPreview(commentPreview)
            .reply(false)
            .postId(postId)
            .action("CREATED")
            .recipientId(post.getUserId())
            .build();
    
    commentPublisher.publish(event);
    return comment;
}
```

**Key Patterns**:
- Block validation before allowing interaction
- Event-driven notification system
- Comment preview generation (50 chars)
- Separate reply method with parentCommentId

### Notification Service Implementation Details

**NotificationServiceImpl.java** - Real-time Notification:

```java
@Override
@Transactional
public NotificationResponse createNotification(NotificationRequest request) {
    // Validation
    if (request == null || request.getActorId() == null || request.getRecipientId() == null) {
        log.warn("Skipping notification: missing actorId/recipientId");
        return null;
    }
    
    // Don't notify yourself
    if (request.getActorId().equals(request.getRecipientId())) {
        log.debug("Skipping self-notification for userId={}", request.getActorId());
        return null;
    }
    
    Notification notification = notificationMapper.toNotification(request);
    notification = notificationRepository.save(notification);
    
    NotificationResponse response = notificationMapper.toNotificationResponse(notification);
    enrichWithActorInfo(response);
    
    // Real-time push via WebSocket
    String destination = "/topic/notifications/" + request.getRecipientId();
    messagingTemplate.convertAndSend(destination, response);
    log.info("Notification sent to userId={} type={}", request.getRecipientId(), request.getType());
    
    return response;
}

private void enrichWithActorInfo(NotificationResponse response) {
    try {
        UserSummaryResponse actor = userClient.getUserById(response.getActorId()).getData();
        if (actor != null) {
            response.setActorName(actor.getFullName());
            response.setActorAvatar(actor.getAvatar());
        }
    } catch (Exception e) {
        log.warn("Failed to fetch actor info for userId={}: {}", response.getActorId(), e.getMessage());
    }
}
```

**Key Patterns**:
- Self-notification prevention
- WebSocket real-time push to user-specific topic
- Actor info enrichment via Feign client
- Graceful error handling for enrichment failures
- Transaction management

### User Service Implementation Details

**AuthController.java** - JWT Token Management:

```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody LoginRequest request) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
    
    User user = userRepository.findByEmailAndActiveTrue(request.getEmail());
    AuthenticationResponse.UserLogin userLogin = new AuthenticationResponse.UserLogin();
    userLogin.setId(user.getId());
    userLogin.setFullName(user.getFullName());
    userLogin.setEmail(user.getEmail());
    userLogin.setRole(user.getRole());
    
    AuthenticationResponse response = new AuthenticationResponse();
    response.setUser(userLogin);
    
    // Create access token
    String accessToken = securityUtil.createAccessToken(request.getEmail(), response);
    response.setAccessToken(accessToken);
    
    // Create refresh token
    String refreshToken = securityUtil.createRefreshToken(request.getEmail(), response);
    userService.updateTokenOfUser(request.getEmail(), refreshToken);
    
    // Set httpOnly cookie
    ResponseCookie springCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtRefreshExpiration)
            .build();
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
    
    ApiResponse apiResponse = ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("Login Successful")
            .data(response)
            .build();
    
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(apiResponse);
}
```

**Key Patterns**:
- Spring Security authentication
- JWT access token + httpOnly refresh token cookie
- Refresh token stored in database for validation
- Security context management
- Standardized API response wrapper

---

## Common Library Components

### BaseEntity.java - Audit Fields

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
```

### ApiResponse.java - Standard Response Wrapper

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
}
```

### SecurityUtil.java - JWT Utilities

```java
public String createAccessToken(String email, AuthenticationResponse response) {
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(email)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(jwtExpiration))
            .claim("scope", response.getUser().getRole())
            .claim("fullName", response.getUser().getFullName())
            .claim("userId", response.getUser().getId())
            .build();
    
    JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
    return jwtEncoder.encode(parameters).getTokenValue();
}
```

### RequestHeaderUtil.java - Header Extraction

```java
public static Optional<String> getHeader(String headerName) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String headerValue = request.getHeader(headerName);
    return Optional.ofNullable(headerValue);
}

public static Optional<Long> getUserId() {
    String userIdHeader = getHeader("X-User-Id").orElse(null);
    if (userIdHeader != null) {
        try {
            return Optional.of(Long.parseLong(userIdHeader));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    return Optional.empty();
}
```

---

## Frontend Implementation Details

### Redux Action Pattern - Post/Action.js

```javascript
// Create post with media
export const createPost = (postData) => async (dispatch) => {
  dispatch({ type: CREATE_POST_REQUEST });
  try {
    const res = await api.post("/posts", postData);
    dispatch({
      type: CREATE_POST_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: CREATE_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Upload files
export const uploadFiles = (files, folder) => async (dispatch) => {
  dispatch({ type: UPLOAD_FILES_REQUEST });
  try {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }
    formData.append("folder", folder);

    const res = await api.post("/files", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    dispatch({
      type: UPLOAD_FILES_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: UPLOAD_FILES_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};
```

### React Component Pattern - Home.jsx

```javascript
export default function Home() {
  const dispatch = useDispatch();
  const { posts, loading, error, createSuccess, uploadedFiles } =
    useSelector((state) => state.post);
  const [content, setContent] = useState("");
  const [media, setMedia] = useState([]);

  useEffect(() => {
    dispatch(getPosts());
  }, [dispatch, createSuccess]);

  useEffect(() => {
    if (uploadedFiles && uploadedFiles.length > 0) {
      setMedia((prev) => [...prev, ...uploadedFiles]);
    }
  }, [uploadedFiles]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const postMedia = media.map((file) => ({
      mediaUrl: file.mediaUrl,
      mediaType: file.mediaType,
      uploadOrder: file.uploadOrder,
    }));

    dispatch(
      createPost({
        content,
        privacy: "PUBLIC",
        media: postMedia,
      }),
    );

    setContent("");
    setMedia([]);
  };

  return (
    <AppLayout>
      {/* Post creation form */}
      {/* Posts list */}
    </AppLayout>
  );
}
```

### WebSocket Hook - useNotificationWebSocket.js

```javascript
import { useEffect } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default function useNotificationWebSocket() {
  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws/notifications");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      const token = localStorage.getItem("token");
      stompClient.subscribe(
        "/topic/notifications/" + token,
        (message) => {
          const notification = JSON.parse(message.body);
          // Handle notification
          console.log("New notification:", notification);
        },
        { token: token }
      );
    });

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, []);
}
```

---

## Conclusion

This social media application demonstrates a well-architected microservices system with:
- Clear separation of concerns
- Robust security implementation
- Event-driven communication
- Comprehensive observability
- Modern React frontend with Redux
- Real-time features via WebSocket
- Resilience patterns for reliability

The codebase follows best practices for both backend and frontend development, with consistent patterns across services and a maintainable structure that allows for easy extension and modification.
