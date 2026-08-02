import {
  GET_USER_BY_ID_REQUEST,
  GET_USER_BY_ID_SUCCESS,
  GET_USER_BY_ID_FAILURE,
  SEARCH_USERS_REQUEST,
  SEARCH_USERS_SUCCESS,
  SEARCH_USERS_FAILURE,
  UPDATE_USER_PROFILE_REQUEST,
  UPDATE_USER_PROFILE_SUCCESS,
  UPDATE_USER_PROFILE_FAILURE,
  GET_ALL_USERS_REQUEST,
  GET_ALL_USERS_SUCCESS,
  GET_ALL_USERS_FAILURE,
} from "./ActionType";

const initialState = {
  user: null,
  searchResults: [],
  allUsers: [],
  loading: false,
  error: null,
  updateLoading: false,
};

export default function userReducer(state = initialState, action) {
  switch (action.type) {
    case GET_USER_BY_ID_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_USER_BY_ID_SUCCESS:
      return { ...state, loading: false, user: action.payload };
    case GET_USER_BY_ID_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case SEARCH_USERS_REQUEST:
      return { ...state, loading: true, error: null };
    case SEARCH_USERS_SUCCESS:
      return { ...state, loading: false, searchResults: action.payload };
    case SEARCH_USERS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case UPDATE_USER_PROFILE_REQUEST:
      return { ...state, updateLoading: true, error: null };
    case UPDATE_USER_PROFILE_SUCCESS:
      return { ...state, updateLoading: false };
    case UPDATE_USER_PROFILE_FAILURE:
      return { ...state, updateLoading: false, error: action.payload };

    case GET_ALL_USERS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_ALL_USERS_SUCCESS:
      return { ...state, loading: false, allUsers: action.payload };
    case GET_ALL_USERS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
