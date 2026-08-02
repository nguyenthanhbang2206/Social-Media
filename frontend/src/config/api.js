/**
 * Axios instance dùng chung cho toàn bộ app.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * 1. isPresentInFavorites export không liên quan – xóa
 * 2. Race condition khi nhiều request cùng nhận 401 → fix bằng isRefreshing + queue
 * 3. localStorage.removeItem("token") không cần vì Keycloak quản lý token in-memory
 */
import axios from "axios";
import { getToken, updateToken, logout as keycloakLogout, isAuthenticated } from "../utils/keycloak";

export const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1";

const api = axios.create({
  baseURL: API_URL,
});

// REQUEST INTERCEPTOR: refresh token nếu sắp hết hạn, gắn Authorization header
api.interceptors.request.use(
    async (config) => {
      if (isAuthenticated()) {
        try {
          await updateToken(5);
          const token = getToken();
          if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
          }
        } catch (error) {
          console.error("[api] Token refresh failed in request interceptor:", error);
          await keycloakLogout();
        }
      }
      return config;
    },
    (error) => Promise.reject(error)
);

// RESPONSE INTERCEPTOR: xử lý 401 với queue tránh race condition
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve(token);
  });
  failedQueue = [];
};

let onUnauthorizedCallback = null;

api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        const isLogoutRequest = originalRequest.url?.includes("/auth/logout");
        if (isLogoutRequest) return Promise.reject(error);

        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          }).then((token) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            return api(originalRequest);
          });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
          await updateToken(30);
          const newToken = getToken();
          if (!newToken) throw new Error("No token after refresh");
          processQueue(null, newToken);
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
          return api(originalRequest);
        } catch (refreshError) {
          processQueue(refreshError);
          console.error("[api] 401 refresh failed, logging out:", refreshError);
          await keycloakLogout();
          if (onUnauthorizedCallback) onUnauthorizedCallback();
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

      return Promise.reject(error);
    }
);

export function setOnUnauthorizedCallback(callback) {
  onUnauthorizedCallback = callback;
}

export default api;
