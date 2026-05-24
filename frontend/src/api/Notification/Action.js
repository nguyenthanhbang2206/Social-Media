import api from "../../config/api";
import {
  GET_NOTIFICATIONS_REQUEST,
  GET_NOTIFICATIONS_SUCCESS,
  GET_NOTIFICATIONS_FAILURE,
  GET_UNREAD_COUNT_REQUEST,
  GET_UNREAD_COUNT_SUCCESS,
  GET_UNREAD_COUNT_FAILURE,
  MARK_AS_READ_REQUEST,
  MARK_AS_READ_SUCCESS,
  MARK_AS_READ_FAILURE,
  MARK_ALL_AS_READ_REQUEST,
  MARK_ALL_AS_READ_SUCCESS,
  MARK_ALL_AS_READ_FAILURE,
  DELETE_NOTIFICATION_REQUEST,
  DELETE_NOTIFICATION_SUCCESS,
  DELETE_NOTIFICATION_FAILURE,
  RECEIVE_NOTIFICATION,
} from "./ActionType";

// Get paginated notifications
export const getNotifications =
  (page = 0, size = 20) =>
  async (dispatch) => {
    dispatch({ type: GET_NOTIFICATIONS_REQUEST });
    try {
      const res = await api.get(
        `/notifications?page=${page}&size=${size}`
      );
      dispatch({
        type: GET_NOTIFICATIONS_SUCCESS,
        payload: {
          content: res.data.data.content,
          page: page,
          totalPages: res.data.data.totalPages,
          totalElements: res.data.data.totalElements,
        },
      });
    } catch (error) {
      dispatch({
        type: GET_NOTIFICATIONS_FAILURE,
        payload: error.response?.data?.message || error.message,
      });
    }
  };

// Get unread notification count
export const getUnreadCount = () => async (dispatch) => {
  dispatch({ type: GET_UNREAD_COUNT_REQUEST });
  try {
    const res = await api.get("/notifications/unread-count");
    dispatch({
      type: GET_UNREAD_COUNT_SUCCESS,
      payload: res.data.data.unreadCount,
    });
  } catch (error) {
    dispatch({
      type: GET_UNREAD_COUNT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Mark a single notification as read
export const markAsRead = (notificationId) => async (dispatch) => {
  dispatch({ type: MARK_AS_READ_REQUEST });
  try {
    const res = await api.patch(`/notifications/${notificationId}/read`);
    dispatch({
      type: MARK_AS_READ_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: MARK_AS_READ_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Mark all notifications as read
export const markAllAsRead = () => async (dispatch) => {
  dispatch({ type: MARK_ALL_AS_READ_REQUEST });
  try {
    await api.patch("/notifications/read-all");
    dispatch({ type: MARK_ALL_AS_READ_SUCCESS });
  } catch (error) {
    dispatch({
      type: MARK_ALL_AS_READ_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Delete a notification
export const deleteNotification = (notificationId) => async (dispatch) => {
  dispatch({ type: DELETE_NOTIFICATION_REQUEST });
  try {
    await api.delete(`/notifications/${notificationId}`);
    dispatch({
      type: DELETE_NOTIFICATION_SUCCESS,
      payload: notificationId,
    });
  } catch (error) {
    dispatch({
      type: DELETE_NOTIFICATION_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Receive a real-time notification via WebSocket
export const receiveNotification = (notification) => ({
  type: RECEIVE_NOTIFICATION,
  payload: notification,
});
