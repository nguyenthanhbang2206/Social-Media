import {
  GET_USER_PROFILE_SUCCESS,
  GET_USER_PROFILE_REQUEST,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  REGISTER_REQUEST,
  REGISTER_SUCCESS,
  REGISTER_FAILURE,
  LOGOUT,
  LOGIN_FAILURE,
  GET_USER_PROFILE_FAILURE,
} from "./ActionType";
import api from "../../config/api";

export const register =
  ({ userData, navigate }) =>
  async (dispatch) => {
    dispatch({ type: REGISTER_REQUEST });
    try {
      const { data } = await api.post(`/auth/register`, userData);

      // Điều hướng đến trang đăng nhập sau khi đăng ký thành công
      navigate("/login");

      // Dispatch thành công
      dispatch({ type: REGISTER_SUCCESS, payload: data });
      console.log("Register Successful:", data.message);
    } catch (error) {
      // Xử lý lỗi từ phản hồi API
      console.error("Register Error:", error);
      const errorMessage =
        error.response?.data?.message ||
        "Registration failed. Please try again.";
      dispatch({ type: REGISTER_FAILURE, payload: errorMessage });
    }
  };
export const login =
  ({ userData, navigate }) =>
  async (dispatch) => {
    dispatch({ type: LOGIN_REQUEST });
    try {
      console.log("User Data:", userData); // Log userData to check its value
      const { data } = await api.post(`/auth/login`, userData);

      // Lưu accessToken và thông tin người dùng vào localStorage
      localStorage.setItem("token", data.data.accessToken);
      localStorage.setItem("user", JSON.stringify(data.data.user));

      // Điều hướng dựa trên vai trò của người dùng
      if (data.data.user.role === "ADMIN") {
        navigate("/admin");
      } else {
        navigate("/");
      }

      // Dispatch thành công
      dispatch({ type: LOGIN_SUCCESS, payload: data.data.accessToken });
    } catch (error) {
      // Xử lý lỗi từ phản hồi API
      const errorMessage =
        error.response?.data?.message || "Login failed. Please try again.";
      dispatch({ type: LOGIN_FAILURE, payload: errorMessage });
      console.error("Login Error:", errorMessage);
    }
  };
export const getProfile = (token) => async (dispatch) => {
  dispatch({ type: GET_USER_PROFILE_REQUEST });
  try {
    const { data } = await api.get("/users/profile", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // Lưu thông tin người dùng vào localStorage
    localStorage.setItem("user", JSON.stringify(data.data));

    // Dispatch thành công
    dispatch({ type: GET_USER_PROFILE_SUCCESS, payload: data.data });
  } catch (error) {
    // Xử lý lỗi từ phản hồi API
    const errorMessage =
      error.response?.data?.message ||
      "Failed to load profile. Please try again.";
    dispatch({ type: GET_USER_PROFILE_FAILURE, payload: errorMessage });
    console.error("Get Profile Error:", errorMessage);

    // Nếu lỗi là 401 (Unauthorized), xóa token và điều hướng đến trang đăng nhập
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      dispatch({ type: LOGOUT });
    }
  }
};

export const logout = () => async (dispatch) => {
  try {
    await api.post("/auth/logout");
  } catch (error) {
    // Ignore logout errors and still clear client state
  }
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  dispatch({ type: LOGOUT });
};
// eslint-disable-next-line no-unused-vars
