import api from "../../config/api";
import {
  SEND_FRIEND_REQUEST_REQUEST,
  SEND_FRIEND_REQUEST_SUCCESS,
  SEND_FRIEND_REQUEST_FAILURE,
  CANCEL_FRIEND_REQUEST_REQUEST,
  CANCEL_FRIEND_REQUEST_SUCCESS,
  CANCEL_FRIEND_REQUEST_FAILURE,
  ACCEPT_FRIEND_REQUEST_REQUEST,
  ACCEPT_FRIEND_REQUEST_SUCCESS,
  ACCEPT_FRIEND_REQUEST_FAILURE,
  REFUSE_FRIEND_REQUEST_REQUEST,
  REFUSE_FRIEND_REQUEST_SUCCESS,
  REFUSE_FRIEND_REQUEST_FAILURE,
  UNFRIEND_REQUEST,
  UNFRIEND_SUCCESS,
  UNFRIEND_FAILURE,
  GET_FRIENDS_REQUEST,
  GET_FRIENDS_SUCCESS,
  GET_FRIENDS_FAILURE,
  GET_FRIEND_REQUESTS_RECEIVED_REQUEST,
  GET_FRIEND_REQUESTS_RECEIVED_SUCCESS,
  GET_FRIEND_REQUESTS_RECEIVED_FAILURE,
  GET_FRIEND_STATUS_REQUEST,
  GET_FRIEND_STATUS_SUCCESS,
  GET_FRIEND_STATUS_FAILURE,
} from "./ActionType";

const FRIEND_CACHE_TTL_MS = 60 * 1000;

// Send friend request
export const sendFriendRequest = (userId) => async (dispatch) => {
  dispatch({ type: SEND_FRIEND_REQUEST_REQUEST });
  try {
    const res = await api.post(`/friend-requests/${userId}`);
    dispatch({
      type: SEND_FRIEND_REQUEST_SUCCESS,
      payload: res.data.data,
    });
    dispatch(getFriendRequestsReceived({ force: true }));
    return res.data.data;
  } catch (error) {
    dispatch({
      type: SEND_FRIEND_REQUEST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Cancel friend request
export const cancelFriendRequest = (userId) => async (dispatch) => {
  dispatch({ type: CANCEL_FRIEND_REQUEST_REQUEST });
  try {
    await api.delete(`/friend-requests/${userId}`);
    dispatch({
      type: CANCEL_FRIEND_REQUEST_SUCCESS,
      payload: userId,
    });
    dispatch(getFriendRequestsReceived({ force: true }));
  } catch (error) {
    dispatch({
      type: CANCEL_FRIEND_REQUEST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Accept friend request
export const acceptFriendRequest = (userId) => async (dispatch) => {
  dispatch({ type: ACCEPT_FRIEND_REQUEST_REQUEST });
  try {
    const res = await api.put(`/friend-requests/${userId}/accept`);
    dispatch({
      type: ACCEPT_FRIEND_REQUEST_SUCCESS,
      payload: res.data.data,
    });
    dispatch(getFriendRequestsReceived({ force: true }));
    return res.data.data;
  } catch (error) {
    dispatch({
      type: ACCEPT_FRIEND_REQUEST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Refuse friend request
export const refuseFriendRequest = (userId) => async (dispatch) => {
  dispatch({ type: REFUSE_FRIEND_REQUEST_REQUEST });
  try {
    await api.put(`/friend-requests/${userId}/refuse`);
    dispatch({
      type: REFUSE_FRIEND_REQUEST_SUCCESS,
      payload: userId,
    });
    dispatch(getFriendRequestsReceived({ force: true }));
  } catch (error) {
    dispatch({
      type: REFUSE_FRIEND_REQUEST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Unfriend
export const unfriend = (userId) => async (dispatch) => {
  dispatch({ type: UNFRIEND_REQUEST });
  try {
    await api.delete(`/friends/${userId}`);
    dispatch({
      type: UNFRIEND_SUCCESS,
      payload: userId,
    });
  } catch (error) {
    dispatch({
      type: UNFRIEND_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get friends list
export const getFriends =
  (userId, options = {}) =>
  async (dispatch, getState) => {
    const { force = false } = options;
    const { friendship } = getState();
    const isSameUser =
      Number(friendship?.friendsFetchedForUserId) === Number(userId);
    const fetchedAt = friendship?.friendsLastFetchedAt;
    const isFresh = fetchedAt && Date.now() - fetchedAt < FRIEND_CACHE_TTL_MS;

    if (!force && isSameUser && isFresh) {
      return friendship.friends;
    }

    dispatch({ type: GET_FRIENDS_REQUEST });
    try {
      const res = await api.get(`/friends/${userId}`);
      dispatch({
        type: GET_FRIENDS_SUCCESS,
        payload: res.data.data,
        meta: { userId },
      });
      return res.data.data;
    } catch (error) {
      dispatch({
        type: GET_FRIENDS_FAILURE,
        payload: error.response?.data?.message || error.message,
      });
      throw error;
    }
  };

// Get received friend requests
export const getFriendRequestsReceived =
  (options = {}) =>
  async (dispatch, getState) => {
    const { force = false } = options;
    const { friendship } = getState();
    const fetchedAt = friendship?.friendRequestsLastFetchedAt;
    const isFresh = fetchedAt && Date.now() - fetchedAt < FRIEND_CACHE_TTL_MS;

    if (!force && isFresh) {
      return friendship.friendRequests;
    }

    dispatch({ type: GET_FRIEND_REQUESTS_RECEIVED_REQUEST });
    try {
      const res = await api.get("/friend-requests/received");
      dispatch({
        type: GET_FRIEND_REQUESTS_RECEIVED_SUCCESS,
        payload: res.data.data,
      });
      return res.data.data;
    } catch (error) {
      dispatch({
        type: GET_FRIEND_REQUESTS_RECEIVED_FAILURE,
        payload: error.response?.data?.message || error.message,
      });
      throw error;
    }
  };

// Get friend status
export const getFriendStatus = (userId) => async (dispatch) => {
  dispatch({ type: GET_FRIEND_STATUS_REQUEST });
  try {
    const res = await api.get(`/friends/status/${userId}`);
    dispatch({
      type: GET_FRIEND_STATUS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_FRIEND_STATUS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
