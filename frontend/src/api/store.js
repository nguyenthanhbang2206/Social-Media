import { applyMiddleware, combineReducers, legacy_createStore } from "redux";
import { thunk } from "redux-thunk";
import authReducer from "./Auth/Reducer";
import postReducer from "./Post/Reducer";
import notificationReducer from "./Notification/Reducer";
import userReducer from "./User/Reducer";
import friendshipReducer from "./FriendShip/Reducer";
import blockReducer from "./Block/Reducer";
import commentReducer from "./Comment/Reducer";
import commentLikeReducer from "./CommentLike/Reducer";
import postShareReducer from "./PostShare/Reducer";
import groupReducer from "./Group/Reducer";
import groupMemberReducer from "./GroupMember/Reducer";
import adminUserReducer from "./AdminUser/Reducer";

const rootReducer = combineReducers({
  auth: authReducer,
  post: postReducer,
  notification: notificationReducer,
  user: userReducer,
  friendship: friendshipReducer,
  block: blockReducer,
  comment: commentReducer,
  commentLike: commentLikeReducer,
  postShare: postShareReducer,
  group: groupReducer,
  groupMember: groupMemberReducer,
  adminUser: adminUserReducer,
});

export const store = legacy_createStore(rootReducer, applyMiddleware(thunk));