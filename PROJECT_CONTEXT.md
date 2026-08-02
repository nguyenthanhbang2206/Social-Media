# Project context (Social-media)

## Overview

- Monorepo with Spring Boot microservices under `backend/` and React frontend under `frontend/`.
- Services: `api-gateway`, `discovery-service`, `user-service`, `post-service`, `group-service`, `interaction-service`, `notification-service`, `common-library`.
- Most public REST APIs are under `/api/v1`.
- Service-to-service endpoints are merged into the main controllers (example: notifications use `/api/v1/notifications`).

## Key conventions

- Response wrapper: `ApiResponse<T>`.
- Authentication: JWT access token + refresh token cookie (see `AuthController`).
- File upload: multipart form with `files` array and `folder` param.

## Service endpoints (public)

### user-service

- AuthController (`/api/v1/auth`)
  - POST `/login`
  - POST `/refresh`
  - POST `/logout`
  - POST `/register`
- UserController (`/api/v1`)
  - GET `/users`
  - GET `/users/{id}`
  - GET `/users/profile`
  - PUT `/users/profile`
  - GET `/users/search?keyword=...`
- FriendShipController (`/api/v1`)
  - POST `/friend-requests/{userId}`
  - DELETE `/friend-requests/{userId}`
  - PUT `/friend-requests/{userId}/accept`
  - PUT `/friend-requests/{userId}/refuse`
  - DELETE `/friends/{userId}`
  - GET `/friends/{userId}`
  - GET `/friend-requests/received`
  - GET `/friends/status/{userId}`
- BlockController (`/api/v1/blocks`)
  - POST `/{userId}`
  - DELETE `/{userId}`
  - GET `/`
  - GET `/exists?userId=...&targetId=...`
- AdminUserController (`/api/v1/admin`)
  - GET `/users?active=...`
  - PUT `/users/{id}`

## Frontend AdminUser integration

- `frontend/src/api/AdminUser/Action.js` exports `getAdminUsers(active)` and `updateAdminUser(userId, userData)`.
- `AdminUserController.PUT /api/v1/admin/users/{id}` toggles the user active flag from the path parameter only; the frontend should dispatch `updateAdminUser(userId)`.
- `AdminDashBoard.jsx` should load users with `getAdminUsers()` instead of the older `getAllUsers` name.

## Frontend API optimization notes

- `frontend/src/api/FriendShip/Action.js` now applies a short TTL cache (60s) for `getFriends(userId)` and `getFriendRequestsReceived()` to avoid repeated calls when layout/components remount.
- `frontend/src/api/FriendShip/Reducer.js` tracks `friendsFetchedForUserId`, `friendsLastFetchedAt`, and `friendRequestsLastFetchedAt` for cache decisions.
- `frontend/src/api/Block/Action.js` now applies a short TTL cache (60s) for `getBlockedUsers()`.
- `frontend/src/api/Block/Reducer.js` tracks `blockedUsersLastFetchedAt`.
- `frontend/src/pages/Home.jsx` no longer requests friend list (it was unused there and duplicated sidebar behavior).
- `frontend/src/pages/UserProfile.jsx` now skips `getFriendStatus` and `getBlockedUsers` when viewing own profile, and reuses `auth.user` instead of fetching `/users/{id}` for self profile.
- `frontend/src/api/Auth/Reducer.js` initializes `user` and `token` from `localStorage` to reduce unnecessary profile fetch on app startup.
- `frontend/src/utils/profileNavigation.js` centralizes profile navigation: when target id equals current login user id, routes to `/profile`; otherwise routes to `/users/{id}`.
- `frontend/src/components/layout/TopNavigation.jsx` now includes a real logout button and uses auth state avatar instead of static demo avatar.
- Post cards should render `ownerName` first; `createdBy` is only a legacy fallback. This matches the backend `PostResponse.ownerName` field.
- `frontend/src/api/Post/Action.js#getPostsByUser` now has a practical fallback: if `/users/{id}/posts` fails, it falls back to `/posts` and filters by `userId`; it also uses a short cooldown to avoid spamming a failing endpoint.

## Backend stabilization notes

- `backend/post-service/src/main/java/com/nguyenthanhbang/Social_media/service/impl/PostServiceImpl.java#getPostByUserId` no longer makes an unnecessary cross-service `userClient.getUserById(userId)` call before reading posts. This reduces avoidable 500 errors when user-service internal calls are unstable.

### post-service

- PostController (`/api/v1`)
  - POST `/posts`
  - PUT `/posts/{id}`
  - GET `/users/{userId}/posts`
  - GET `/posts`
  - DELETE `/posts/{id}`
  - GET `/posts/{id}`
  - POST `/groups/{groupId}/posts`
  - GET `/groups/{groupId}/posts`
  - GET `/groups/{groupId}/posts/pending`
  - PUT `/groups/{groupId}/posts/{postId}/approve`
  - PUT `/groups/{groupId}/posts/{postId}/pin`
  - PUT `/groups/{groupId}/posts/{postId}/unpin`
- PostShareController (`/api/v1`)
  - POST `/posts/{postId}/shares`
  - GET `/posts/{postId}/shares`
  - DELETE `/posts/{postId}/shares/{shareId}`
- FileController (`/api/v1/files`)
  - POST `/` (multipart `files[]`, `folder`)

### interaction-service

- CommentController (`/api/v1`)
  - POST `/posts/{postId}/comments`
  - GET `/posts/{postId}/comments`
  - PUT `/comments/{commentId}`
  - DELETE `/comments/{commentId}`
  - POST `/comments/{commentId}/reply`
  - GET `/comments/{commentId}/replies`
- PostLikeController (`/api/v1`)
  - POST `/posts/{postId}/react`
  - GET `/posts/{postId}/reactions`
  - GET `/posts/{postId}/me`
  - DELETE `/posts/{postId}/un-react`
  - GET `/posts/{postId}/counts`
- CommentLikeController (`/api/v1`)
  - POST `/comments/{commentId}/react`
  - GET `/comments/{commentId}/reactions`
  - GET `/comments/{commentId}/me`
  - DELETE `/comments/{commentId}/un-react`

### group-service

- GroupController (`/api/v1`)
  - POST `/groups`
  - GET `/groups`
  - GET `/groups/{id}`
  - PUT `/groups/{id}`
  - DELETE `/groups/{id}`
  - GET `/groups/search?keyword=...`
  - GET `/groups/my-group`
- GroupMemberController (`/api/v1`)
  - POST `/groups/{groupId}/join`
  - POST `/groups/{groupId}/left`
  - POST `/groups/{groupId}/pending-members`
  - POST `/groups/{groupId}/members/{userId}/approve`
  - POST `/groups/{groupId}/members/{userId}/reject`
  - DELETE `/groups/{groupId}/members/{userId}`
  - GET `/groups/{groupId}/members`
  - GET `/groups/{groupId}/members/status`
  - GET `/groups/{groupId}/members/me/is-admin`
  - PUT `/groups/{groupId}/members/{userId}/role`

### notification-service

- NotificationController (`/api/v1/notifications`)
  - POST `/`
  - DELETE `/` (query: `actorId`, `referenceId`, `type`)
  - GET `/` (query: `page`, `size`)
  - GET `/unread-count`
  - PATCH `/{id}/read`
  - PATCH `/read-all`
  - DELETE `/{id}`

## Internal endpoints (service-to-service)

- None. All service-to-service endpoints are in the main controllers.

## Feign clients (service-to-service)

- `common-library`: `NotificationClient` -> `notification-service` (`/api/v1/notifications`)
