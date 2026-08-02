/**
 * App.js – root component với Keycloak initialization.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * 1. initKeycloak() với `onLoad: 'login-required'` → toàn bộ app block tại
 *    loading screen → /login và /register không bao giờ render được.
 *    Fix: dùng check-sso (đã sửa trong keycloak.js) → App render bình thường,
 *    các protected route tự handle redirect.
 *
 * 2. Không có ProtectedRoute → tất cả route đều public, ai cũng vào được
 *    khi biết URL, dù chưa login.
 *    Fix: thêm ProtectedRoute component bên trong router.js
 *
 * 3. useEffect [keycloakInitialized, isAuthenticated(), !user] → gọi getProfile()
 *    ngay cả khi user chưa login (isAuthenticated() = false sau check-sso).
 *    Fix: chỉ gọi getProfile khi isAuthenticated() = true.
 *
 * 4. syncUserWithKeycloak được gọi nhưng action này cũ gọi POST /auth/sync
 *    không tồn tại. Fix: dùng action mới dùng GET /users/profile.
 */
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getProfile, logout, syncUserWithKeycloak } from "./api/Auth/Action";
import { setOnUnauthorizedCallback } from "./config/api";
import AppRouter from "./router/router";
import useNotificationWebSocket from "./config/useNotificationWebSocket";
import { initKeycloak, isAuthenticated, getToken } from "./utils/keycloak";

function App() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [keycloakInitialized, setKeycloakInitialized] = useState(false);
  const [keycloakError, setKeycloakError] = useState(false);

  useNotificationWebSocket(keycloakInitialized);

  // ── Bước 1: Khởi tạo Keycloak ──────────────────────────────────────────
  useEffect(() => {
    const initializeKeycloak = async () => {
      try {
        // check-sso: trả về true/false, KHÔNG block hay redirect
        const authenticated = await initKeycloak();
        setKeycloakInitialized(true);

        if (authenticated) {
          // Có session Keycloak → sync user profile với backend
          dispatch(syncUserWithKeycloak(navigate));
        }
        // Nếu không authenticated: app render bình thường
        // Router sẽ xử lý điều hướng tới /login cho protected routes
      } catch (error) {
        console.error("[App] Keycloak initialization error:", error);
        setKeycloakError(true);
        setKeycloakInitialized(true); // vẫn cho app render, hiển thị lỗi
      }
    };

    initializeKeycloak();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // ── Bước 2: Nếu đã init và đang authenticated nhưng Redux mất user (F5) ─
  useEffect(() => {
    if (keycloakInitialized && isAuthenticated() && !user) {
      dispatch(getProfile());
    }
  }, [keycloakInitialized, user, dispatch]);

  // ── Bước 3: Đăng ký callback khi bị 401 không thể refresh ───────────────
  useEffect(() => {
    setOnUnauthorizedCallback(() => {
      dispatch(logout());
      navigate("/login");
    });
  }, [navigate, dispatch]);

  // Loading: chờ Keycloak init xong (thường < 500ms với check-sso)
  if (!keycloakInitialized) {
    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Đang tải...</p>
          </div>
        </div>
    );
  }

  // Lỗi kết nối Keycloak (server down, sai config...)
  if (keycloakError) {
    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
          <div className="text-center bg-white p-8 rounded-xl shadow-lg max-w-md">
            <div className="text-red-500 text-5xl mb-4">⚠️</div>
            <p className="text-red-600 font-semibold mb-2">
              Không thể kết nối hệ thống xác thực
            </p>
            <p className="text-gray-500 text-sm mb-6">
              Keycloak server có thể đang offline hoặc cấu hình sai.
            </p>
            <button
                onClick={() => window.location.reload()}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Thử lại
            </button>
          </div>
        </div>
    );
  }

  return <AppRouter />;
}

export default App;
