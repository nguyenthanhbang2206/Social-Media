import React, { useEffect, useState } from "react";
import axios from "axios";
import { useSelector } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";

export default function FriendList() {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const token = localStorage.getItem("token");
  const [friends, setFriends] = useState([]);
  const [loading, setLoading] = useState(false);

  const location = useLocation();

  useEffect(() => {
    const fetchFriends = async () => {
      if (!user || !user.id) return;
      setLoading(true);
      try {
        const res = await axios.get(
          `http://localhost:8080/api/v1/friends/${user.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setFriends(res.data.data || []);
      } catch (err) {
        setFriends([]);
      }
      setLoading(false);
    };
    fetchFriends();
  }, [user, token]);
  const handleUserClick = (id) => {
    navigate("/users/" + id);
  };

  const [searchKeyword, setSearchKeyword] = useState("");
  const handleNavbarSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(searchKeyword.trim())}`);
    }
  };
  return (
    <div className="bg-gray-100 min-h-screen font-sans">
      {/* Navbar */}
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
          <img
            src="https://randomuser.me/api/portraits/men/32.jpg"
            alt="Avatar"
            className="w-8 h-8 rounded-full border-2 border-blue-500"
          />
        </div>
      </nav>

      {/* Main Content */}
      <div className="flex pt-20 max-w-7xl mx-auto">
        {/* Left Sidebar */}
        <aside className="w-1/4 hidden md:block p-4">
          <div className="bg-white rounded-lg shadow p-4 sticky top-24">
            <div className="font-bold text-lg mb-4 text-blue-600">Bạn bè</div>
            <ul className="space-y-2">
              <li>
                <Link
                  to="/friend-list"
                  className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                    location.pathname === "/friend-list"
                      ? "bg-blue-100 text-blue-700"
                      : "text-gray-700"
                  }`}
                >
                  Danh sách bạn bè
                </Link>
              </li>
              <li>
                <Link
                  to="/friend-requests"
                  className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                    location.pathname === "/friend-requests"
                      ? "bg-blue-100 text-blue-700"
                      : "text-gray-700"
                  }`}
                >
                  Lời mời kết bạn
                </Link>
              </li>
              <li>
                <Link
                  to="/suggestion-friends"
                  className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                    location.pathname === "/suggestion-friends"
                      ? "bg-blue-100 text-blue-700"
                      : "text-gray-700"
                  }`}
                >
                  Bạn bè gợi ý
                </Link>
              </li>
            </ul>
          </div>
        </aside>
        <main className="w-3/4 p-4 space-y-4">
          <h2 className="text-xl font-bold mb-4 text-blue-700">
            Danh sách bạn bè
          </h2>
          {loading ? (
            <div className="text-center text-gray-500 py-8">
              Đang tải danh sách bạn bè...
            </div>
          ) : friends.length === 0 ? (
            <div className="text-center text-gray-500 py-8">
              Chưa có bạn bè nào.
            </div>
          ) : (
            <ul className="grid grid-cols-1 gap-6">
              {friends.map((friend) => (
                <li
                  onClick={() => handleUserClick(friend.id)}
                  key={friend.id}
                  className="flex items-center gap-3 bg-white shadow rounded-lg px-4 py-3 hover:bg-blue-50 cursor-pointer transition"
                >
                  <img
                    src={
                      friend.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-14 h-14 rounded-full object-cover border"
                  />
                  <div className="min-w-0">
                    <div className="font-semibold truncate">
                      {friend.fullName}
                    </div>
                    <div className="text-xs text-gray-500 truncate">
                      {friend.gender === "MALE"
                        ? "Nam"
                        : friend.gender === "FEMALE"
                        ? "Nữ"
                        : "Khác"}
                      {friend.dateOfBirth && (
                        <>
                          {" "}
                          · {new Date(friend.dateOfBirth).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </main>
      </div>
    </div>
  );
}
