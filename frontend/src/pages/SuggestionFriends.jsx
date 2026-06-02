import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import api from "../config/api";
import Header from "../components/Header";

export default function SuggestionFriends() {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchSuggestions = async () => {
    if (!user || !user.id) return;
    setLoading(true);
    try {
      const [usersRes, friendsRes] = await Promise.all([
        api.get("/users"),
        api.get(`/friends/${user.id}`),
      ]);
      const allUsers = usersRes.data.data || [];
      const friends = friendsRes.data.data || [];
      const friendIds = new Set(friends.map((friend) => friend.id));
      const filtered = allUsers.filter(
        (u) => u.id !== user.id && !friendIds.has(u.id)
      );
      setSuggestions(filtered);
    } catch (err) {
      setSuggestions([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchSuggestions();
  }, [user]);

  const handleAddFriend = async (userId) => {
    try {
      await api.post(`/friend-requests/${userId}`);
      setSuggestions((prev) => prev.filter((item) => item.id !== userId));
    } catch (err) {
      alert(err?.response?.data?.message || "Không gửi được lời mời kết bạn");
    }
  };

  const handleUserClick = (id) => {
    navigate("/users/" + id);
  };

  return (
    <div className="bg-gray-100 min-h-screen font-sans">
      <Header />
      <div className="max-w-4xl mx-auto pt-24 py-8">
      <h1 className="text-2xl font-bold text-blue-600 mb-4">Bạn bè gợi ý</h1>
      {loading ? (
        <div className="text-gray-500">Đang tải...</div>
      ) : suggestions.length === 0 ? (
        <div className="text-gray-500">Không có gợi ý nào.</div>
      ) : (
        <ul className="grid grid-cols-1 gap-4">
          {suggestions.map((suggestion) => (
            <li
              key={suggestion.id}
              className="flex items-center justify-between bg-white rounded-lg shadow px-4 py-3"
            >
              <div
                className="flex items-center gap-3 cursor-pointer"
                onClick={() => handleUserClick(suggestion.id)}
              >
                <img
                  src={
                    suggestion.avatar ||
                    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  }
                  alt="avatar"
                  className="w-12 h-12 rounded-full object-cover border"
                />
                <div>
                  <div className="font-semibold">{suggestion.fullName}</div>
                  <div className="text-xs text-gray-500">
                    {suggestion.gender === "MALE"
                      ? "Nam"
                      : suggestion.gender === "FEMALE"
                      ? "Nữ"
                      : "Khác"}
                    {suggestion.dateOfBirth && (
                      <> · {new Date(suggestion.dateOfBirth).toLocaleDateString()}</>
                    )}
                  </div>
                </div>
              </div>
              <button
                className="px-3 py-1 rounded bg-blue-500 text-white font-semibold hover:bg-blue-600"
                onClick={() => handleAddFriend(suggestion.id)}
              >
                Kết bạn
              </button>
            </li>
          ))}
        </ul>
      )}
      </div>
    </div>
  );
}

