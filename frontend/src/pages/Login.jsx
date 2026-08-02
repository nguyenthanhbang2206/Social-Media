/**
 * Login page.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * - Page này bị block bởi `onLoad: 'login-required'` trong keycloak.js
 *   nên không bao giờ hiển thị (app redirect sang Keycloak trước khi render).
 *   Fix: đã sửa keycloak.js dùng check-sso → page này render bình thường.
 *
 * Thiết kế: Login page là wrapper đẹp chứa nút bấm redirect sang Keycloak.
 * User nhập credentials trên Keycloak login page (không phải trên app).
 * Sau khi xác thực xong, Keycloak redirect về app và App.js xử lý sync.
 */
import React, { useState } from "react";
import { login, register } from "../utils/keycloak";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

export const Login = () => {
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "error" });
  const [isLoading, setIsLoading] = useState(false);

  const handleCloseSnackbar = () => setSnackbar((s) => ({ ...s, open: false }));

  const handleLogin = async () => {
    setIsLoading(true);
    try {
      // Redirect sang Keycloak login page
      // Keycloak sẽ redirect về app sau khi xác thực xong
      await login();
    } catch (error) {
      setSnackbar({
        open: true,
        message: "Không thể kết nối đến hệ thống đăng nhập. Vui lòng thử lại.",
        severity: "error",
      });
      setIsLoading(false);
      console.error("[Login] Keycloak login error:", error);
    }
  };

  const handleRegister = async () => {
    setIsLoading(true);
    try {
      // Redirect sang Keycloak register page
      await register();
    } catch (error) {
      setSnackbar({
        open: true,
        message: "Không thể mở trang đăng ký. Vui lòng thử lại.",
        severity: "error",
      });
      setIsLoading(false);
      console.error("[Login] Keycloak register error:", error);
    }
  };

  return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100 font-sans">
        <div className="bg-white shadow-lg rounded-xl p-8 w-full max-w-md">
          {/* Logo */}
          <div className="text-center mb-8">
            <img
                src="https://static.xx.fbcdn.net/rsrc.php/yo/r/iRmz9lCMBD2.ico"
                alt="Logo"
                className="w-16 h-16 mx-auto mb-4"
            />
            <h1 className="text-3xl font-bold text-blue-600">Social Media</h1>
            <p className="text-gray-600 mt-2">Kết nối với bạn bè và thế giới</p>
          </div>

          <div className="space-y-4">
            {/* Login Button */}
            <button
                onClick={handleLogin}
                disabled={isLoading}
                className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Đang chuyển hướng..." : "Đăng nhập"}
            </button>

            {/* Divider */}
            <div className="relative my-2">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">hoặc</span>
              </div>
            </div>

            {/* Register Button */}
            <button
                onClick={handleRegister}
                disabled={isLoading}
                className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Tạo tài khoản mới
            </button>
          </div>

          <p className="text-center text-xs text-gray-400 mt-6">
            Đăng nhập được bảo mật bởi Keycloak
          </p>
        </div>

        <Snackbar
            open={snackbar.open}
            autoHideDuration={4000}
            onClose={handleCloseSnackbar}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
        >
          <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: "100%" }}>
            {snackbar.message}
          </Alert>
        </Snackbar>
      </div>
  );
};
