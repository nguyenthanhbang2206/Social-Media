import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import NotificationBell from "./NotificationBell";

export default function Header() {
  const navigate = useNavigate();
  const [searchKeyword, setSearchKeyword] = useState("");

  const handleNavbarSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(searchKeyword.trim())}`);
    }
  };

  return (
    <nav className="bg-white shadow px-4 py-2 flex items-center justify-between fixed w-full z-20">
      <div className="flex items-center space-x-2">
        <img
          src="https://static.xx.fbcdn.net/rsrc.php/yo/r/iRmz9lCMBD2.ico"
          alt="Logo"
          className="w-8 h-8"
        />
        <span className="text-blue-600 font-bold text-2xl tracking-tight">
          facebook
        </span>
      </div>
      <div className="flex items-center space-x-4">
        <form onSubmit={handleNavbarSearch} className="flex items-center">
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="Tìm kiếm người dùng"
            className="px-3 py-1 border rounded-l-full focus:outline-none bg-gray-100"
            style={{ width: 200 }}
          />
          <button
            type="submit"
            className="bg-blue-500 text-white px-3 py-1 rounded-r-full hover:bg-blue-600"
          >
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24">
              <path
                d="M21 21l-4.35-4.35M11 19a8 8 0 100-16 8 8 0 000 16z"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </button>
        </form>
        <NotificationBell />
        <img
          src="https://randomuser.me/api/portraits/men/32.jpg"
          alt="Avatar"
          className="w-8 h-8 rounded-full border-2 border-blue-500"
        />
      </div>
    </nav>
  );
}
