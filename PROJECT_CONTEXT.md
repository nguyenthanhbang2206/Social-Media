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
