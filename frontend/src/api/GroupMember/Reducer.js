import {
  JOIN_GROUP_REQUEST,
  JOIN_GROUP_SUCCESS,
  JOIN_GROUP_FAILURE,
  LEAVE_GROUP_REQUEST,
  LEAVE_GROUP_SUCCESS,
  LEAVE_GROUP_FAILURE,
  GET_PENDING_MEMBERS_REQUEST,
  GET_PENDING_MEMBERS_SUCCESS,
  GET_PENDING_MEMBERS_FAILURE,
  APPROVE_MEMBER_REQUEST,
  APPROVE_MEMBER_SUCCESS,
  APPROVE_MEMBER_FAILURE,
  REJECT_MEMBER_REQUEST,
  REJECT_MEMBER_SUCCESS,
  REJECT_MEMBER_FAILURE,
  DELETE_MEMBER_REQUEST,
  DELETE_MEMBER_SUCCESS,
  DELETE_MEMBER_FAILURE,
  GET_GROUP_MEMBERS_REQUEST,
  GET_GROUP_MEMBERS_SUCCESS,
  GET_GROUP_MEMBERS_FAILURE,
  GET_MEMBERSHIP_STATUS_REQUEST,
  GET_MEMBERSHIP_STATUS_SUCCESS,
  GET_MEMBERSHIP_STATUS_FAILURE,
  CHECK_IS_ADMIN_REQUEST,
  CHECK_IS_ADMIN_SUCCESS,
  CHECK_IS_ADMIN_FAILURE,
  UPDATE_MEMBER_ROLE_REQUEST,
  UPDATE_MEMBER_ROLE_SUCCESS,
  UPDATE_MEMBER_ROLE_FAILURE,
} from "./ActionType";

const initialState = {
  members: [],
  pendingMembers: [],
  membershipStatus: null,
  isAdmin: false,
  loading: false,
  error: null,
  actionLoading: false,
};

export default function groupMemberReducer(state = initialState, action) {
  switch (action.type) {
    case JOIN_GROUP_REQUEST:
    case LEAVE_GROUP_REQUEST:
    case APPROVE_MEMBER_REQUEST:
    case REJECT_MEMBER_REQUEST:
    case DELETE_MEMBER_REQUEST:
    case UPDATE_MEMBER_ROLE_REQUEST:
      return { ...state, actionLoading: true, error: null };

    case JOIN_GROUP_SUCCESS:
    case LEAVE_GROUP_SUCCESS:
    case APPROVE_MEMBER_SUCCESS:
    case REJECT_MEMBER_SUCCESS:
    case DELETE_MEMBER_SUCCESS:
    case UPDATE_MEMBER_ROLE_SUCCESS:
      return { ...state, actionLoading: false };

    case JOIN_GROUP_FAILURE:
    case LEAVE_GROUP_FAILURE:
    case APPROVE_MEMBER_FAILURE:
    case REJECT_MEMBER_FAILURE:
    case DELETE_MEMBER_FAILURE:
    case UPDATE_MEMBER_ROLE_FAILURE:
      return { ...state, actionLoading: false, error: action.payload };

    case GET_GROUP_MEMBERS_REQUEST:
    case GET_PENDING_MEMBERS_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_GROUP_MEMBERS_SUCCESS:
      return { ...state, loading: false, members: action.payload };
    case GET_PENDING_MEMBERS_SUCCESS:
      return { ...state, loading: false, pendingMembers: action.payload };
    case GET_GROUP_MEMBERS_FAILURE:
    case GET_PENDING_MEMBERS_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case GET_MEMBERSHIP_STATUS_REQUEST:
    case CHECK_IS_ADMIN_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_MEMBERSHIP_STATUS_SUCCESS:
      return { ...state, loading: false, membershipStatus: action.payload };
    case CHECK_IS_ADMIN_SUCCESS:
      return { ...state, loading: false, isAdmin: action.payload };
    case GET_MEMBERSHIP_STATUS_FAILURE:
    case CHECK_IS_ADMIN_FAILURE:
      return { ...state, loading: false, error: action.payload };

    default:
      return state;
  }
}
