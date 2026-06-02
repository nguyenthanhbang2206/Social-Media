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

const initialState = {
  reactions: [],
  myReaction: null,
  loading: false,
  error: null,
  actionLoading: false,
};

export default function commentLikeReducer(state = initialState, action) {
  switch (action.type) {
    case REACT_COMMENT_REQUEST:
    case UNREACT_COMMENT_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case REACT_COMMENT_SUCCESS:
    case UNREACT_COMMENT_SUCCESS:
      return { ...state, actionLoading: false };

    case REACT_COMMENT_FAILURE:
    case UNREACT_COMMENT_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_COMMENT_REACTIONS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_COMMENT_REACTIONS_SUCCESS:
      return { ...state, loading: false, reactions: action.payload };
    case GET_COMMENT_REACTIONS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_MY_COMMENT_REACTION_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_MY_COMMENT_REACTION_SUCCESS:
      return { ...state, loading: false, myReaction: action.payload };
    case GET_MY_COMMENT_REACTION_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
