import {
  SEND_FRIEND_REQUEST_REQUEST,
  SEND_FRIEND_REQUEST_SUCCESS,
  SEND_FRIEND_REQUEST_FAILURE,
  CANCEL_FRIEND_REQUEST_REQUEST,
  CANCEL_FRIEND_REQUEST_SUCCESS,
  CANCEL_FRIEND_REQUEST_FAILURE,
  ACCEPT_FRIEND_REQUEST_REQUEST,
  ACCEPT_FRIEND_REQUEST_SUCCESS,
  ACCEPT_FRIEND_REQUEST_FAILURE,
  REFUSE_FRIEND_REQUEST_REQUEST,
  REFUSE_FRIEND_REQUEST_SUCCESS,
  REFUSE_FRIEND_REQUEST_FAILURE,
  UNFRIEND_REQUEST,
  UNFRIEND_SUCCESS,
  UNFRIEND_FAILURE,
  GET_FRIENDS_REQUEST,
  GET_FRIENDS_SUCCESS,
  GET_FRIENDS_FAILURE,
  GET_FRIEND_REQUESTS_RECEIVED_REQUEST,
  GET_FRIEND_REQUESTS_RECEIVED_SUCCESS,
  GET_FRIEND_REQUESTS_RECEIVED_FAILURE,
  GET_FRIEND_STATUS_REQUEST,
  GET_FRIEND_STATUS_SUCCESS,
  GET_FRIEND_STATUS_FAILURE,
} from "./ActionType";

const initialState = {
  friends: [],
  friendRequests: [],
  friendStatus: null,
  friendsFetchedForUserId: null,
  friendsLastFetchedAt: null,
  friendRequestsLastFetchedAt: null,
  loading: false,
  error: null,
  actionLoading: false,
};

export default function friendshipReducer(state = initialState, action) {
  switch (action.type) {
    case SEND_FRIEND_REQUEST_REQUEST:
    case CANCEL_FRIEND_REQUEST_REQUEST:
    case ACCEPT_FRIEND_REQUEST_REQUEST:
    case REFUSE_FRIEND_REQUEST_REQUEST:
    case UNFRIEND_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case SEND_FRIEND_REQUEST_SUCCESS:
    case CANCEL_FRIEND_REQUEST_SUCCESS:
    case ACCEPT_FRIEND_REQUEST_SUCCESS:
    case REFUSE_FRIEND_REQUEST_SUCCESS:
    case UNFRIEND_SUCCESS:
      return { ...state, actionLoading: false };

    case SEND_FRIEND_REQUEST_FAILURE:
    case CANCEL_FRIEND_REQUEST_FAILURE:
    case ACCEPT_FRIEND_REQUEST_FAILURE:
    case REFUSE_FRIEND_REQUEST_FAILURE:
    case UNFRIEND_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_FRIENDS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_FRIENDS_SUCCESS:
      return {
        ...state,
        loading: false,
        friends: action.payload,
        friendsFetchedForUserId:
          action.meta?.userId ?? state.friendsFetchedForUserId,
        friendsLastFetchedAt: Date.now(),
      };
    case GET_FRIENDS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_FRIEND_REQUESTS_RECEIVED_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_FRIEND_REQUESTS_RECEIVED_SUCCESS:
      return {
        ...state,
        loading: false,
        friendRequests: action.payload,
        friendRequestsLastFetchedAt: Date.now(),
      };
    case GET_FRIEND_REQUESTS_RECEIVED_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_FRIEND_STATUS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_FRIEND_STATUS_SUCCESS:
      return { ...state, loading: false, friendStatus: action.payload };
    case GET_FRIEND_STATUS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
