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

const initialState = {
  blockedUsers: [],
  blockExists: false,
  blockedUsersLastFetchedAt: null,
  loading: false,
  error: null,
  actionLoading: false,
};

export default function blockReducer(state = initialState, action) {
  switch (action.type) {
    case BLOCK_USER_REQUEST:
    case UNBLOCK_USER_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case BLOCK_USER_SUCCESS:
    case UNBLOCK_USER_SUCCESS:
      return { ...state, actionLoading: false };

    case BLOCK_USER_FAILURE:
    case UNBLOCK_USER_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_BLOCKED_USERS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_BLOCKED_USERS_SUCCESS:
      return {
        ...state,
        loading: false,
        blockedUsers: action.payload,
        blockedUsersLastFetchedAt: Date.now(),
      };
    case GET_BLOCKED_USERS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case CHECK_BLOCK_EXISTS_REQUEST:
      return { ...state, loading: true, error: null };
    case CHECK_BLOCK_EXISTS_SUCCESS:
      return { ...state, loading: false, blockExists: action.payload };
    case CHECK_BLOCK_EXISTS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
