import axios from "axios";

export const API_URL = "http://localhost:8080/api/v1";

const api = axios.create({
  baseURL: API_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

let onUnauthorized = null;

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      if (onUnauthorized) onUnauthorized();
    }
    return Promise.reject(error);
  }
);

export function setOnUnauthorizedCallback(callback) {
  onUnauthorized = callback;
}

// Hàm decode base64 payload của JWT để lấy exp
export function getTokenExpiration(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded.exp; // thời gian hết hạn tính bằng giây (Unix timestamp)
  } catch {
    return null;
  }
}

export default api;

export const isPresentInFavorites = (favorites, restaurant) => {
  for (let item of favorites) {
    if (item.id === restaurant.id) {
      return true;
    }
  }
  return false;
};