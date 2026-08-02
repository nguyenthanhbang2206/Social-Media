# UI Design Document - Social Media Project

## Overview

This document provides a comprehensive UI design for the Social Media application, ensuring all backend APIs have corresponding UI features and the interface is consistent across all pages.

## Design Principles

1. **Consistency**: All pages follow the same layout pattern
2. **Completeness**: Every API endpoint has a corresponding UI feature
3. **User-Friendly**: Intuitive navigation and clear visual hierarchy
4. **Responsive**: Works well on different screen sizes
5. **Accessibility**: Clear labels, proper contrast, keyboard navigation
6. **API Efficiency**: Shared layout components should avoid duplicated network calls and reuse cached Redux data when possible

## API Efficiency Rules (Frontend)

- Do not fetch relationship or block state for self-profile pages.
- Avoid page-level calls that duplicate data already fetched by shared layout components.
- Use short-lived cache (TTL) for frequently reused sidebar data (`friends`, `friend requests`, `blocked users`).
- Rehydrate auth state from localStorage to avoid unnecessary profile bootstrap calls.
- After mutations (send/cancel/accept/refuse friend request, block/unblock), refresh only the impacted dataset.
- Centralize profile navigation so clicking current user always routes to `/profile` (no redundant `/users/{myId}` flow).
- When profile-post endpoint is unstable, use a graceful fallback strategy in the client to keep UI responsive while avoiding repeated failing calls.
- For post headers, show the backend-provided owner name (`ownerName`) instead of a generic user label when available.

## Layout Structure

### Main Layout Components

#### 1. AppLayout (Main Container)

- Wraps all authenticated pages
- Contains: Top Navigation, Left Sidebar, Main Content Area, Right Sidebar

#### 2. Top Navigation (Header)

- Fixed at top
- Contains:
  - Logo/Brand name
  - Search bar (global search)
  - Navigation icons (Home, Friends, Groups, Notifications, Messages)
  - User profile dropdown
  - Notification bell with badge
  - Logout button for explicit user session control

#### 3. Left Sidebar (Navigation Menu)

- Fixed on left side
- Contains:
  - User profile summary (avatar, name)
  - Navigation menu items:
    - News Feed
    - My Profile
    - Friends
      - Friend List
      - Friend Requests
      - Friend Suggestions
      - Blocked Users
    - Groups
      - My Groups
      - All Groups
      - Create Group
    - Messages (if implemented)
  - Quick shortcuts

#### 4. Main Content Area

- Center area for page-specific content
- Scrollable independently
- Responsive width

#### 5. Right Sidebar (Optional)

- Contextual information
- Suggestions, trending, etc.
- Can be hidden on smaller screens

## Page Structure & Features

### Authentication Pages (No Sidebar)

#### Login Page

- Email/Username input
- Password input
- Remember me checkbox
- Login button
- Forgot password link
- Register link
- No header/sidebar

#### Register Page

- Full name input
- Email input
- Password input
- Confirm password input
- Date of birth
- Gender selection
- Register button
- Login link
- No header/sidebar

### Main Application Pages (With Layout)

#### 1. Home / News Feed

**APIs Used:**

- `GET /posts` - Get all posts
- `POST /posts` - Create post
- `POST /files` - Upload files
- `POST /posts/{id}/react` - React to post
- `DELETE /posts/{id}/un-react` - Unreact post
- `GET /posts/{id}/reactions` - Get reactions
- `GET /posts/{id}/me` - Get my reaction
- `POST /posts/{id}/shares` - Share post
- `GET /posts/{id}/shares` - Get shares
- `POST /posts/{postId}/comments` - Create comment
- `GET /posts/{postId}/comments` - Get comments
- `POST /comments/{commentId}/react` - React to comment
- `DELETE /comments/{commentId}/un-react` - Unreact comment

**Layout:**

- Left sidebar: Navigation
- Center: Post creation form + Feed
- Right sidebar: Friend suggestions, trending topics

**Features:**

- Create post with text, images, videos
- View posts in chronological order
- React to posts (LIKE, LOVE, HAHA, WOW, SAD, ANGRY)
- Comment on posts
- Share posts
- View reaction counts and who reacted
- Edit/delete own posts
- Report posts

#### 2. User Profile

**APIs Used:**

- `GET /users/{id}` - Get user by ID
- `GET /users/profile` - Get current user profile
- `PUT /users/profile` - Update profile
- `GET /users/{userId}/posts` - Get user's posts
- `GET /friends/{userId}` - Get friends list
- `GET /friends/status/{userId}` - Get friendship status
- `POST /friend-requests/{userId}` - Send friend request
- `DELETE /friend-requests/{userId}` - Cancel friend request
- `PUT /friend-requests/{userId}/accept` - Accept friend request
- `PUT /friend-requests/{userId}/refuse` - Refuse friend request
- `DELETE /friends/{userId}` - Unfriend
- `POST /blocks/{userId}` - Block user
- `DELETE /blocks/{userId}` - Unblock user
- `GET /blocks/exists?userId=...&targetId=...` - Check if blocked

**Layout:**

- Left sidebar: Navigation
- Center: Profile info + User's posts
- Right sidebar: Mutual friends, similar users

**Features:**

- View user profile (avatar, cover, bio, info)
- Edit own profile
- View user's posts
- Friend actions (add friend, accept, unfriend)
- Block/unblock user
- View mutual friends
- View friend count, post count

#### 3. Friend List

**APIs Used:**

- `GET /friends/{userId}` - Get friends list
- `GET /users/search?keyword=...` - Search friends

**Layout:**

- Left sidebar: Navigation with "Friends" section highlighted
- Center: Friend list with search
- Right sidebar: Friend suggestions

**Features:**

- View all friends
- Search friends
- Filter by name
- View friend details
- Quick actions (message, unfriend, block)

#### 4. Friend Requests

**APIs Used:**

- `GET /friend-requests/received` - Get received requests
- `PUT /friend-requests/{userId}/accept` - Accept request
- `PUT /friend-requests/{userId}/refuse` - Refuse request
- `DELETE /friend-requests/{userId}` - Cancel request (for sent requests)

**Layout:**

- Left sidebar: Navigation with "Friend Requests" highlighted
- Center: List of friend requests
- Right sidebar: Friend suggestions

**Features:**

- View received friend requests
- Accept/decline requests
- View sent requests
- Cancel sent requests
- See mutual friends for each request

#### 5. Friend Suggestions

**APIs Used:**

- `GET /users` - Get all users (for suggestions)
- `GET /friends/{userId}` - Get friends (to filter)
- `POST /friend-requests/{userId}` - Send friend request

**Layout:**

- Left sidebar: Navigation with "Friend Suggestions" highlighted
- Center: Suggested friends grid/list
- Right sidebar: People you may know

**Features:**

- View friend suggestions
- Send friend requests
- See mutual friends
- See why suggested (mutual friends, same groups, etc.)

#### 6. Blocked Users

**APIs Used:**

- `GET /blocks` - Get blocked users
- `DELETE /blocks/{userId}` - Unblock user

**Layout:**

- Left sidebar: Navigation with "Blocked Users" highlighted
- Center: List of blocked users
- Right sidebar: Privacy settings

**Features:**

- View all blocked users
- Unblock users
- Block new users
- See block reason (if any)

#### 7. Group List

**APIs Used:**

- `GET /groups` - Get all groups
- `GET /groups/my-group` - Get my groups
- `GET /groups/search?keyword=...` - Search groups
- `POST /groups` - Create group

**Layout:**

- Left sidebar: Navigation with "Groups" section highlighted
- Center: Group list with tabs (My Groups, All Groups)
- Right sidebar: Suggested groups

**Features:**

- View all groups
- View my groups
- Search groups
- Create new group
- Filter by privacy (public/private)
- See member count, post count

#### 8. Group Detail

**APIs Used:**

- `GET /groups/{id}` - Get group by ID
- `PUT /groups/{id}` - Update group
- `DELETE /groups/{id}` - Delete group
- `POST /groups/{groupId}/join` - Join group
- `POST /groups/{groupId}/left` - Leave group
- `GET /groups/{groupId}/members` - Get group members
- `GET /groups/{groupId}/members/status` - Get membership status
- `GET /groups/{groupId}/members/me/is-admin` - Check if admin
- `POST /groups/{groupId}/posts` - Create group post
- `GET /groups/{groupId}/posts` - Get group posts
- `GET /groups/{groupId}/posts/pending` - Get pending posts (admin)
- `PUT /groups/{groupId}/posts/{postId}/approve` - Approve post (admin)
- `PUT /groups/{groupId}/posts/{postId}/pin` - Pin post (admin)
- `PUT /groups/{groupId}/posts/{postId}/unpin` - Unpin post (admin)
- `POST /groups/{groupId}/pending-members` - Get pending members (admin)
- `POST /groups/{groupId}/members/{userId}/approve` - Approve member (admin)
- `POST /groups/{groupId}/members/{userId}/reject` - Reject member (admin)
- `DELETE /groups/{groupId}/members/{userId}` - Remove member (admin)
- `PUT /groups/{groupId}/members/{userId}/role` - Update member role (admin)

**Layout:**

- Left sidebar: Navigation
- Center: Group info + Tabs (Posts, Members, Settings)
- Right sidebar: Group members, similar groups

**Features:**

- View group info
- Join/leave group
- Create/view group posts
- Admin features:
  - Approve/reject pending members
  - Approve/reject pending posts
  - Pin/unpin posts
  - Manage member roles
  - Edit group settings
- View group members
- Search within group

#### 9. Group Create

**APIs Used:**

- `POST /groups` - Create group

**Layout:**

- Left sidebar: Navigation
- Center: Group creation form
- Right sidebar: Group creation tips

**Features:**

- Group name
- Description
- Privacy setting (public/private)
- Group image
- Cover image
- Category selection
- Create button

#### 10. Group Edit

**APIs Used:**

- `GET /groups/{id}` - Get group by ID
- `PUT /groups/{id}` - Update group

**Layout:**

- Left sidebar: Navigation
- Center: Group edit form
- Right sidebar: Group settings summary

**Features:**

- Edit group name
- Edit description
- Change privacy
- Update images
- Delete group (admin only)

#### 11. User Search

**APIs Used:**

- `GET /users/search?keyword=...` - Search users
- `GET /users/{id}` - Get user details
- `GET /friends/status/{userId}` - Check friendship status

**Layout:**

- Left sidebar: Navigation
- Center: Search results
- Right sidebar: Search filters

**Features:**

- Search by name
- Filter by location, gender, age
- View user profiles
- Add friend directly from search
- See mutual friends

#### 12. Notifications

**APIs Used:**

- `GET /notifications?page=...&size=...` - Get notifications
- `GET /notifications/unread-count` - Get unread count
- `PATCH /notifications/{id}/read` - Mark as read
- `PATCH /notifications/read-all` - Mark all as read
- `DELETE /notifications/{id}` - Delete notification

**Layout:**

- Left sidebar: Navigation
- Center: Notification list
- Right sidebar: Notification settings

**Features:**

- View all notifications
- Filter by type (friend requests, comments, likes, etc.)
- Mark as read/unread
- Mark all as read
- Delete notifications
- Notification settings

#### 13. Admin Dashboard

**APIs Used:**

- `GET /admin/users?active=...` - Get all users (admin)
- `PUT /admin/users/{id}` - Update user (admin)
- `GET /groups` - Get all groups (admin overview)
- `GET /posts` - Get all posts (admin overview)

**Layout:**

- Left sidebar: Admin navigation
- Center: Dashboard content
- Right sidebar: Quick stats

**Features:**

- User management (view, activate/deactivate, update)
- Group management (view, delete)
- Post management (view, delete)
- Statistics (users, groups, posts)
- Reports (if implemented)
- System settings

## Component Library

### Reusable Components

#### 1. Button

- Primary, secondary, danger variants
- Loading state
- Disabled state
- Icon support

#### 2. Input

- Text, email, password, number
- Label and error message
- Validation
- Icon prefix/suffix

#### 3. Card

- Content container
- Header, body, footer
- Hover effects
- Action buttons

#### 4. Modal

- Title, content, actions
- Close button
- Backdrop
- Animation

#### 5. Dropdown

- Menu items
- Click to open
- Close on outside click
- Keyboard navigation

#### 6. Avatar

- Image or initials
- Size variants
- Online status indicator
- Click to view profile

#### 7. Badge

- Notification count
- Status indicator
- Color variants

#### 8. Tabs

- Horizontal or vertical
- Active state
- Content panels
- Keyboard navigation

#### 9. List

- Item list
- Selection
- Actions per item
- Infinite scroll

#### 10. Skeleton

- Loading placeholder
- Different types (text, image, card)
- Animation

## Color Scheme

### Primary Colors

- Primary Blue: #1877F2 (Facebook-like)
- Secondary Blue: #42b72a (for success actions)
- Danger Red: #dc3545
- Warning Orange: #ffc107
- Success Green: #28a745

### Neutral Colors

- Background: #f0f2f5
- Surface: #ffffff
- Text Primary: #050505
- Text Secondary: #65676b
- Border: #ced0d4
- Divider: #e4e6eb

## Typography

### Font Family

- Primary: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif

### Font Sizes

- H1: 32px
- H2: 24px
- H3: 20px
- H4: 16px
- Body: 14px
- Small: 12px

### Font Weights

- Regular: 400
- Medium: 500
- Semibold: 600
- Bold: 700

## Spacing

### Scale

- XS: 4px
- SM: 8px
- MD: 16px
- LG: 24px
- XL: 32px
- XXL: 48px

## Icons

Use icon library (Lucide React or similar) for consistency:

- Navigation icons
- Action icons
- Status icons
- Social icons

## Responsive Design

### Breakpoints

- Mobile: < 640px
- Tablet: 640px - 1024px
- Desktop: > 1024px

### Behavior

- Mobile: Hide right sidebar, collapse left sidebar to bottom nav
- Tablet: Show left sidebar as icons, hide right sidebar
- Desktop: Full layout

## Accessibility

### Keyboard Navigation

- Tab through interactive elements
- Enter/Space to activate
- Escape to close modals/dropdowns
- Arrow keys for lists

### Screen Readers

- ARIA labels
- Alt text for images
- Semantic HTML
- Focus indicators

### Color Contrast

- WCAG AA compliant
- Minimum 4.5:1 for normal text
- Minimum 3:1 for large text

## Implementation Priority

### Phase 1: Core Layout

1. Create AppLayout component
2. Create TopNavigation component
3. Create LeftSidebar component
4. Create RightSidebar component

### Phase 2: Authentication Pages

1. Redesign Login page
2. Redesign Register page

### Phase 3: Main Pages

1. Redesign Home/News Feed
2. Redesign User Profile
3. Redesign Friend-related pages
4. Redesign Group-related pages

### Phase 4: Additional Features

1. Notifications page
2. Search page
3. Admin Dashboard

### Phase 5: Polish

1. Add animations
2. Improve accessibility
3. Performance optimization
4. Testing

## API to UI Mapping

### Complete API Coverage Checklist

#### User Service

- [x] Auth: Login, Register, Logout - Login.jsx, Register.jsx, App.js
- [x] User: Get all, Get by ID, Get profile, Update profile, Search - UserProfile.jsx, UserSearch.jsx
- [x] FriendShip: All endpoints - FriendList.jsx, FriendRequest.jsx, SuggestionFriends.jsx
- [x] Block: All endpoints - BlockedUsers.jsx
- [x] AdminUser: Get all users, Update user - AdminDashBoard.jsx

#### Post Service

- [x] Post: CRUD, Group posts - Home.jsx, GroupDetail.jsx
- [x] PostShare: Share, Get shares, Delete share - Home.jsx
- [x] File: Upload - Home.jsx

#### Interaction Service

- [x] Comment: All endpoints - Home.jsx, UserProfile.jsx
- [x] PostLike: All endpoints - Home.jsx, UserProfile.jsx
- [x] CommentLike: All endpoints - Home.jsx, UserProfile.jsx

#### Group Service

- [x] Group: CRUD, Search, My groups - GroupList.jsx, GroupCreate.jsx, GroupEdit.jsx, GroupDetail.jsx
- [x] GroupMember: All endpoints - GroupDetail.jsx

#### Notification Service

- [x] Notification: All endpoints - NotificationBell.jsx, Notifications.jsx

## Missing UI Features

Based on API analysis, the following UI features are missing or incomplete:

1. **Sent Friend Requests** - Can view received but not sent requests
2. **Post Reactions Modal** - Can react but can't see who reacted in detail
3. **Comment Replies UI** - API exists but UI may be incomplete
4. **Share History** - Can share but can't view share history
5. **Group Settings** - Edit exists but comprehensive settings missing
6. **Admin User Management** - Basic exists but needs full management UI
7. **Group Categories** - No category selection when creating groups
8. **Post Privacy Settings** - Only public/private, missing custom privacy
9. **Message/Chat** - No messaging feature (if API exists)
10. **Search Filters** - Basic search exists but no advanced filters
11. **User Activity Feed** - Can see posts but no comprehensive activity view
12. **Group Statistics** - No stats for groups
13. **Friend Suggestions Algorithm** - Basic exists but could be improved

## Next Steps

1. Create the layout components (AppLayout, TopNavigation, LeftSidebar, RightSidebar)
2. Refactor existing pages to use the new layout
3. Create missing pages (BlockedUsers, Notifications, etc.)
4. Implement missing features
5. Test all API integrations
6. Ensure consistency across all pages
