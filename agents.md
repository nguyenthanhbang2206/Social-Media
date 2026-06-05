# AI Agents Memory - Social Media Project

## Project Overview

- **Type**: Monorepo with Spring Boot microservices (backend) and React frontend
- **Backend Services**: api-gateway, discovery-service, user-service, post-service, group-service, interaction-service, notification-service, common-library
- **Frontend**: React with Redux for state management
- **API Base URL**: http://localhost:8080/api/v1
- **Authentication**: JWT access token + refresh token cookie

## Completed API Integrations

### Frontend API Structure (Redux)

All API integrations follow the pattern:

- `ActionType.js` - Action type constants
- `Action.js` - Async action creators using Redux Thunk
- `Reducer.js` - Redux reducer

#### Currently Integrated APIs:

1. **Auth** (`/api/auth`) - Login, Register, Logout, Get Profile
2. **Notification** (`/api/notifications`) - Get notifications, mark as read, unread count
3. **Post** (`/api/posts`) - CRUD posts, reactions, file upload
4. **User** (`/api/users`) - Get user by ID, search users, update profile
5. **FriendShip** (`/api/friend-requests`, `/api/friends`) - Send/accept/refuse requests, get friends, friend status
6. **Block** (`/api/blocks`) - Block/unblock users, get blocked list
7. **Comment** (`/api/posts/{id}/comments`) - Create, update, delete comments, replies
8. **CommentLike** (`/api/comments/{id}/react`) - React/unreact to comments
9. **PostShare** (`/api/posts/{id}/shares`) - Share posts, get shares
10. **Group** (`/api/groups`) - CRUD groups, search groups, get my groups
11. **GroupMember** (`/api/groups/{id}/members`) - Join/leave, approve/reject members, manage roles
12. **AdminUser** (`/api/admin/users`) - Get all users, update user (admin only)

## Frontend AdminUser Notes

- `frontend/src/api/AdminUser/Action.js` currently exports `getAdminUsers(active)` and `updateAdminUser(userId, userData)`.
- The admin dashboard page uses `getAdminUsers()` for initial load and `updateAdminUser(userId)` to toggle `active` through `PUT /api/v1/admin/users/{id}`.
- If `AdminDashBoard.jsx` is revisited, do not restore the legacy `getAllUsers` / `updateUserStatus` names.

## Frontend API Optimization Notes

- Friendship API calls are cached with a 60s TTL in `frontend/src/api/FriendShip/Action.js` (`getFriends`, `getFriendRequestsReceived`) to prevent duplicate calls from repeated layout mounts.
- Block list API calls are cached with a 60s TTL in `frontend/src/api/Block/Action.js` (`getBlockedUsers`).
- Reducers store fetch timestamps (`friendsLastFetchedAt`, `friendRequestsLastFetchedAt`, `blockedUsersLastFetchedAt`) so cache logic is state-driven.
- `Home.jsx` no longer calls `getFriends` because that data is not required to render home feed.
- `UserProfile.jsx` avoids calling `getFriendStatus`, `getBlockedUsers`, and `/users/{id}` when the current user is viewing their own profile.
- Auth reducer now hydrates `user` and `token` from localStorage on startup to reduce unnecessary `/users/profile` calls.
- Added shared utility `frontend/src/utils/profileNavigation.js` so all user-card clicks route to `/profile` for self and `/users/{id}` for others.
- `TopNavigation.jsx` now has a direct logout button that dispatches `logout()` and navigates to `/login`.
- `Post/Action.js#getPostsByUser` now uses a fallback (`/posts` + client filter by `userId`) and a short cooldown after failures to reduce repeated 500 calls.
- Post rendering should prefer `ownerName` from `PostResponse`; `createdBy` is legacy fallback data only.

## Backend Resilience Note

- Removed an unnecessary internal user-service call from `post-service` `getPostByUserId`; the method now directly queries posts by `userId` and computes totals.

## Frontend Layout Components

### New Layout System (AppLayout)

All authenticated pages now use a consistent layout system with the following components:

- **AppLayout.jsx** (`frontend/src/components/layout/AppLayout`) - Main layout wrapper
  - Includes TopNavigation, LeftSidebar, and optional RightSidebar
  - Props: `children`, `showRightSidebar` (default: true)

- **TopNavigation.jsx** (`frontend/src/components/layout/TopNavigation`) - Top navigation bar
  - Logo, search bar, navigation icons (Home, Friends, Groups)
  - Notification bell with unread count
  - User profile avatar with dropdown

- **LeftSidebar.jsx** (`frontend/src/components/layout/LeftSidebar`) - Left sidebar menu
  - User profile summary (avatar, name)
  - Navigation menu: News Feed, My Profile, Friends (List, Requests, Suggestions, Blocked Users), Groups (My Groups, All Groups, Create Group)

- **RightSidebar.jsx** (`frontend/src/components/layout/RightSidebar`) - Right sidebar
  - Sponsored content section
  - Friend requests section (using Redux data)
  - Contacts/Online friends section (using Redux data)

## Frontend Pages & Components

### Pages (All using AppLayout except Login/Register)

- **Home.jsx** - News feed with posts, uses AppLayout
- **Login.jsx** - Login page (no layout, standalone)
- **Register.jsx** - Registration page (no layout, standalone)
- **UserProfile.jsx** - User profile page, uses AppLayout
- **UserSearch.jsx** - Search users page, uses AppLayout
- **FriendList.jsx** - List of friends, uses AppLayout
- **FriendRequest.jsx** - Friend requests page, uses AppLayout
- **SuggestionFriends.jsx** - Friend suggestions page, uses AppLayout
- **BlockedUsers.jsx** - Blocked users list page, uses AppLayout (NEW)
- **Notifications.jsx** - Notifications history page, uses AppLayout (NEW)
- **GroupList.jsx** - List of groups, uses AppLayout
- **GroupCreate.jsx** - Create group page, uses AppLayout
- **GroupDetail.jsx** - Group detail page, uses AppLayout
- **GroupEdit.jsx** - Edit group page, uses AppLayout
- **AdminDashBoard.jsx** - Admin dashboard, uses AppLayout with showRightSidebar={false}

### Components

- **Header.jsx** - Legacy shared header component (replaced by TopNavigation in AppLayout)
- **NotificationBell.jsx** - Notification bell component
- **PostModal.jsx** - Post modal component
- **CommentItem.jsx** - Comment item component
- **CommentList.jsx** - Comment list component

## UI Consistency

- All authenticated pages now use the `AppLayout` component with TopNavigation, LeftSidebar, and RightSidebar
- Pages without layout: Login, Register (authentication pages)
- TopNavigation includes: Logo, search bar, navigation icons, notification bell, user avatar
- LeftSidebar includes: User profile summary, navigation menu
- RightSidebar includes: Sponsored, friend requests, contacts (currently using test data)
- Consistent styling: `bg-gray-100 min-h-screen font-sans` for page backgrounds

## Redux Store Structure

The Redux store includes the following reducers:

- `auth` - User authentication state
- `post` - Posts state
- `notification` - Notifications state
- `user` - User data state
- `friendship` - Friend relationships state
- `block` - Blocked users state
- `comment` - Comments state
- `commentLike` - Comment reactions state
- `postShare` - Post shares state
- `group` - Groups state
- `groupMember` - Group members state
- `adminUser` - Admin user management state

## API Endpoints Reference

### User Service

- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/register` - Register
- `POST /api/v1/auth/logout` - Logout
- `POST /api/v1/auth/refresh` - Refresh token
- `GET /api/v1/users/profile` - Get current user profile
- `PUT /api/v1/users/profile` - Update profile
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/search?keyword=...` - Search users
- `GET /api/v1/friends/{userId}` - Get friends list
- `POST /api/v1/friend-requests/{userId}` - Send friend request
- `DELETE /api/v1/friend-requests/{userId}` - Cancel friend request
- `PUT /api/v1/friend-requests/{userId}/accept` - Accept friend request
- `PUT /api/v1/friend-requests/{userId}/refuse` - Refuse friend request
- `DELETE /api/v1/friends/{userId}` - Unfriend
- `GET /api/v1/friend-requests/received` - Get received friend requests
- `GET /api/v1/friends/status/{userId}` - Get friendship status
- `POST /api/v1/blocks/{userId}` - Block user
- `DELETE /api/v1/blocks/{userId}` - Unblock user
- `GET /api/v1/blocks` - Get blocked users
- `GET /api/v1/blocks/exists?userId=...&targetId=...` - Check if blocked
- `GET /api/v1/admin/users?active=...` - Get all users (admin)
- `PUT /api/v1/admin/users/{id}` - Update user (admin)

### Post Service

- `POST /api/v1/posts` - Create post
- `GET /api/v1/posts` - Get all posts
- `GET /api/v1/posts/{id}` - Get post by ID
- `PUT /api/v1/posts/{id}` - Update post
- `DELETE /api/v1/posts/{id}` - Delete post
- `GET /api/v1/users/{userId}/posts` - Get user's posts
- `POST /api/v1/posts/{postId}/react` - React to post
- `DELETE /api/v1/posts/{postId}/un-react` - Unreact post
- `GET /api/v1/posts/{postId}/reactions` - Get post reactions
- `GET /api/v1/posts/{postId}/me` - Get my reaction to post
- `GET /api/v1/posts/{postId}/counts` - Get reaction counts
- `POST /api/v1/posts/{postId}/shares` - Share post
- `GET /api/v1/posts/{postId}/shares` - Get post shares
- `DELETE /api/v1/posts/{postId}/shares/{shareId}` - Delete share
- `POST /api/v1/files` - Upload files (multipart)
- `POST /api/v1/groups/{groupId}/posts` - Create group post
- `GET /api/v1/groups/{groupId}/posts` - Get group posts
- `GET /api/v1/groups/{groupId}/posts/pending` - Get pending group posts
- `PUT /api/v1/groups/{groupId}/posts/{postId}/approve` - Approve group post
- `PUT /api/v1/groups/{groupId}/posts/{postId}/pin` - Pin group post
- `PUT /api/v1/groups/{groupId}/posts/{postId}/unpin` - Unpin group post

### Interaction Service

- `POST /api/v1/posts/{postId}/comments` - Create comment
- `GET /api/v1/posts/{postId}/comments` - Get post comments
- `PUT /api/v1/comments/{commentId}` - Update comment
- `DELETE /api/v1/comments/{commentId}` - Delete comment
- `POST /api/v1/comments/{commentId}/reply` - Reply to comment
- `GET /api/v1/comments/{commentId}/replies` - Get comment replies
- `POST /api/v1/comments/{commentId}/react` - React to comment
- `DELETE /api/v1/comments/{commentId}/un-react` - Unreact comment
- `GET /api/v1/comments/{commentId}/reactions` - Get comment reactions
- `GET /api/v1/comments/{commentId}/me` - Get my reaction to comment

### Group Service

- `POST /api/v1/groups` - Create group
- `GET /api/v1/groups` - Get all groups
- `GET /api/v1/groups/{id}` - Get group by ID
- `PUT /api/v1/groups/{id}` - Update group
- `DELETE /api/v1/groups/{id}` - Delete group
- `GET /api/v1/groups/search?keyword=...` - Search groups
- `GET /api/v1/groups/my-group` - Get my groups
- `POST /api/v1/groups/{groupId}/join` - Join group
- `POST /api/v1/groups/{groupId}/left` - Leave group
- `POST /api/v1/groups/{groupId}/pending-members` - Get pending members
- `POST /api/v1/groups/{groupId}/members/{userId}/approve` - Approve member
- `POST /api/v1/groups/{groupId}/members/{userId}/reject` - Reject member
- `DELETE /api/v1/groups/{groupId}/members/{userId}` - Remove member
- `GET /api/v1/groups/{groupId}/members` - Get group members
- `GET /api/v1/groups/{groupId}/members/status` - Get membership status
- `GET /api/v1/groups/{groupId}/members/me/is-admin` - Check if admin
- `PUT /api/v1/groups/{groupId}/members/{userId}/role` - Update member role

### Notification Service

- `POST /api/v1/notifications` - Create notification
- `GET /api/v1/notifications?page=...&size=...` - Get notifications
- `GET /api/v1/notifications/unread-count` - Get unread count
- `PATCH /api/v1/notifications/{id}/read` - Mark as read
- `PATCH /api/v1/notifications/read-all` - Mark all as read
- `DELETE /api/v1/notifications/{id}` - Delete notification
- `DELETE /api/v1/notifications?actorId=...&referenceId=...&type=...` - Delete by query

## Important Notes for AI Agents

1. **API Integration Pattern**: When adding new API integrations, follow the established pattern of ActionType.js, Action.js, and Reducer.js in the appropriate folder under `frontend/src/api/`.

2. **Redux Store**: Always add new reducers to the Redux store in `frontend/src/api/store.js` using combineReducers.

3. **UI Consistency**: All authenticated pages should use the `AppLayout` component with TopNavigation, LeftSidebar, and RightSidebar. Only authentication pages (Login, Register) should not have the layout.

4. **Direct API Calls**: All pages and components have been migrated to use Redux actions. No direct axios calls remain in the frontend codebase.

5. **File Upload**: File uploads use multipart/form-data with `files[]` array and `folder` parameter.

6. **Authentication**: All API calls (except auth endpoints) require the JWT token in the Authorization header as `Bearer ${token}`.

7. **Response Format**: All API responses follow the `ApiResponse<T>` wrapper format with `data`, `message`, and `status` fields.

8. **WebSocket**: Notifications use WebSocket for real-time updates via `useNotificationWebSocket` hook.

9. **Token Management**: Token is stored in localStorage as "token". User info is stored as "user". Token expiration is checked and auto-logout is implemented.

10. **Project Context**: The PROJECT_CONTEXT.md file may not always be up-to-date with the latest API changes. Always check the actual backend controllers for the most accurate API information.

## Tasks Completed

- ✅ Read and analyzed PROJECT_CONTEXT.md
- ✅ Identified missing API integrations in frontend
- ✅ Created User API integration (search, get by ID, update profile)
- ✅ Created FriendShip API integration (centralized in Redux)
- ✅ Created Block API integration
- ✅ Created Comment API integration
- ✅ Created CommentLike API integration
- ✅ Created PostShare API integration
- ✅ Created Group API integration (centralized in Redux)
- ✅ Created GroupMember API integration
- ✅ Created AdminUser API integration
- ✅ Added missing post endpoints (group posts, pin/unpin)
- ✅ Updated Redux store with all new reducers
- ✅ Created AppLayout component system
- ✅ Created TopNavigation component
- ✅ Created LeftSidebar component
- ✅ Created RightSidebar component
- ✅ Refactored all authenticated pages to use AppLayout
- ✅ Created BlockedUsers page
- ✅ Created Notifications page
- ✅ Updated router with new routes
- ✅ Fixed JSX syntax errors in modified pages
- ✅ Migrated BlockedUsers.jsx to Redux actions
- ✅ Migrated GroupEdit.jsx to Redux actions
- ✅ Migrated GroupCreate.jsx to Redux actions
- ✅ Migrated FriendList.jsx to Redux actions
- ✅ Migrated GroupList.jsx to Redux actions
- ✅ Migrated FriendRequest.jsx to Redux actions
- ✅ Migrated Home.jsx to Redux actions
- ✅ Migrated UserSearch.jsx to Redux actions
- ✅ Migrated UserProfile.jsx to Redux actions
- ✅ Migrated GroupDetail.jsx to Redux actions
- ✅ Migrated SuggestionFriends.jsx to Redux actions
- ✅ Migrated CommentList.jsx to Redux actions
- ✅ Migrated CommentItem.jsx to Redux actions
- ✅ Migrated RightSidebar.jsx to Redux actions
- ✅ Migrated AdminDashBoard.jsx to Redux actions
- ✅ Added group post Redux actions (getGroupPosts, getGroupPendingPosts, createGroupPost, approveGroupPost)
- ✅ Added getAllUsers action to User API
- ✅ Updated RightSidebar to use real data from Redux

## Recent Bug Fixes
- ✅ Fixed missing key prop warning in GroupDetail.jsx (line 346, 469) - Changed from `m.user?.id` to `m.id || m.user?.id || Math.random()` for unique keys
- ✅ Added error handling for getMembershipStatus and checkIsAdmin calls in GroupDetail.jsx to prevent 500 errors from crashing the app
- ✅ Fixed friend request accept with undefined userId - Added validation in FriendRequest.jsx and UserProfile.jsx to prevent calling API with undefined IDs
- ✅ Added error handling to GroupMember/Action.js for getMembershipStatus and checkIsAdmin to handle 500/404 errors gracefully (when user is not a member)
- ✅ Added error handling to RightSidebar.jsx for getFriendRequestsReceived and getFriends calls
- ✅ Added error handling to GroupDetail.jsx for getGroupPosts, getGroupPendingPosts, and getPendingMembers calls
- ✅ Added error handling for joinGroup in GroupMember/Action.js and GroupDetail.jsx to handle backend 500 errors
- ✅ Fixed backend authentication issue between group-service and user-service - Created FeignHeaderConfig.java in group-service and added internal.auth.secret configuration to application.properties
- ✅ Fixed approve member with undefined userId - Added validation in GroupDetail.jsx handleApprove, handleRejectMember, handleDeleteMember, and handleChangeRole to prevent calling API with undefined userId

## Keycloak Migration (Production-Ready Architecture)
- ✅ Created comprehensive migration plan (KEYCLOAK_MIGRATION_PLAN.md)
- ✅ Created Docker compose for Keycloak with PostgreSQL (docker-compose.keycloak.yml)
- ✅ Created Keycloak realm configuration with clients, roles, and permissions (keycloak-realm-config.json)
- ✅ Migrated API Gateway to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
  - UserContextFilter.java to extract user info and forward to services
  - gateway-config-keycloak.yml with route configuration
  - Updated pom.xml with OAuth2 dependencies
- ✅ Migrated User Service to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
  - Updated pom.xml with OAuth2 dependencies
- ✅ Migrated Post Service to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
- ✅ Migrated Group Service to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
- ✅ Migrated Interaction Service to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
- ✅ Migrated Notification Service to Keycloak:
  - application-keycloak.yml with OAuth2 configuration
  - SecurityConfigurationKeycloak.java with JWT validation
- ✅ Migrated Frontend to Keycloak:
  - keycloak.js configuration
  - keycloak.js utility functions with token refresh
  - axiosWithKeycloak.js with automatic token refresh
  - KeycloakProvider.jsx React context
  - silent-check-sso.html for silent SSO
- ✅ Created migration summary document (KEYCLOAK_MIGRATION_SUMMARY.md)

**Architecture:**
- Hybrid approach with JWKS caching (gateway + service-level validation)
- Single realm: social-media
- Clients: web-app, mobile-app, service accounts for all services
- Resource-based permissions (user:read, post:create, etc.)
- RS256 asymmetric keys
- Short-lived access tokens (5 minutes)
- Refresh token rotation (7 days)
- OAuth2 client credentials for service-to-service

**Next Steps:**
1. Deploy Keycloak: `docker-compose -f docker-compose.keycloak.yml up -d`
2. Import realm configuration to Keycloak Admin Console
3. Enable keycloak profile for each service
4. Update frontend to use KeycloakProvider and axiosWithKeycloak
5. Remove old authentication code
6. Migrate existing users to Keycloak
7. Test authentication and authorization flows

## Remaining Tasks

- Update PROJECT_CONTEXT.md with latest API changes from user-service
