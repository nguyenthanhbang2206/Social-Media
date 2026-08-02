import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getAllUsers } from "../api/User/Action";
import { getFriends, sendFriendRequest } from "../api/FriendShip/Action";
import AppLayout from "../components/layout/AppLayout";
import { navigateToUserProfile } from "../utils/profileNavigation";

export default function SuggestionFriends() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const { friends } = useSelector((state) => state.friendship);
  const { allUsers } = useSelector((state) => state.user);
  const [loading, setLoading] = useState(false);

  const suggestions = allUsers
    ? allUsers.filter(
        (u) =>
          u.id !== user?.id && !friends?.some((friend) => friend.id === u.id),
      )
    : [];

  useEffect(() => {
    if (user && user.id) {
      setLoading(true);
      Promise.all([
        dispatch(getAllUsers()),
        dispatch(getFriends(user.id)),
      ]).finally(() => {
        setLoading(false);
      });
    }
  }, [user, dispatch]);

  const handleAddFriend = async (userId) => {
    try {
      await dispatch(sendFriendRequest(userId));
    } catch (err) {
      alert(err?.response?.data?.message || "Không gửi được lời mời kết bạn");
    }
  };

  const handleUserClick = (id) => {
    navigateToUserProfile(navigate, user?.id, id);
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold text-blue-600 mb-6">Bạn bè gợi ý</h1>
        {loading ? (
          <div className="text-gray-500">Đang tải...</div>
        ) : suggestions.length === 0 ? (
          <div className="text-gray-500">Không có gợi ý nào.</div>
        ) : (
          <ul className="grid grid-cols-1 gap-4">
            {suggestions.map((suggestion) => (
              <li
                key={suggestion.id}
                className="flex items-center justify-between bg-white rounded-lg shadow px-6 py-4"
              >
                <div
                  className="flex items-center gap-4 cursor-pointer"
                  onClick={() => handleUserClick(suggestion.id)}
                >
                  <img
                    src={
                      suggestion.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-14 h-14 rounded-full object-cover border"
                  />
                  <div>
                    <div className="font-semibold text-lg">
                      {suggestion.fullName}
                    </div>
                    <div className="text-sm text-gray-500">
                      {suggestion.gender === "MALE"
                        ? "Nam"
                        : suggestion.gender === "FEMALE"
                          ? "Nữ"
                          : "Khác"}
                      {suggestion.dateOfBirth && (
                        <>
                          {" "}
                          ·{" "}
                          {new Date(
                            suggestion.dateOfBirth,
                          ).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                </div>
                <button
                  className="px-4 py-2 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700"
                  onClick={() => handleAddFriend(suggestion.id)}
                >
                  Kết bạn
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </AppLayout>
  );
}
