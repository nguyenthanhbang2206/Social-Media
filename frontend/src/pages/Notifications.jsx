import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  getNotifications,
  markAsRead,
  deleteNotification,
} from "../api/Notification/Action";
import AppLayout from "../components/layout/AppLayout";

export default function Notifications() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { notifications, loading } = useSelector((state) => state.notification);

  useEffect(() => {
    dispatch(getNotifications({ page: 0, size: 20 }));
  }, [dispatch]);

  const handleMarkAsRead = (id) => {
    dispatch(markAsRead(id));
  };

  const handleDelete = (id) => {
    dispatch(deleteNotification(id));
  };

  const handleNotificationClick = (notification) => {
    // Mark as read when clicked
    if (!notification.read) {
      dispatch(markAsRead(notification.id));
    }

    // Navigate based on notification type
    if (notification.type === "FRIEND_REQUEST") {
      navigate("/friend-requests");
    } else if (notification.type === "POST_REACTION" || notification.type === "COMMENT") {
      if (notification.referenceId) {
        navigate(`/posts/${notification.referenceId}`);
      }
    } else if (notification.type === "GROUP_INVITE") {
      if (notification.referenceId) {
        navigate(`/groups/${notification.referenceId}`);
      }
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case "FRIEND_REQUEST":
        return "👋";
      case "POST_REACTION":
        return "❤️";
      case "COMMENT":
        return "💬";
      case "GROUP_INVITE":
        return "👥";
      default:
        return "🔔";
    }
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto">
        <h2 className="text-2xl font-bold mb-6 text-blue-700">Thông báo</h2>
        {loading ? (
          <div className="text-center text-gray-500 py-8">
            Đang tải thông báo...
          </div>
        ) : notifications.length === 0 ? (
          <div className="text-center text-gray-500 py-8">
            Không có thông báo nào.
          </div>
        ) : (
          <ul className="space-y-3">
            {notifications.map((notification) => (
              <li
                key={notification.id}
                className={`flex items-start gap-4 bg-white shadow rounded-lg px-6 py-4 cursor-pointer hover:bg-gray-50 transition ${
                  !notification.read ? "border-l-4 border-blue-500" : ""
                }`}
                onClick={() => handleNotificationClick(notification)}
              >
                <div className="text-3xl">
                  {getNotificationIcon(notification.type)}
                </div>
                <div className="flex-1">
                  <div
                    className={`font-medium ${!notification.read ? "text-gray-900" : "text-gray-600"}`}
                  >
                    {notification.message || "Thông báo mới"}
                  </div>
                  <div className="text-sm text-gray-500 mt-1">
                    {notification.createdDate
                      ? new Date(notification.createdDate).toLocaleString()
                      : ""}
                  </div>
                </div>
                <div className="flex gap-2">
                  {!notification.read && (
                    <button
                      className="text-blue-600 hover:underline text-sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleMarkAsRead(notification.id);
                      }}
                    >
                      Đánh dấu đã đọc
                    </button>
                  )}
                  <button
                    className="text-red-600 hover:underline text-sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(notification.id);
                    }}
                  >
                    Xóa
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </AppLayout>
  );
}
