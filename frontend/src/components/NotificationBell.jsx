import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  deleteNotification,
  getNotifications,
  markAllAsRead,
  markAsRead,
} from "../api/Notification/Action";

const DEFAULT_AVATAR =
  "https://cdn-icons-png.flaticon.com/512/149/149071.png";

const formatTimestamp = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString();
};

export default function NotificationBell() {
  const dispatch = useDispatch();
  const { notifications, unreadCount, loading, page, totalPages } = useSelector(
    (state) => state.notification,
  );
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);
  const hasLoadedRef = useRef(false);

  useEffect(() => {
    if (!open || hasLoadedRef.current) return;
    dispatch(getNotifications(0));
    hasLoadedRef.current = true;
  }, [open, dispatch]);

  useEffect(() => {
    if (!open) return;

    const handleClickOutside = (event) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target)
      ) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  const handleMarkRead = (notification) => {
    if (!notification?.isRead) {
      dispatch(markAsRead(notification.id));
    }
  };

  const handleDelete = (notificationId) => {
    dispatch(deleteNotification(notificationId));
  };

  const handleLoadMore = () => {
    if (page + 1 < totalPages) {
      dispatch(getNotifications(page + 1));
    }
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        type="button"
        onClick={() => setOpen((prev) => !prev)}
        className="relative p-2 rounded-full hover:bg-gray-100"
        aria-label="Notifications"
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M18 8a6 6 0 10-12 0c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 01-3.46 0" />
        </svg>
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full px-1.5">
            {unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-96 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
          <div className="flex items-center justify-between px-4 py-2 border-b">
            <span className="font-semibold">Notifications</span>
            {unreadCount > 0 && (
              <button
                type="button"
                onClick={() => dispatch(markAllAsRead())}
                className="text-sm text-blue-600 hover:underline"
              >
                Mark all as read
              </button>
            )}
          </div>

          <div className="max-h-96 overflow-y-auto">
            {notifications.length === 0 && !loading ? (
              <div className="px-4 py-6 text-center text-gray-500">
                No notifications yet.
              </div>
            ) : (
              notifications.map((notification) => (
                <div
                  key={notification.id}
                  onClick={() => handleMarkRead(notification)}
                  className={`flex items-start gap-3 px-4 py-3 border-b cursor-pointer hover:bg-gray-50 ${
                    notification.isRead ? "bg-white" : "bg-blue-50"
                  }`}
                >
                  <img
                    src={notification.actorAvatar || DEFAULT_AVATAR}
                    alt="avatar"
                    className="w-10 h-10 rounded-full object-cover border"
                  />
                  <div className="flex-1 min-w-0">
                    <div className="text-sm text-gray-800">
                      {notification.message || "New notification"}
                    </div>
                    <div className="text-xs text-gray-400">
                      {formatTimestamp(notification.createdDate)}
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={(event) => {
                      event.stopPropagation();
                      handleDelete(notification.id);
                    }}
                    className="text-xs text-gray-400 hover:text-red-500"
                  >
                    Delete
                  </button>
                </div>
              ))
            )}
          </div>

          <div className="px-4 py-2 flex items-center justify-between">
            <button
              type="button"
              onClick={handleLoadMore}
              disabled={page + 1 >= totalPages || loading}
              className="text-sm text-blue-600 disabled:text-gray-400"
            >
              {page + 1 < totalPages ? "Load more" : "No more"}
            </button>
            {loading && <span className="text-xs text-gray-400">Loading...</span>}
          </div>
        </div>
      )}
    </div>
  );
}
