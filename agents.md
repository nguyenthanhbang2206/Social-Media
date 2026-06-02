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

## Frontend Pages & Components

### Pages
- **Home.jsx** - News feed with posts, has header/navbar
- **Login.jsx** - Login page (no header)
- **Register.jsx** - Registration page (no header)
- **UserProfile.jsx** - User profile page, now has header
- **UserSearch.jsx** - Search users page, now has header
- **FriendList.jsx** - List of friends, has header/navbar
- **FriendRequest.jsx** - Friend requests page, now has header
- **SuggestionFriends.jsx** - Friend suggestions page, now has header
- **GroupList.jsx** - List of groups, now has header
- **GroupCreate.jsx** - Create group page, now has header
- **GroupDetail.jsx** - Group detail page, now has header
- **GroupEdit.jsx** - Edit group page (needs header)
- **AdminDashBoard.jsx** - Admin dashboard (needs header)

### Components
- **Header.jsx** - Shared header component with logo, search, notifications, avatar
- **NotificationBell.jsx** - Notification bell component
- **PostModal.jsx** - Post modal component
- **CommentItem.jsx** - Comment item component
- **CommentList.jsx** - Comment list component

## UI Consistency
- All authenticated pages now use the shared `Header` component
- Pages without header: Login, Register (authentication pages)
- Header includes: Logo, search bar, notification bell, user avatar
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

3. **UI Consistency**: All authenticated pages should use the shared `Header` component. Only authentication pages (Login, Register) should not have the header.

4. **Direct API Calls**: Some pages still use direct axios calls instead of Redux actions. These should be migrated to use Redux actions for consistency.

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
- ✅ Created shared Header component
- ✅ Added header to all pages that were missing it
- ✅ Fixed JSX syntax errors in modified pages

## Remaining Tasks (Optional)
- Add header to GroupEdit.jsx
- Add header to AdminDashBoard.jsx
- Migrate direct axios calls in some pages to use Redux actions
- Update PROJECT_CONTEXT.md with latest API changes from user-service
