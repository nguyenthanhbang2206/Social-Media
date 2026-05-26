import {
  GET_NOTIFICATIONS_REQUEST,
  GET_NOTIFICATIONS_SUCCESS,
  GET_NOTIFICATIONS_FAILURE,
  GET_UNREAD_COUNT_REQUEST,
  GET_UNREAD_COUNT_SUCCESS,
  GET_UNREAD_COUNT_FAILURE,
  MARK_AS_READ_REQUEST,
  MARK_AS_READ_SUCCESS,
  MARK_AS_READ_FAILURE,
  MARK_ALL_AS_READ_REQUEST,
  MARK_ALL_AS_READ_SUCCESS,
  MARK_ALL_AS_READ_FAILURE,
  DELETE_NOTIFICATION_REQUEST,
  DELETE_NOTIFICATION_SUCCESS,
  DELETE_NOTIFICATION_FAILURE,
  RECEIVE_NOTIFICATION,
  RESET_NOTIFICATIONS,
} from "./ActionType";

const initialState = {
  notifications: [],
  unreadCount: 0,
  loading: false,
  error: null,
  page: 0,
  totalPages: 0,
  totalElements: 0,
};

const notificationReducer = (state = initialState, action) => {
  switch (action.type) {
    // Loading states
    case GET_NOTIFICATIONS_REQUEST:
    case GET_UNREAD_COUNT_REQUEST:
    case MARK_AS_READ_REQUEST:
    case MARK_ALL_AS_READ_REQUEST:
    case DELETE_NOTIFICATION_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    // Get notifications success
    case GET_NOTIFICATIONS_SUCCESS: {
      const nextNotifications =
        action.payload.page === 0
          ? action.payload.content
          : [...state.notifications, ...action.payload.content];

      if (action.payload.page === 0 && state.notifications.length > 0) {
        const seen = new Set(nextNotifications.map((n) => n.id));
        state.notifications.forEach((n) => {
          if (!seen.has(n.id)) {
            nextNotifications.push(n);
          }
        });
      }

      return {
        ...state,
        loading: false,
        notifications: nextNotifications,
        page: action.payload.page,
        totalPages: action.payload.totalPages,
        totalElements: action.payload.totalElements,
      };
    }

    // Get unread count success
    case GET_UNREAD_COUNT_SUCCESS:
      return {
        ...state,
        loading: false,
        unreadCount: action.payload,
      };

    // Mark single notification as read
    case MARK_AS_READ_SUCCESS:
      return {
        ...state,
        loading: false,
        notifications: state.notifications.map((n) =>
          n.id === action.payload.id ? { ...n, isRead: true } : n
        ),
        unreadCount: Math.max(0, state.unreadCount - 1),
      };

    // Mark all as read
    case MARK_ALL_AS_READ_SUCCESS:
      return {
        ...state,
        loading: false,
        notifications: state.notifications.map((n) => ({
          ...n,
          isRead: true,
        })),
        unreadCount: 0,
      };

    // Delete notification
    case DELETE_NOTIFICATION_SUCCESS:
      const deleted = state.notifications.find(
        (n) => n.id === action.payload
      );
      return {
        ...state,
        loading: false,
        notifications: state.notifications.filter(
          (n) => n.id !== action.payload
        ),
        unreadCount:
          deleted && !deleted.isRead
            ? Math.max(0, state.unreadCount - 1)
            : state.unreadCount,
        totalElements: Math.max(0, state.totalElements - 1),
      };

    // Real-time notification from WebSocket
    case RECEIVE_NOTIFICATION:
      // Avoid duplicates
      if (state.notifications.some((n) => n.id === action.payload.id)) {
        return state;
      }
      return {
        ...state,
        notifications: [action.payload, ...state.notifications],
        unreadCount: state.unreadCount + 1,
        totalElements: state.totalElements + 1,
      };

    // Error states
    case GET_NOTIFICATIONS_FAILURE:
    case GET_UNREAD_COUNT_FAILURE:
    case MARK_AS_READ_FAILURE:
    case MARK_ALL_AS_READ_FAILURE:
    case DELETE_NOTIFICATION_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    // Reset
    case RESET_NOTIFICATIONS:
      return initialState;

    default:
      return state;
  }
};

export default notificationReducer;
