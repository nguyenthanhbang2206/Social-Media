import {
  GET_ADMIN_USERS_REQUEST,
  GET_ADMIN_USERS_SUCCESS,
  GET_ADMIN_USERS_FAILURE,
  UPDATE_ADMIN_USER_REQUEST,
  UPDATE_ADMIN_USER_SUCCESS,
  UPDATE_ADMIN_USER_FAILURE,
} from "./ActionType";

const initialState = {
  users: [],
  loading: false,
  error: null,
  actionLoading: false,
};

export default function adminUserReducer(state = initialState, action) {
  switch (action.type) {
    case UPDATE_ADMIN_USER_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case UPDATE_ADMIN_USER_SUCCESS:
      return { ...state, actionLoading: false };

    case UPDATE_ADMIN_USER_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_ADMIN_USERS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_ADMIN_USERS_SUCCESS:
      return { ...state, loading: false, users: action.payload };
    case GET_ADMIN_USERS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
