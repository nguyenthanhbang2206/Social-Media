import { Routes, Route } from "react-router-dom";
import AdminLayout from "../layout/AdminLayout";
import AdminDashboard from "../pages/AdminDashBoard";
import UserLayout from "../layout/UserLayout";
import Home from "../pages/Home";
import { Login } from "../pages/Login";
import { Register } from "../pages/Register";
import UserSearch from "../pages/UserSearch";
import UserProfile from "../pages/UserProfile";
import FriendList from "../pages/FriendList";
import FriendRequest from "../pages/FriendRequest";
import GroupList from "../pages/GroupList";
import GroupCreate from "../pages/GroupCreate";
import GroupDetail from "../pages/GroupDetail";
import GroupEdit from "../pages/GroupEdit";

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
      <Route path="/users/:userId" element={<UserProfile></UserProfile>} />
      <Route path="/friend-list" element={<FriendList></FriendList>} />
      <Route
        path="/friend-requests"
        element={<FriendRequest></FriendRequest>}
      />
      <Route path="/groups" element={<GroupList />} />
      <Route path="/groups/create" element={<GroupCreate />} />
      <Route path="/groups/:id" element={<GroupDetail />} />
      <Route path="/groups/:id/edit" element={<GroupEdit />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
}
