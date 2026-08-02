import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getFriends } from "../api/FriendShip/Action";
import AppLayout from "../components/layout/AppLayout";
import { navigateToUserProfile } from "../utils/profileNavigation";

export default function FriendList() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const { friends, loading } = useSelector((state) => state.friendship);

  useEffect(() => {
    if (user && user.id) {
      dispatch(getFriends(user.id));
    }
  }, [user, dispatch]);

  const handleUserClick = (id) => {
    navigateToUserProfile(navigate, user?.id, id);
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto">
        <h2 className="text-2xl font-bold mb-6 text-blue-700">
          Danh sách bạn bè
        </h2>
        {loading ? (
          <div className="text-center text-gray-500 py-8">
            Đang tải danh sách bạn bè...
          </div>
        ) : !friends || friends.length === 0 ? (
          <div className="text-center text-gray-500 py-8">
            Chưa có bạn bè nào.
          </div>
        ) : (
          <ul className="grid grid-cols-1 gap-4">
            {friends.map((friend) => (
              <li
                onClick={() => handleUserClick(friend.id)}
                key={friend.id}
                className="flex items-center gap-4 bg-white shadow rounded-lg px-6 py-4 hover:bg-blue-50 cursor-pointer transition"
              >
                <img
                  src={
                    friend.avatar ||
                    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  }
                  alt="avatar"
                  className="w-16 h-16 rounded-full object-cover border"
                />
                <div className="min-w-0 flex-1">
                  <div className="font-semibold text-lg truncate">
                    {friend.fullName}
                  </div>
                  <div className="text-sm text-gray-500">
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
      </div>
    </AppLayout>
  );
}
