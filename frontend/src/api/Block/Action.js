import api from "../../config/api";
import {
  BLOCK_USER_REQUEST,
  BLOCK_USER_SUCCESS,
  BLOCK_USER_FAILURE,
  UNBLOCK_USER_REQUEST,
  UNBLOCK_USER_SUCCESS,
  UNBLOCK_USER_FAILURE,
  GET_BLOCKED_USERS_REQUEST,
  GET_BLOCKED_USERS_SUCCESS,
  GET_BLOCKED_USERS_FAILURE,
  CHECK_BLOCK_EXISTS_REQUEST,
  CHECK_BLOCK_EXISTS_SUCCESS,
  CHECK_BLOCK_EXISTS_FAILURE,
} from "./ActionType";

const BLOCKED_USERS_CACHE_TTL_MS = 60 * 1000;

// Block user
export const blockUser = (userId, reason) => async (dispatch) => {
  dispatch({ type: BLOCK_USER_REQUEST });
  try {
    const res = await api.post(`/blocks/${userId}`, reason ? { reason } : {});
    dispatch({
      type: BLOCK_USER_SUCCESS,
      payload: res.data.data,
    });
    dispatch(getBlockedUsers({ force: true }));
    return res.data.data;
  } catch (error) {
    dispatch({
      type: BLOCK_USER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Unblock user
export const unblockUser = (userId) => async (dispatch) => {
  dispatch({ type: UNBLOCK_USER_REQUEST });
  try {
    await api.delete(`/blocks/${userId}`);
    dispatch({
      type: UNBLOCK_USER_SUCCESS,
      payload: userId,
    });
    dispatch(getBlockedUsers({ force: true }));
  } catch (error) {
    dispatch({
      type: UNBLOCK_USER_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get blocked users
export const getBlockedUsers =
  (options = {}) =>
  async (dispatch, getState) => {
    const { force = false } = options;
    const { block } = getState();
    const fetchedAt = block?.blockedUsersLastFetchedAt;
    const isFresh =
      fetchedAt && Date.now() - fetchedAt < BLOCKED_USERS_CACHE_TTL_MS;

    if (!force && isFresh) {
      return block.blockedUsers;
    }

    dispatch({ type: GET_BLOCKED_USERS_REQUEST });
    try {
      const res = await api.get("/blocks");
      dispatch({
        type: GET_BLOCKED_USERS_SUCCESS,
        payload: res.data.data,
      });
      return res.data.data;
    } catch (error) {
      dispatch({
        type: GET_BLOCKED_USERS_FAILURE,
        payload: error.response?.data?.message || error.message,
      });
      throw error;
    }
  };

// Check if block exists
export const checkBlockExists = (userId, targetId) => async (dispatch) => {
  dispatch({ type: CHECK_BLOCK_EXISTS_REQUEST });
  try {
    const res = await api.get(
      `/blocks/exists?userId=${userId}&targetId=${targetId}`,
    );
    dispatch({
      type: CHECK_BLOCK_EXISTS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: CHECK_BLOCK_EXISTS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
