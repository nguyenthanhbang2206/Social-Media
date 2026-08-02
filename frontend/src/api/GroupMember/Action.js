import api from "../../config/api";
import {
  JOIN_GROUP_REQUEST,
  JOIN_GROUP_SUCCESS,
  JOIN_GROUP_FAILURE,
  LEAVE_GROUP_REQUEST,
  LEAVE_GROUP_SUCCESS,
  LEAVE_GROUP_FAILURE,
  GET_PENDING_MEMBERS_REQUEST,
  GET_PENDING_MEMBERS_SUCCESS,
  GET_PENDING_MEMBERS_FAILURE,
  APPROVE_MEMBER_REQUEST,
  APPROVE_MEMBER_SUCCESS,
  APPROVE_MEMBER_FAILURE,
  REJECT_MEMBER_REQUEST,
  REJECT_MEMBER_SUCCESS,
  REJECT_MEMBER_FAILURE,
  DELETE_MEMBER_REQUEST,
  DELETE_MEMBER_SUCCESS,
  DELETE_MEMBER_FAILURE,
  GET_GROUP_MEMBERS_REQUEST,
  GET_GROUP_MEMBERS_SUCCESS,
  GET_GROUP_MEMBERS_FAILURE,
  GET_MEMBERSHIP_STATUS_REQUEST,
  GET_MEMBERSHIP_STATUS_SUCCESS,
  GET_MEMBERSHIP_STATUS_FAILURE,
  CHECK_IS_ADMIN_REQUEST,
  CHECK_IS_ADMIN_SUCCESS,
  CHECK_IS_ADMIN_FAILURE,
  UPDATE_MEMBER_ROLE_REQUEST,
  UPDATE_MEMBER_ROLE_SUCCESS,
  UPDATE_MEMBER_ROLE_FAILURE,
} from "./ActionType";

// Join group
export const joinGroup = (groupId) => async (dispatch) => {
  dispatch({ type: JOIN_GROUP_REQUEST });
  try {
    const res = await api.post(`/groups/${groupId}/join`);
    dispatch({
      type: JOIN_GROUP_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message;
    console.error("Error joining group:", errorMessage);
    dispatch({
      type: JOIN_GROUP_FAILURE,
      payload: errorMessage,
    });
    throw error;
  }
};

// Leave group
export const leaveGroup = (groupId) => async (dispatch) => {
  dispatch({ type: LEAVE_GROUP_REQUEST });
  try {
    await api.post(`/groups/${groupId}/left`);
    dispatch({
      type: LEAVE_GROUP_SUCCESS,
      payload: groupId,
    });
  } catch (error) {
    dispatch({
      type: LEAVE_GROUP_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get pending members
export const getPendingMembers = (groupId) => async (dispatch) => {
  dispatch({ type: GET_PENDING_MEMBERS_REQUEST });
  try {
    const res = await api.post(`/groups/${groupId}/pending-members`);
    dispatch({
      type: GET_PENDING_MEMBERS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_PENDING_MEMBERS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Approve member
export const approveMember = (groupId, userId) => async (dispatch) => {
  dispatch({ type: APPROVE_MEMBER_REQUEST });
  try {
    const res = await api.post(`/groups/${groupId}/members/${userId}/approve`);
    dispatch({
      type: APPROVE_MEMBER_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: APPROVE_MEMBER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Reject member
export const rejectMember = (groupId, userId) => async (dispatch) => {
  dispatch({ type: REJECT_MEMBER_REQUEST });
  try {
    await api.post(`/groups/${groupId}/members/${userId}/reject`);
    dispatch({
      type: REJECT_MEMBER_SUCCESS,
      payload: userId,
    });
  } catch (error) {
    dispatch({
      type: REJECT_MEMBER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Delete member
export const deleteMember = (groupId, userId) => async (dispatch) => {
  dispatch({ type: DELETE_MEMBER_REQUEST });
  try {
    await api.delete(`/groups/${groupId}/members/${userId}`);
    dispatch({
      type: DELETE_MEMBER_SUCCESS,
      payload: userId,
    });
  } catch (error) {
    dispatch({
      type: DELETE_MEMBER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get group members
export const getGroupMembers = (groupId) => async (dispatch) => {
  dispatch({ type: GET_GROUP_MEMBERS_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}/members`);
    dispatch({
      type: GET_GROUP_MEMBERS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_GROUP_MEMBERS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get membership status
export const getMembershipStatus = (groupId) => async (dispatch) => {
  dispatch({ type: GET_MEMBERSHIP_STATUS_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}/members/status`);
    dispatch({
      type: GET_MEMBERSHIP_STATUS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    // If user is not a member, set status to null or appropriate default
    // Backend throws EntityNotFoundException when user is not a member
    if (error.response?.status === 500 || error.response?.status === 404) {
      dispatch({
        type: GET_MEMBERSHIP_STATUS_SUCCESS,
        payload: null, // User is not a member
      });
      return null;
    }
    dispatch({
      type: GET_MEMBERSHIP_STATUS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Check if admin
export const checkIsAdmin = (groupId) => async (dispatch) => {
  dispatch({ type: CHECK_IS_ADMIN_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}/members/me/is-admin`);
    dispatch({
      type: CHECK_IS_ADMIN_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    // If user is not a member or not admin, set to false
    if (error.response?.status === 500 || error.response?.status === 404) {
      dispatch({
        type: CHECK_IS_ADMIN_SUCCESS,
        payload: false, // User is not an admin
      });
      return false;
    }
    dispatch({
      type: CHECK_IS_ADMIN_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Update member role
export const updateMemberRole = (groupId, userId, role) => async (dispatch) => {
  dispatch({ type: UPDATE_MEMBER_ROLE_REQUEST });
  try {
    const res = await api.put(`/groups/${groupId}/members/${userId}/role`, { role });
    dispatch({
      type: UPDATE_MEMBER_ROLE_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UPDATE_MEMBER_ROLE_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
