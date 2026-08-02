import React from "react";
import { useSelector } from "react-redux";
import { Link, useLocation } from "react-router-dom";

export default function LeftSidebar() {
  const { user } = useSelector((state) => state.auth);
  const location = useLocation();

  const menuItems = [
    { icon: "🏠", label: "News Feed", path: "/" },
    { icon: "👤", label: "My Profile", path: "/profile" },
    { type: "divider" },
    { icon: "👥", label: "Friends", path: "/friend-list", submenu: true },
    {
      icon: "👋",
      label: "Friend Requests",
      path: "/friend-requests",
      indent: true,
    },
    {
      icon: "💡",
      label: "Friend Suggestions",
      path: "/suggestion-friends",
      indent: true,
    },
    {
      icon: "🚫",
      label: "Blocked Users",
      path: "/blocked-users",
      indent: true,
    },
    { type: "divider" },
    { icon: "👥", label: "Groups", path: "/groups", submenu: true },
    { icon: "📁", label: "My Groups", path: "/groups?tab=my", indent: true },
    { icon: "🌐", label: "All Groups", path: "/groups?tab=all", indent: true },
    { icon: "➕", label: "Create Group", path: "/groups/create", indent: true },
  ];

  const isActive = (path) => {
    if (path === "/") return location.pathname === "/";
    return location.pathname.startsWith(path);
  };

  return (
    <aside className="fixed left-0 top-16 h-[calc(100vh-4rem)] w-64 bg-white shadow-lg overflow-y-auto z-40">
      <div className="p-4">
        {/* User Profile Summary */}
        <Link
          to="/profile"
          className="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-100 mb-4"
        >
          <img
            src={
              user?.avatar ||
              "https://cdn-icons-png.flaticon.com/512/149/149071.png"
            }
            alt="Avatar"
            className="w-12 h-12 rounded-full"
          />
          <div>
            <div className="font-semibold">{user?.fullName || "User"}</div>
            <div className="text-sm text-gray-500">View Profile</div>
          </div>
        </Link>

        {/* Navigation Menu */}
        <nav className="space-y-1">
          {menuItems.map((item, index) => {
            if (item.type === "divider") {
              return <hr key={index} className="my-3 border-gray-200" />;
            }

            return (
              <Link
                key={index}
                to={item.path}
                className={`flex items-center space-x-3 p-3 rounded-lg ${
                  isActive(item.path)
                    ? "bg-blue-100 text-blue-700"
                    : "hover:bg-gray-100"
                } ${item.indent ? "ml-4" : ""}`}
              >
                <span className="text-xl">{item.icon}</span>
                <span className="font-medium">{item.label}</span>
              </Link>
            );
          })}
        </nav>
      </div>
    </aside>
  );
}
