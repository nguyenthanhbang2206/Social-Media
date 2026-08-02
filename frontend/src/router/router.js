/**
 * App Router.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * - Không có route protection → user chưa login vẫn vào được /, /profile, ...
 *   và nhận 401 từ API rồi bị redirect vô hồi.
 *   Fix: thêm ProtectedRoute redirect về /login nếu chưa authenticated.
 *
 * - Không có AdminRoute bảo vệ /admin khỏi user thường.
 *   Fix: thêm AdminRoute kiểm tra role ADMIN.
 */
import { Routes, Route, Navigate } from "react-router-dom";
import { isAuthenticated, hasRole } from "../utils/keycloak";
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

/**
 * Bảo vệ route yêu cầu đăng nhập.
 * Nếu chưa authenticated → redirect /login.
 */
function ProtectedRoute({ children }) {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }
    return children;
}

/**
 * Bảo vệ route chỉ dành cho ADMIN.
 * Nếu chưa đăng nhập → /login. Đã đăng nhập nhưng không phải ADMIN → /.
 */
function AdminRoute({ children }) {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }
    if (!hasRole("ADMIN")) {
        return <Navigate to="/" replace />;
    }
    return children;
}

/**
 * Route chỉ dành cho user CHƯA đăng nhập (login, register).
 * Nếu đã authenticated → redirect về /.
 */
function GuestRoute({ children }) {
    if (isAuthenticated()) {
        return <Navigate to="/" replace />;
    }
    return children;
}

export default function AppRouter() {
    return (
        <Routes>
            {/* ── Admin routes ─────────────────────────────────────────────── */}
            <Route
                path="/admin"
                element={
                    <AdminRoute>
                        <AdminLayout />
                    </AdminRoute>
                }
            >
                <Route index element={<AdminDashboard />} />
            </Route>

            {/* ── Auth routes (chỉ cho guest) ──────────────────────────────── */}
            <Route
                path="/login"
                element={
                    <GuestRoute>
                        <Login />
                    </GuestRoute>
                }
            />
            <Route
                path="/register"
                element={
                    <GuestRoute>
                        <Register />
                    </GuestRoute>
                }
            />

            {/* ── Protected routes (yêu cầu đăng nhập) ───────────────────── */}
            <Route
                path="/"
                element={
                    <ProtectedRoute>
                        <Home />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/search"
                element={
                    <ProtectedRoute>
                        <UserSearch />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/profile"
                element={
                    <ProtectedRoute>
                        <ProfileRedirect />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/users/:userId"
                element={
                    <ProtectedRoute>
                        <UserProfile />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/friend-list"
                element={
                    <ProtectedRoute>
                        <FriendList />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/friend-requests"
                element={
                    <ProtectedRoute>
                        <FriendRequest />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/suggestion-friends"
                element={
                    <ProtectedRoute>
                        <SuggestionFriends />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/blocked-users"
                element={
                    <ProtectedRoute>
                        <BlockedUsers />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/notifications"
                element={
                    <ProtectedRoute>
                        <Notifications />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/groups"
                element={
                    <ProtectedRoute>
                        <GroupList />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/groups/create"
                element={
                    <ProtectedRoute>
                        <GroupCreate />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/groups/:id"
                element={
                    <ProtectedRoute>
                        <GroupDetail />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/groups/:id/edit"
                element={
                    <ProtectedRoute>
                        <GroupEdit />
                    </ProtectedRoute>
                }
            />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}

function ProfileRedirect() {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (user?.id) {
        return <Navigate to={`/users/${user.id}`} replace />;
    }
    return <Navigate to="/" replace />;
}
