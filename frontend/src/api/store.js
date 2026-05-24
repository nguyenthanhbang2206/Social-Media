import { applyMiddleware, combineReducers, legacy_createStore } from "redux";
import { thunk } from "redux-thunk";
import authReducer from "./Auth/Reducer";
import postReducer from "./Post/Reducer";
import notificationReducer from "./Notification/Reducer";
const rootReducer = combineReducers({
  auth: authReducer,
  post: postReducer,
  notification: notificationReducer,
});
export const store = legacy_createStore(rootReducer, applyMiddleware(thunk));