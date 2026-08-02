import api from "../../config/api";
import {
  CREATE_POST_REQUEST,
  CREATE_POST_SUCCESS,
  CREATE_POST_FAILURE,
  UPDATE_POST_REQUEST,
  UPDATE_POST_SUCCESS,
  UPDATE_POST_FAILURE,
  GET_POSTS_REQUEST,
  GET_POSTS_SUCCESS,
  GET_POSTS_FAILURE,
  GET_POST_BY_USER_ID_REQUEST,
  GET_POST_BY_USER_ID_SUCCESS,
  GET_POST_BY_USER_ID_FAILURE,
  DELETE_POST_REQUEST,
  DELETE_POST_SUCCESS,
  DELETE_POST_FAILURE,
  GET_POST_BY_ID_REQUEST,
  GET_POST_BY_ID_SUCCESS,
  GET_POST_BY_ID_FAILURE,
  UPLOAD_FILES_REQUEST,
  UPLOAD_FILES_SUCCESS,
  UPLOAD_FILES_FAILURE,
  REACT_POST_REQUEST,
  REACT_POST_SUCCESS,
  REACT_POST_FAILURE,
  UNREACT_POST_REQUEST,
  UNREACT_POST_SUCCESS,
  UNREACT_POST_FAILURE,
  GET_REACT_POST_BY_ME_AND_POST_ID_REQUEST,
  GET_REACT_POST_BY_ME_AND_POST_ID_SUCCESS,
  GET_REACT_POST_BY_ME_AND_POST_ID_FAILURE,
  GET_REACTIONS_OF_POST_REQUEST,
  GET_REACTIONS_OF_POST_SUCCESS,
  GET_REACTIONS_OF_POST_FAILURE,
  CREATE_GROUP_POST_REQUEST,
  CREATE_GROUP_POST_SUCCESS,
  CREATE_GROUP_POST_FAILURE,
  GET_GROUP_POSTS_REQUEST,
  GET_GROUP_POSTS_SUCCESS,
  GET_GROUP_POSTS_FAILURE,
  GET_GROUP_PENDING_POSTS_REQUEST,
  GET_GROUP_PENDING_POSTS_SUCCESS,
  GET_GROUP_PENDING_POSTS_FAILURE,
  APPROVE_GROUP_POST_REQUEST,
  APPROVE_GROUP_POST_SUCCESS,
  APPROVE_GROUP_POST_FAILURE,
  PIN_GROUP_POST_REQUEST,
  PIN_GROUP_POST_SUCCESS,
  PIN_GROUP_POST_FAILURE,
  UNPIN_GROUP_POST_REQUEST,
  UNPIN_GROUP_POST_SUCCESS,
  UNPIN_GROUP_POST_FAILURE,
} from "./ActionType";

const USER_POSTS_FAILURE_COOLDOWN_MS = 30 * 1000;
const userPostsFailureByUserId = new Map();

const getUserPostsFromFeed = async (userId) => {
  const res = await api.get("/posts");
  const normalizedUserId = Number(userId);
  const feedPosts = Array.isArray(res.data?.data) ? res.data.data : [];
  return feedPosts.filter((post) => Number(post?.userId) === normalizedUserId);
};

// Get all posts
export const getPosts = () => async (dispatch) => {
  dispatch({ type: GET_POSTS_REQUEST });
  try {
    const res = await api.get("/posts");
    dispatch({
      type: GET_POSTS_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: GET_POSTS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Get posts by user id
export const getPostsByUser = (userId) => async (dispatch) => {
  dispatch({ type: GET_POST_BY_USER_ID_REQUEST });
  const normalizedUserId = Number(userId);

  if (!normalizedUserId) {
    dispatch({ type: GET_POST_BY_USER_ID_SUCCESS, payload: [] });
    return [];
  }

  const lastFailureAt = userPostsFailureByUserId.get(normalizedUserId);
  const inCooldown =
    lastFailureAt &&
    Date.now() - lastFailureAt < USER_POSTS_FAILURE_COOLDOWN_MS;

  if (inCooldown) {
    try {
      const fallbackPosts = await getUserPostsFromFeed(normalizedUserId);
      dispatch({
        type: GET_POST_BY_USER_ID_SUCCESS,
        payload: fallbackPosts,
      });
      return fallbackPosts;
    } catch (fallbackError) {
      dispatch({
        type: GET_POST_BY_USER_ID_FAILURE,
        payload: fallbackError.response?.data?.message || fallbackError.message,
      });
      return [];
    }
  }

  try {
    const res = await api.get(`/users/${normalizedUserId}/posts`);
    userPostsFailureByUserId.delete(normalizedUserId);
    dispatch({
      type: GET_POST_BY_USER_ID_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    userPostsFailureByUserId.set(normalizedUserId, Date.now());
    try {
      const fallbackPosts = await getUserPostsFromFeed(normalizedUserId);
      dispatch({
        type: GET_POST_BY_USER_ID_SUCCESS,
        payload: fallbackPosts,
      });
      return fallbackPosts;
    } catch (fallbackError) {
      dispatch({
        type: GET_POST_BY_USER_ID_FAILURE,
        payload:
          fallbackError.response?.data?.message ||
          error.response?.data?.message ||
          error.message,
      });
      return [];
    }
  }
};

// Get post by id
export const getPostById = (id) => async (dispatch) => {
  dispatch({ type: GET_POST_BY_ID_REQUEST });
  try {
    const res = await api.get(`/posts/${id}`);
    dispatch({
      type: GET_POST_BY_ID_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: GET_POST_BY_ID_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Create post
export const createPost = (postData) => async (dispatch) => {
  dispatch({ type: CREATE_POST_REQUEST });
  try {
    const res = await api.post("/posts", postData);
    dispatch({
      type: CREATE_POST_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: CREATE_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Update post
export const updatePost = (id, postData) => async (dispatch) => {
  dispatch({ type: UPDATE_POST_REQUEST });
  try {
    const res = await api.put(`/posts/${id}`, postData);
    dispatch({
      type: UPDATE_POST_SUCCESS,
      payload: res.data.data,
    });
  } catch (error) {
    dispatch({
      type: UPDATE_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};

// Delete post
export const deletePost = (id) => async (dispatch) => {
  dispatch({ type: DELETE_POST_REQUEST });
  try {
    await api.delete(`/posts/${id}`);
    dispatch({
      type: DELETE_POST_SUCCESS,
      payload: id,
    });
  } catch (error) {
    dispatch({
      type: DELETE_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};
export const uploadFiles = (files, folder) => async (dispatch) => {
  dispatch({ type: UPLOAD_FILES_REQUEST });
  try {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }
    formData.append("folder", folder);

    const res = await api.post("/files", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    dispatch({
      type: UPLOAD_FILES_SUCCESS,
      payload: res.data.data, // List<FileResponse>
    });
  } catch (error) {
    dispatch({
      type: UPLOAD_FILES_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};
// React to a post
export const reactPost = (postId, reactionType) => async (dispatch) => {
  dispatch({ type: REACT_POST_REQUEST });
  try {
    const res = await api.post(`/posts/${postId}/react`, { reactionType });
    dispatch({
      type: REACT_POST_SUCCESS,
      payload: res.data.data, // PostLikeResponse
    });
  } catch (error) {
    dispatch({
      type: REACT_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};
// Unreact a post
export const unreactPost = (postId) => async (dispatch) => {
  dispatch({ type: UNREACT_POST_REQUEST });
  try {
    await api.delete(`/posts/${postId}/un-react`);
    dispatch({
      type: UNREACT_POST_SUCCESS,
      payload: postId,
    });
  } catch (error) {
    dispatch({
      type: UNREACT_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
  }
};
export const getReactionsOfPost = (postId) => async (dispatch) => {
  dispatch({ type: GET_REACTIONS_OF_POST_REQUEST });
  try {
    const res = await api.get(`/posts/${postId}/reactions`);
    dispatch({
      type: GET_REACTIONS_OF_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data; // ✅ Thêm dòng này
  } catch (error) {
    dispatch({
      type: GET_REACTIONS_OF_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error; // ✅ Cũng nên throw nếu muốn xử lý trong component
  }
};
export const getReactPostByMeAndPostId = (postId) => async (dispatch) => {
  dispatch({ type: GET_REACT_POST_BY_ME_AND_POST_ID_REQUEST });
  try {
    const res = await api.get(`/posts/${postId}/me`);
    dispatch({
      type: GET_REACT_POST_BY_ME_AND_POST_ID_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data; // ✅ Trả về dữ liệu
  } catch (error) {
    dispatch({
      type: GET_REACT_POST_BY_ME_AND_POST_ID_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error; // ✅ Cho phép bắt lỗi ở component
  }
};

// Create group post
export const createGroupPost = (groupId, postData) => async (dispatch) => {
  dispatch({ type: CREATE_GROUP_POST_REQUEST });
  try {
    const res = await api.post(`/groups/${groupId}/posts`, postData);
    dispatch({
      type: CREATE_GROUP_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: CREATE_GROUP_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get group posts
export const getGroupPosts = (groupId) => async (dispatch) => {
  dispatch({ type: GET_GROUP_POSTS_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}/posts`);
    dispatch({
      type: GET_GROUP_POSTS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_GROUP_POSTS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Get group pending posts
export const getGroupPendingPosts = (groupId) => async (dispatch) => {
  dispatch({ type: GET_GROUP_PENDING_POSTS_REQUEST });
  try {
    const res = await api.get(`/groups/${groupId}/posts/pending`);
    dispatch({
      type: GET_GROUP_PENDING_POSTS_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: GET_GROUP_PENDING_POSTS_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Approve group post
export const approveGroupPost = (groupId, postId) => async (dispatch) => {
  dispatch({ type: APPROVE_GROUP_POST_REQUEST });
  try {
    const res = await api.put(`/groups/${groupId}/posts/${postId}/approve`);
    dispatch({
      type: APPROVE_GROUP_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: APPROVE_GROUP_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Pin group post
export const pinGroupPost = (groupId, postId) => async (dispatch) => {
  dispatch({ type: PIN_GROUP_POST_REQUEST });
  try {
    const res = await api.put(`/groups/${groupId}/posts/${postId}/pin`);
    dispatch({
      type: PIN_GROUP_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: PIN_GROUP_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};

// Unpin group post
export const unpinGroupPost = (groupId, postId) => async (dispatch) => {
  dispatch({ type: UNPIN_GROUP_POST_REQUEST });
  try {
    const res = await api.put(`/groups/${groupId}/posts/${postId}/unpin`);
    dispatch({
      type: UNPIN_GROUP_POST_SUCCESS,
      payload: res.data.data,
    });
    return res.data.data;
  } catch (error) {
    dispatch({
      type: UNPIN_GROUP_POST_FAILURE,
      payload: error.response?.data?.message || error.message,
    });
    throw error;
  }
};
