/**
 * Auth Redux Actions – Keycloak PKCE flow.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * 1. syncUserWithKeycloak gọi POST /auth/sync – endpoint này KHÔNG TỒN TẠI
 *    trong backend. Backend có POST /auth/register và POST /auth/login nhưng
 *    không có /auth/sync. Fix: tạo endpoint sync hoặc dùng /users/profile.
 *    → Chọn cách dùng GET /users/profile (đã có) để lấy thông tin user sau
 *      khi Keycloak xác thực. Nếu 404 → user chưa có trong local DB → tự
 *      động được HeaderAuthenticationConverter tạo (auto-provision).
 *
 * 2. register action dispatch LOGIN_FAILURE với message "Please use Keycloak
 *    registration" – gây confuse. Xóa action này, không cần thiết.
 *
 * 3. getProfile gọi keycloakLogout() khi nhận 401 – đúng nhưng nên dùng
 *    logout action để clean Redux state luôn.
 */
import {
  GET_USER_PROFILE_REQUEST,
  GET_USER_PROFILE_SUCCESS,
  GET_USER_PROFILE_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGIN_FAILURE,
  LOGOUT,
} from "./ActionType";
import api from "../../config/api";
import {
  getToken,
  getEmail,
  getFullName,
  getUserId,
  getRoles,
  logout as keycloakLogout,
} from "../../utils/keycloak";

/**
 * Gọi sau khi Keycloak xác thực thành công.
 *
 * Flow:
 *   1. Lấy token + claims từ Keycloak instance (đã in-memory)
 *   2. Gọi GET /users/profile với Bearer token
 *      → API Gateway validate token → inject X-User-* headers → user-service
 *      → user-service trả về user profile từ local DB
 *      → Nếu user chưa có trong DB: HeaderAuthenticationConverter auto-provision
 *   3. Lưu vào Redux state + localStorage
 *   4. Navigate dựa theo role
 */
export const syncUserWithKeycloak = (navigate) => async (dispatch) => {
  dispatch({ type: LOGIN_REQUEST });

  try {
    const token = getToken();
    if (!token) throw new Error("No valid Keycloak token");

    // Lấy profile từ backend – token đã được gắn bởi axios interceptor
    const { data } = await api.get("/users/profile");
    const userProfile = data.data;

    localStorage.setItem("user", JSON.stringify(userProfile));

    dispatch({ type: LOGIN_SUCCESS, payload: token });
    dispatch({ type: GET_USER_PROFILE_SUCCESS, payload: userProfile });

    // Điều hướng theo role
    const roles = getRoles();
    if (roles.includes("ADMIN")) {
      navigate("/admin");
    } else {
      navigate("/");
    }
  } catch (error) {
    const message =
        error.response?.data?.message || error.message || "Failed to load user profile";
    dispatch({ type: LOGIN_FAILURE, payload: message });
    console.error("[syncUserWithKeycloak]", message);
  }
};

/**
 * Lấy profile user hiện tại từ backend.
 * Gọi khi Redux state mất user (ví dụ: F5 trang).
 */
export const getProfile = () => async (dispatch) => {
  dispatch({ type: GET_USER_PROFILE_REQUEST });

  try {
    const { data } = await api.get("/users/profile");
    localStorage.setItem("user", JSON.stringify(data.data));
    dispatch({ type: GET_USER_PROFILE_SUCCESS, payload: data.data });
  } catch (error) {
    const message =
        error.response?.data?.message || "Failed to load profile. Please try again.";
    dispatch({ type: GET_USER_PROFILE_FAILURE, payload: message });
    console.error("[getProfile]", message);

    if (error.response?.status === 401) {
      dispatch(logout());
    }
  }
};

/**
 * Đăng xuất:
 *   1. Keycloak revoke session (xóa cookie, invalidate refresh token)
 *   2. Clear Redux state + localStorage
 */
export const logout = () => async (dispatch) => {
  try {
    await keycloakLogout();
  } catch (error) {
    console.error("[logout] Keycloak logout error:", error);
  }
  localStorage.removeItem("user");
  dispatch({ type: LOGOUT });
};
