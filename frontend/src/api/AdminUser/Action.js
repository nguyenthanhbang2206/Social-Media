import api from "../../config/api";
import {
  GET_ADMIN_USERS_REQUEST,
  GET_ADMIN_USERS_SUCCESS,
  GET_ADMIN_USERS_FAILURE,
  UPDATE_ADMIN_USER_REQUEST,
  UPDATE_ADMIN_USER_SUCCESS,
  UPDATE_ADMIN_USER_FAILURE,
} from "./ActionType";

// Get all users (admin)
export const getAdminUsers = (active) => async (dispatch) => {
  dispatch({ type: GET_ADMIN_USERS_REQUEST });
  try {
    const res = await api.get(`/admin/users?active=${active}`);
    dispatch({
      type: GET_ADMIN_USERS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_ADMIN_USERS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Update user (admin)
export const updateAdminUser = (userId, userData) => async (dispatch) => {
  dispatch({ type: UPDATE_ADMIN_USER_REQUEST });
  try {
    const res = await api.put(`/admin/users/${userId}`, userData);
    dispatch({
      type: UPDATE_ADMIN_USER_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UPDATE_ADMIN_USER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
