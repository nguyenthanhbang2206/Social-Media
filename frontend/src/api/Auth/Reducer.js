/**
 * Auth Reducer.
 *
 * Thay đổi so với bản gốc:
 * - Bỏ `token` khỏi state – token do Keycloak JS quản lý in-memory,
 *   không cần lưu trong Redux. Lấy token dùng getToken() từ keycloak.js.
 * - Bỏ `localStorage.getItem("token")` trong initialState (không dùng nữa).
 * - Giữ user từ localStorage để tránh mất state khi F5.
 */
import {
  GET_USER_PROFILE_REQUEST,
  GET_USER_PROFILE_SUCCESS,
  GET_USER_PROFILE_FAILURE,
  LOGIN_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGOUT,
  UPDATE_USER_PROFILE_REQUEST,
  UPDATE_USER_PROFILE_SUCCESS,
  UPDATE_USER_PROFILE_FAILURE,
} from "./ActionType";

const initialState = {
  // Restore user từ localStorage sau khi F5
  user: JSON.parse(localStorage.getItem("user") || "null"),
  isLoading: false,
  error: null,
  success: null,
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case LOGIN_REQUEST:
    case GET_USER_PROFILE_REQUEST:
    case UPDATE_USER_PROFILE_REQUEST:
      return { ...state, isLoading: true, error: null, success: null };

    case LOGIN_SUCCESS:
      return {
        ...state,
        isLoading: false,
        // user sẽ được set bởi GET_USER_PROFILE_SUCCESS ngay sau đó
        error: null,
        success: "Đăng nhập thành công!",
      };

    case GET_USER_PROFILE_SUCCESS:
      return {
        ...state,
        isLoading: false,
        user: action.payload,
        success: "Tải thông tin người dùng thành công!",
      };

    case UPDATE_USER_PROFILE_SUCCESS:
      return {
        ...state,
        isLoading: false,
        user: action.payload,
        success: "Cập nhật thông tin thành công!",
      };

    case LOGOUT:
      return {
        ...initialState,
        user: null,
        success: null,
      };

    case LOGIN_FAILURE:
    case GET_USER_PROFILE_FAILURE:
    case UPDATE_USER_PROFILE_FAILURE:
      return { ...state, isLoading: false, error: action.payload, success: null };

    default:
      return state;
  }
};

export default authReducer;
