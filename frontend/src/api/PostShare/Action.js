import api from "../../config/api";
import {
  SHARE_POST_REQUEST,
  SHARE_POST_SUCCESS,
  SHARE_POST_FAILURE,
  GET_POST_SHARES_REQUEST,
  GET_POST_SHARES_SUCCESS,
  GET_POST_SHARES_FAILURE,
  DELETE_POST_SHARE_REQUEST,
  DELETE_POST_SHARE_SUCCESS,
  DELETE_POST_SHARE_FAILURE,
} from "./ActionType";

// Share post
export const sharePost = (postId, shareContent) => async (dispatch) => {
  dispatch({ type: SHARE_POST_REQUEST });
  try {
    const res = await api.post(`/posts/${postId}/shares`, { shareContent });
    dispatch({
      type: SHARE_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: SHARE_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get post shares
export const getPostShares = (postId) => async (dispatch) => {
  dispatch({ type: GET_POST_SHARES_REQUEST });
  try {
    const res = await api.get(`/posts/${postId}/shares`);
    dispatch({
      type: GET_POST_SHARES_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_POST_SHARES_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Delete post share
export const deletePostShare = (postId, shareId) => async (dispatch) => {
  dispatch({ type: DELETE_POST_SHARE_REQUEST });
  try {
    await api.delete(`/posts/${postId}/shares/${shareId}`);
    dispatch({
      type: DELETE_POST_SHARE_SUCCESS,
      payload: shareId,
    });
  } catch (error) {
    dispatch({
      type: DELETE_POST_SHARE_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
