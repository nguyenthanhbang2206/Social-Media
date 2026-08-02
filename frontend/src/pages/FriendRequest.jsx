import React, { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import {
  getFriendRequestsReceived,
  acceptFriendRequest,
  refuseFriendRequest,
} from "../api/FriendShip/Action";
import AppLayout from "../components/layout/AppLayout";
import { navigateToUserProfile } from "../utils/profileNavigation";

export default function FriendRequest() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const { friendRequests, loading } = useSelector((state) => state.friendship);
  const [actionLoading, setActionLoading] = useState({}); // { [userId]: boolean }

  const location = useLocation();

  useEffect(() => {
    if (user && user.id) {
      dispatch(getFriendRequestsReceived());
    }
  }, [user, dispatch]);

  const handleUserClick = (id) => {
    navigateToUserProfile(navigate, user?.id, id);
  };

  const handleAccept = async (senderId) => {
    if (!senderId) {
      console.error("Cannot accept friend request: senderId is undefined");
      return;
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: true }));
    try {
      await dispatch(acceptFriendRequest(senderId));
    } catch (err) {
      console.error("Error accepting friend request:", err);
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: false }));
  };

  const handleRefuse = async (senderId) => {
    if (!senderId) {
      console.error("Cannot refuse friend request: senderId is undefined");
      return;
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: true }));
    try {
      await dispatch(refuseFriendRequest(senderId));
    } catch (err) {
      console.error("Error refusing friend request:", err);
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: false }));
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-2xl font-bold mb-6 text-blue-700">
            Lời mời kết bạn đã nhận
          </h2>
          {loading ? (
            <div className="text-center text-gray-500 py-8">
              Đang tải lời mời kết bạn...
            </div>
          ) : !friendRequests || friendRequests.length === 0 ? (
            <div className="text-center text-gray-500 py-8">
              Không có lời mời kết bạn nào.
            </div>
          ) : (
            <ul className="space-y-3">
              {friendRequests.map((req) => (
                <li
                  key={req.id}
                  className="flex items-center gap-4 px-4 py-3 hover:bg-gray-50 rounded-lg border"
                >
                  <img
                    src={
                      req.sender?.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-14 h-14 rounded-full object-cover cursor-pointer"
                    onClick={() => handleUserClick(req.sender?.id)}
                  />
                  <div className="flex-1 min-w-0">
                    <div
                      className="font-semibold text-lg cursor-pointer hover:text-blue-600"
                      onClick={() => handleUserClick(req.sender?.id)}
                    >
                      {req.sender?.fullName}
                    </div>
                    <div className="text-sm text-gray-500">
                      {req.sender?.gender === "MALE"
                        ? "Nam"
                        : req.sender?.gender === "FEMALE"
                          ? "Nữ"
                          : "Khác"}
                      {req.sender?.dateOfBirth && (
                        <>
                          {" "}
                          ·{" "}
                          {new Date(
                            req.sender.dateOfBirth,
                          ).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button
                      className="px-4 py-2 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
                      disabled={actionLoading[req.sender?.id]}
                      onClick={() => handleAccept(req.sender?.id)}
                    >
                      {actionLoading[req.sender?.id]
                        ? "Đang xử lý..."
                        : "Đồng ý"}
                    </button>
                    <button
                      className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
                      disabled={actionLoading[req.sender?.id]}
                      onClick={() => handleRefuse(req.sender?.id)}
                    >
                      Từ chối
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
