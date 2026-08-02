import api from "../../config/api";
import {
  GET_USER_BY_ID_REQUEST,
  GET_USER_BY_ID_SUCCESS,
  GET_USER_BY_ID_FAILURE,
  SEARCH_USERS_REQUEST,
  SEARCH_USERS_SUCCESS,
  SEARCH_USERS_FAILURE,
  UPDATE_USER_PROFILE_REQUEST,
  UPDATE_USER_PROFILE_SUCCESS,
  UPDATE_USER_PROFILE_FAILURE,
  GET_ALL_USERS_REQUEST,
  GET_ALL_USERS_SUCCESS,
  GET_ALL_USERS_FAILURE,
} from "./ActionType";

// Get user by ID
export const getUserById = (userId) => async (dispatch) => {
  dispatch({ type: GET_USER_BY_ID_REQUEST });
  try {
    const res = await api.get(`/users/${userId}`);
    dispatch({
      type: GET_USER_BY_ID_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_USER_BY_ID_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Search users
export const searchUsers = (keyword) => async (dispatch) => {
  dispatch({ type: SEARCH_USERS_REQUEST });
  try {
    const res = await api.get(`/users/search?keyword=${encodeURIComponent(keyword)}`);
    dispatch({
      type: SEARCH_USERS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: SEARCH_USERS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Update user profile
export const updateUserProfile = (userData) => async (dispatch) => {
  dispatch({ type: UPDATE_USER_PROFILE_REQUEST });
  try {
    const res = await api.put("/users/profile", userData);
    dispatch({
      type: UPDATE_USER_PROFILE_SUCCESS,
      payload: res.data.data,
    });
    // Update localStorage
    localStorage.setItem("user", JSON.stringify(res.data.data));
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UPDATE_USER_PROFILE_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get all users
export const getAllUsers = () => async (dispatch) => {
  dispatch({ type: GET_ALL_USERS_REQUEST });
  try {
    const res = await api.get("/users");
    dispatch({
      type: GET_ALL_USERS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_ALL_USERS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
