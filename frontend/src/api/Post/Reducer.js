import {
  CREATE_POST_REQUEST,
  CREATE_POST_SUCCESS,
  CREATE_POST_FAILURE,
  UPDATE_POST_REQUEST,
  UPDATE_POST_SUCCESS,
  UPDATE_POST_FAILURE,
  GET_POSTS_REQUEST,
  GET_POSTS_SUCCESS,
  GET_POSTS_FAILURE,
  GET_POST_BY_USER_ID_REQUEST,
  GET_POST_BY_USER_ID_SUCCESS,
  GET_POST_BY_USER_ID_FAILURE,
  DELETE_POST_REQUEST,
  DELETE_POST_SUCCESS,
  DELETE_POST_FAILURE,
  GET_POST_BY_ID_REQUEST,
  GET_POST_BY_ID_SUCCESS,
  GET_POST_BY_ID_FAILURE,
  UPLOAD_FILES_REQUEST,
  UPLOAD_FILES_SUCCESS,
  UPLOAD_FILES_FAILURE,
  REACT_POST_REQUEST,
  REACT_POST_SUCCESS,
  REACT_POST_FAILURE,
  UNREACT_POST_REQUEST,
  UNREACT_POST_SUCCESS,
  UNREACT_POST_FAILURE,
  GET_REACT_POST_BY_ME_AND_POST_ID_REQUEST,
  GET_REACT_POST_BY_ME_AND_POST_ID_SUCCESS,
  GET_REACT_POST_BY_ME_AND_POST_ID_FAILURE,
  GET_REACTIONS_OF_POST_REQUEST,
  GET_REACTIONS_OF_POST_SUCCESS,
  GET_REACTIONS_OF_POST_FAILURE,
} from "./ActionType";

// Initial state
const initialState = {
  posts: [],
  loading: false,
  error: null,
  createSuccess: false,
  updateSuccess: false,
  deleteSuccess: false,
  postDetail: null,
  uploadLoading: false,
  uploadError: null,
  uploadedFiles: [],
  myReact: null, // user's reaction for current post
  reactLoading: false,
  reactError: null,
  reactionsOfPost: {}, // { [postId]: [PostLikeResponse] }
};

const postReducer = (state = initialState, action) => {
  switch (action.type) {
    // Get all posts
    case GET_POSTS_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
        createSuccess: false,
        updateSuccess: false,
        deleteSuccess: false,
      };
    case GET_POSTS_SUCCESS:
      return {
        ...state,
        loading: false,
        posts: action.payload,
        error: null,
      };
    case GET_POSTS_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    // Get posts by user id
    case GET_POST_BY_USER_ID_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case GET_POST_BY_USER_ID_SUCCESS:
      return {
        ...state,
        loading: false,
        posts: action.payload,
        error: null,
      };
    case GET_POST_BY_USER_ID_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    // Get post by id
    case GET_POST_BY_ID_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
        postDetail: null,
      };
    case GET_POST_BY_ID_SUCCESS:
      return {
        ...state,
        loading: false,
        postDetail: action.payload,
        error: null,
      };
    case GET_POST_BY_ID_FAILURE:
      return {
        ...state,
        loading: false,
        postDetail: null,
        error: action.payload,
      };

    // Create post
    case CREATE_POST_REQUEST:
      return {
        ...state,
        loading: true,
        createSuccess: false,
        error: null,
      };
    case CREATE_POST_SUCCESS:
      return {
        ...state,
        loading: false,
        posts: [action.payload, ...state.posts],
        createSuccess: true,
        error: null,
      };
    case CREATE_POST_FAILURE:
      return {
        ...state,
        loading: false,
        createSuccess: false,
        error: action.payload,
      };

    // Update post
    case UPDATE_POST_REQUEST:
      return {
        ...state,
        loading: true,
        updateSuccess: false,
        error: null,
      };
    case UPDATE_POST_SUCCESS:
      return {
        ...state,
        loading: false,
        posts: state.posts.map((post) =>
          post.id === action.payload.id ? action.payload : post
        ),
        updateSuccess: true,
        error: null,
      };
    case UPDATE_POST_FAILURE:
      return {
        ...state,
        loading: false,
        updateSuccess: false,
        error: action.payload,
      };

    // Delete post
    case DELETE_POST_REQUEST:
      return {
        ...state,
        loading: true,
        deleteSuccess: false,
        error: null,
      };
    case DELETE_POST_SUCCESS:
      return {
        ...state,
        loading: false,
        posts: state.posts.filter((post) => post.id !== action.payload),
        deleteSuccess: true,
        error: null,
      };
    case DELETE_POST_FAILURE:
      return {
        ...state,
        loading: false,
        deleteSuccess: false,
        error: action.payload,
      };

    // Upload files
    case UPLOAD_FILES_REQUEST:
      return {
        ...state,
        uploadLoading: true,
        uploadError: null,
        uploadedFiles: [],
      };
    case UPLOAD_FILES_SUCCESS:
      return {
        ...state,
        uploadLoading: false,
        uploadedFiles: action.payload,
        uploadError: null,
      };
    case UPLOAD_FILES_FAILURE:
      return {
        ...state,
        uploadLoading: false,
        uploadError: action.payload,
        uploadedFiles: [],
      };

    // React post
    case REACT_POST_REQUEST:
      return {
        ...state,
        reactLoading: true,
        reactError: null,
      };
    case REACT_POST_SUCCESS:
      return {
        ...state,
        reactLoading: false,
        reactError: null,
        myReact: action.payload, // PostLikeResponse
      };
    case REACT_POST_FAILURE:
      return {
        ...state,
        reactLoading: false,
        reactError: action.payload,
      };

    // Unreact post
    case UNREACT_POST_REQUEST:
      return {
        ...state,
        reactLoading: true,
        reactError: null,
      };
    case UNREACT_POST_SUCCESS:
      return {
        ...state,
        reactLoading: false,
        reactError: null,
        myReact: null,
      };
    case UNREACT_POST_FAILURE:
      return {
        ...state,
        reactLoading: false,
        reactError: action.payload,
      };

    // Get react post by me and post id
    case GET_REACT_POST_BY_ME_AND_POST_ID_REQUEST:
      return {
        ...state,
        reactLoading: true,
        reactError: null,
        myReact: null,
      };
    case GET_REACT_POST_BY_ME_AND_POST_ID_SUCCESS:
      return {
        ...state,
        reactLoading: false,
        reactError: null,
        myReact: action.payload, // PostLikeResponse
      };
    case GET_REACT_POST_BY_ME_AND_POST_ID_FAILURE:
      return {
        ...state,
        reactLoading: false,
        reactError: action.payload,
        myReact: null,
      };

    // Get all reactions of a post
    case GET_REACTIONS_OF_POST_REQUEST:
      return {
        ...state,
        reactLoading: true,
        reactError: null,
      };
    case GET_REACTIONS_OF_POST_SUCCESS:
      return {
        ...state,
        reactLoading: false,
        reactError: null,
        reactionsOfPost: {
          ...state.reactionsOfPost,
          [action.postId]: action.payload, // List<PostLikeResponse>
        },
      };
    case GET_REACTIONS_OF_POST_FAILURE:
      return {
        ...state,
        reactLoading: false,
        reactError: action.payload,
      };

    default:
      return state;
  }
};

export default postReducer;
