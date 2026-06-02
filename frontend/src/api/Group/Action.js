import api from "../../config/api";
import {
  CREATE_GROUP_REQUEST,
  CREATE_GROUP_SUCCESS,
  CREATE_GROUP_FAILURE,
  GET_GROUPS_REQUEST,
  GET_GROUPS_SUCCESS,
  GET_GROUPS_FAILURE,
  GET_GROUP_BY_ID_REQUEST,
  GET_GROUP_BY_ID_SUCCESS,
  GET_GROUP_BY_ID_FAILURE,
  UPDATE_GROUP_REQUEST,
  UPDATE_GROUP_SUCCESS,
  UPDATE_GROUP_FAILURE,
  DELETE_GROUP_REQUEST,
  DELETE_GROUP_SUCCESS,
  DELETE_GROUP_FAILURE,
  SEARCH_GROUPS_REQUEST,
  SEARCH_GROUPS_SUCCESS,
  SEARCH_GROUPS_FAILURE,
  GET_MY_GROUPS_REQUEST,
  GET_MY_GROUPS_SUCCESS,
  GET_MY_GROUPS_FAILURE,
} from "./ActionType";

// Create group
export const createGroup = (groupData) => async (dispatch) => {
  dispatch({ type: CREATE_GROUP_REQUEST });
  try {
    const res = await api.post("/groups", groupData);
    dispatch({
      type: CREATE_GROUP_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: CREATE_GROUP_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get all groups
export const getGroups = () => async (dispatch) => {
  dispatch({ type: GET_GROUPS_REQUEST });
  try {
    const res = await api.get("/groups");
    dispatch({
      type: GET_GROUPS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_GROUPS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get group by ID
export const getGroupById = (groupId) => async (dispatch) => {
  dispatch({ type: GET_GROUP_BY_ID_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}`);
    dispatch({
      type: GET_GROUP_BY_ID_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_GROUP_BY_ID_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Update group
export const updateGroup = (groupId, groupData) => async (dispatch) => {
  dispatch({ type: UPDATE_GROUP_REQUEST });
  try {
    const res = await api.put(`/groups/${groupId}`, groupData);
    dispatch({
      type: UPDATE_GROUP_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UPDATE_GROUP_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Delete group
export const deleteGroup = (groupId) => async (dispatch) => {
  dispatch({ type: DELETE_GROUP_REQUEST });
  try {
    await api.delete(`/groups/${groupId}`);
    dispatch({
      type: DELETE_GROUP_SUCCESS,
      payload: groupId,
    });
  } catch (error) {
    dispatch({
      type: DELETE_GROUP_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Search groups
export const searchGroups = (keyword) => async (dispatch) => {
  dispatch({ type: SEARCH_GROUPS_REQUEST });
  try {
    const res = await api.get(`/groups/search?keyword=${encodeURIComponent(keyword)}`);
    dispatch({
      type: SEARCH_GROUPS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: SEARCH_GROUPS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get my groups
export const getMyGroups = () => async (dispatch) => {
  dispatch({ type: GET_MY_GROUPS_REQUEST });
  try {
    const res = await api.get("/groups/my-group");
    dispatch({
      type: GET_MY_GROUPS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_MY_GROUPS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
