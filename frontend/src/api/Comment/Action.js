import api from "../../config/api";
import {
  CREATE_COMMENT_REQUEST,
  CREATE_COMMENT_SUCCESS,
  CREATE_COMMENT_FAILURE,
  GET_COMMENTS_REQUEST,
  GET_COMMENTS_SUCCESS,
  GET_COMMENTS_FAILURE,
  UPDATE_COMMENT_REQUEST,
  UPDATE_COMMENT_SUCCESS,
  UPDATE_COMMENT_FAILURE,
  DELETE_COMMENT_REQUEST,
  DELETE_COMMENT_SUCCESS,
  DELETE_COMMENT_FAILURE,
  REPLY_COMMENT_REQUEST,
  REPLY_COMMENT_SUCCESS,
  REPLY_COMMENT_FAILURE,
  GET_COMMENT_REPLIES_REQUEST,
  GET_COMMENT_REPLIES_SUCCESS,
  GET_COMMENT_REPLIES_FAILURE,
} from "./ActionType";

// Create comment
export const createComment = (postId, content) => async (dispatch) => {
  dispatch({ type: CREATE_COMMENT_REQUEST });
  try {
    const res = await api.post(`/posts/${postId}/comments`, { content });
    dispatch({
      type: CREATE_COMMENT_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: CREATE_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get comments for a post
export const getComments = (postId) => async (dispatch) => {
  dispatch({ type: GET_COMMENTS_REQUEST });
  try {
    const res = await api.get(`/posts/${postId}/comments`);
    dispatch({
      type: GET_COMMENTS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_COMMENTS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Update comment
export const updateComment = (commentId, content) => async (dispatch) => {
  dispatch({ type: UPDATE_COMMENT_REQUEST });
  try {
    const res = await api.put(`/comments/${commentId}`, { content });
    dispatch({
      type: UPDATE_COMMENT_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UPDATE_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Delete comment
export const deleteComment = (commentId) => async (dispatch) => {
  dispatch({ type: DELETE_COMMENT_REQUEST });
  try {
    await api.delete(`/comments/${commentId}`);
    dispatch({
      type: DELETE_COMMENT_SUCCESS,
      payload: commentId,
    });
  } catch (error) {
    dispatch({
      type: DELETE_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Reply to comment
export const replyComment = (commentId, content) => async (dispatch) => {
  dispatch({ type: REPLY_COMMENT_REQUEST });
  try {
    const res = await api.post(`/comments/${commentId}/reply`, { content });
    dispatch({
      type: REPLY_COMMENT_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: REPLY_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get comment replies
export const getCommentReplies = (commentId) => async (dispatch) => {
  dispatch({ type: GET_COMMENT_REPLIES_REQUEST });
  try {
    const res = await api.get(`/comments/${commentId}/replies`);
    dispatch({
      type: GET_COMMENT_REPLIES_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_COMMENT_REPLIES_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
