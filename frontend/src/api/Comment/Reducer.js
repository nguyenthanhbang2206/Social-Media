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

const initialState = {
  comments: [],
  replies: [],
  loading: false,
  error: null,
  actionLoading: false,
};

export default function commentReducer(state = initialState, action) {
  switch (action.type) {
    case CREATE_COMMENT_REQUEST:
    case UPDATE_COMMENT_REQUEST:
    case DELETE_COMMENT_REQUEST:
    case REPLY_COMMENT_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case CREATE_COMMENT_SUCCESS:
    case UPDATE_COMMENT_SUCCESS:
    case DELETE_COMMENT_SUCCESS:
    case REPLY_COMMENT_SUCCESS:
      return { ...state, actionLoading: false };

    case CREATE_COMMENT_FAILURE:
    case UPDATE_COMMENT_FAILURE:
    case DELETE_COMMENT_FAILURE:
    case REPLY_COMMENT_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_COMMENTS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_COMMENTS_SUCCESS:
      return { ...state, loading: false, comments: action.payload };
    case GET_COMMENTS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_COMMENT_REPLIES_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_COMMENT_REPLIES_SUCCESS:
      return { ...state, loading: false, replies: action.payload };
    case GET_COMMENT_REPLIES_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
