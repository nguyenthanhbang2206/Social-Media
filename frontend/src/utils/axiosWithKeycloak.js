import axios from 'axios';
import keycloak from './keycloak';

// Create axios instance with Keycloak token
const axiosWithKeycloak = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 30000,
});

// Request interceptor to add Authorization header
axiosWithKeycloak.interceptors.request.use(
  async (config) => {
    // Ensure token is valid
    try {
      const refreshed = await keycloak.updateToken(30);
      if (refreshed) {
        console.log('Token was refreshed');
      }
    } catch (error) {
      console.error('Failed to refresh token:', error);
      // Redirect to login if refresh fails
      keycloak.logout();
      return Promise.reject(error);
    }

    // Add Authorization header
    if (keycloak.token) {
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration
axiosWithKeycloak.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Try to refresh token
        const refreshed = await keycloak.updateToken(5);
        if (refreshed) {
          console.log('Token was refreshed on 401');
          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${keycloak.token}`;
          return axiosWithKeycloak(originalRequest);
        } else {
          // If refresh fails, logout
          keycloak.logout();
          return Promise.reject(error);
        }
      } catch (refreshError) {
        console.error('Failed to refresh token on 401:', refreshError);
        keycloak.logout();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosWithKeycloak;
