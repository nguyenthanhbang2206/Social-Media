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

const initialState = {
  shares: [],
  loading: false,
  error: null,
  actionLoading: false,
};

export default function postShareReducer(state = initialState, action) {
  switch (action.type) {
    case SHARE_POST_REQUEST:
    case DELETE_POST_SHARE_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case SHARE_POST_SUCCESS:
    case DELETE_POST_SHARE_SUCCESS:
      return { ...state, actionLoading: false };

    case SHARE_POST_FAILURE:
    case DELETE_POST_SHARE_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_POST_SHARES_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_POST_SHARES_SUCCESS:
      return { ...state, loading: false, shares: action.payload };
    case GET_POST_SHARES_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
