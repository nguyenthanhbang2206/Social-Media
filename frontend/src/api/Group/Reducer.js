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
  GET_GROUP_POSTS_REQUEST,
  GET_GROUP_POSTS_SUCCESS,
  GET_GROUP_POSTS_FAILURE,
  GET_GROUP_PENDING_POSTS_REQUEST,
  GET_GROUP_PENDING_POSTS_SUCCESS,
  GET_GROUP_PENDING_POSTS_FAILURE,
  CREATE_GROUP_POST_REQUEST,
  CREATE_GROUP_POST_SUCCESS,
  CREATE_GROUP_POST_FAILURE,
  APPROVE_GROUP_POST_REQUEST,
  APPROVE_GROUP_POST_SUCCESS,
  APPROVE_GROUP_POST_FAILURE,
} from "./ActionType";

const initialState = {
  groups: [],
  myGroups: [],
  currentGroup: null,
  searchResults: [],
  groupPosts: [],
  pendingPosts: [],
  loading: false,
  error: null,
  actionLoading: false,
};

export default function groupReducer(state = initialState, action) {
  switch (action.type) {
    case CREATE_GROUP_REQUEST:
    case UPDATE_GROUP_REQUEST:
    case DELETE_GROUP_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case CREATE_GROUP_SUCCESS:
    case UPDATE_GROUP_SUCCESS:
    case DELETE_GROUP_SUCCESS:
      return { ...state, actionLoading: false };

    case CREATE_GROUP_FAILURE:
    case UPDATE_GROUP_FAILURE:
    case DELETE_GROUP_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_GROUPS_REQUEST:
    case SEARCH_GROUPS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_GROUPS_SUCCESS:
    case SEARCH_GROUPS_SUCCESS:
      return { ...state, loading: false, groups: action.payload };
    case GET_GROUPS_FAILURE:
    case SEARCH_GROUPS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_GROUP_BY_ID_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_GROUP_BY_ID_SUCCESS:
      return { ...state, loading: false, currentGroup: action.payload };
    case GET_GROUP_BY_ID_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_MY_GROUPS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_MY_GROUPS_SUCCESS:
      return { ...state, loading: false, myGroups: action.payload };
    case GET_MY_GROUPS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_GROUP_POSTS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_GROUP_POSTS_SUCCESS:
      return { ...state, loading: false, groupPosts: action.payload };
    case GET_GROUP_POSTS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_GROUP_PENDING_POSTS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_GROUP_PENDING_POSTS_SUCCESS:
      return { ...state, loading: false, pendingPosts: action.payload };
    case GET_GROUP_PENDING_POSTS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case CREATE_GROUP_POST_REQUEST:
    case APPROVE_GROUP_POST_REQUEST:
      return { ...state, actionLoading: true, error: null };
    case CREATE_GROUP_POST_SUCCESS:
    case APPROVE_GROUP_POST_SUCCESS:
      return { ...state, actionLoading: false };
    case CREATE_GROUP_POST_FAILURE:
    case APPROVE_GROUP_POST_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    default:
      return state;
  }
}
