import api from "../../config/api";
import {
  REACT_COMMENT_REQUEST,
  REACT_COMMENT_SUCCESS,
  REACT_COMMENT_FAILURE,
  UNREACT_COMMENT_REQUEST,
  UNREACT_COMMENT_SUCCESS,
  UNREACT_COMMENT_FAILURE,
  GET_COMMENT_REACTIONS_REQUEST,
  GET_COMMENT_REACTIONS_SUCCESS,
  GET_COMMENT_REACTIONS_FAILURE,
  GET_MY_COMMENT_REACTION_REQUEST,
  GET_MY_COMMENT_REACTION_SUCCESS,
  GET_MY_COMMENT_REACTION_FAILURE,
} from "./ActionType";

// React to comment
export const reactComment = (commentId, reactionType) => async (dispatch) => {
  dispatch({ type: REACT_COMMENT_REQUEST });
  try {
    const res = await api.post(`/comments/${commentId}/react`, { reactionType });
    dispatch({
      type: REACT_COMMENT_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: REACT_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Unreact comment
export const unreactComment = (commentId) => async (dispatch) => {
  dispatch({ type: UNREACT_COMMENT_REQUEST });
  try {
    await api.delete(`/comments/${commentId}/un-react`);
    dispatch({
      type: UNREACT_COMMENT_SUCCESS,
      payload: commentId,
    });
  } catch (error) {
    dispatch({
      type: UNREACT_COMMENT_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get comment reactions
export const getCommentReactions = (commentId) => async (dispatch) => {
  dispatch({ type: GET_COMMENT_REACTIONS_REQUEST });
  try {
    const res = await api.get(`/comments/${commentId}/reactions`);
    dispatch({
      type: GET_COMMENT_REACTIONS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_COMMENT_REACTIONS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get my reaction to comment
export const getMyCommentReaction = (commentId) => async (dispatch) => {
  dispatch({ type: GET_MY_COMMENT_REACTION_REQUEST });
  try {
    const res = await api.get(`/comments/${commentId}/me`);
    dispatch({
      type: GET_MY_COMMENT_REACTION_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_MY_COMMENT_REACTION_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
