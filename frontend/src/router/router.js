import { Routes, Route, Navigate } from "react-router-dom";
import AdminLayout from "../layout/AdminLayout";
import AdminDashboard from "../pages/AdminDashBoard";
import Home from "../pages/Home";
import { Login } from "../pages/Login";
import { Register } from "../pages/Register";
import UserSearch from "../pages/UserSearch";
import UserProfile from "../pages/UserProfile";
import FriendList from "../pages/FriendList";
import FriendRequest from "../pages/FriendRequest";
import SuggestionFriends from "../pages/SuggestionFriends.jsx";
import GroupList from "../pages/GroupList";
import GroupCreate from "../pages/GroupCreate";
import GroupDetail from "../pages/GroupDetail";
import GroupEdit from "../pages/GroupEdit";
import BlockedUsers from "../pages/BlockedUsers";
import Notifications from "../pages/Notifications";

export default function AppRouter() {
  return (
    <Routes>
      {/* Admin routes */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
        {/* Các route admin khác */}
      </Route>

      {/* User routes */}
      <Route path="/" element={<Home />} />
      <Route path="/search" element={<UserSearch></UserSearch>} />
      <Route path="/profile" element={<ProfileRedirect />} />
      <Route path="/users/:userId" element={<UserProfile></UserProfile>} />
      <Route path="/friend-list" element={<FriendList></FriendList>} />
      <Route
        path="/friend-requests"
        element={<FriendRequest></FriendRequest>}
      />
      <Route path="/suggestion-friends" element={<SuggestionFriends />} />
      <Route path="/blocked-users" element={<BlockedUsers />} />
      <Route path="/notifications" element={<Notifications />} />
      <Route path="/groups" element={<GroupList />} />
      <Route path="/groups/create" element={<GroupCreate />} />
      <Route path="/groups/:id" element={<GroupDetail />} />
      <Route path="/groups/:id/edit" element={<GroupEdit />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
}

// Component to redirect /profile to /users/{currentUserId}
function ProfileRedirect() {
  const user = JSON.parse(localStorage.getItem("user"));
  if (user && user.id) {
    return <Navigate to={`/users/${user.id}`} replace />;
  }
  return <Navigate to="/" replace />;
}
