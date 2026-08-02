import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  getFriendRequestsReceived,
  getFriends,
} from "../../api/FriendShip/Action";
import { navigateToUserProfile } from "../../utils/profileNavigation";

export default function RightSidebar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const { friendRequests, friends } = useSelector((state) => state.friendship);

  useEffect(() => {
    if (user && user.id) {
      dispatch(getFriendRequestsReceived()).catch((err) => {
        console.error("Error fetching friend requests:", err);
      });
      dispatch(getFriends(user.id)).catch((err) => {
        console.error("Error fetching friends:", err);
      });
    }
  }, [user, dispatch]);

  const handleAcceptRequest = (requesterId) => {
    navigate("/friend-requests");
  };

  const handleNavigateProfile = (targetUserId) => {
    navigateToUserProfile(navigate, user?.id, targetUserId);
  };

  return (
    <aside className="fixed right-0 top-16 h-[calc(100vh-4rem)] w-80 bg-white shadow-lg overflow-y-auto z-40">
      <div className="p-4">
        <h3 className="font-bold text-lg mb-4">Sponsored</h3>
        <div className="space-y-4 mb-6">
          <div className="flex items-center space-x-3">
            <div className="w-24 h-24 bg-gray-200 rounded-lg"></div>
            <div>
              <div className="font-semibold">Ad Title</div>
              <div className="text-sm text-gray-500">Ad description</div>
            </div>
          </div>
        </div>

        <hr className="my-4 border-gray-200" />

        <h3 className="font-bold text-lg mb-4">Friend Requests</h3>
        <div className="space-y-3 mb-6">
          {friendRequests && friendRequests.length > 0 ? (
            friendRequests.slice(0, 3).map((request) => (
              <div key={request.id} className="flex items-center space-x-3">
                <img
                  src={
                    request.sender?.avatar ||
                    request.avatar ||
                    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  }
                  alt="avatar"
                  className="w-12 h-12 rounded-full object-cover"
                />
                <div className="flex-1">
                  <div className="font-semibold">
                    {request.sender?.fullName || request.fullName}
                  </div>
                  <button
                    className="text-xs text-blue-600 hover:underline"
                    onClick={() =>
                      handleAcceptRequest(request.sender?.id || request.id)
                    }
                  >
                    View request
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="text-sm text-gray-500">No friend requests</div>
          )}
        </div>

        <hr className="my-4 border-gray-200" />

        <h3 className="font-bold text-lg mb-4">Contacts</h3>
        <div className="space-y-3">
          {friends && friends.length > 0 ? (
            friends.slice(0, 5).map((friend) => (
              <div
                key={friend.id}
                className="flex items-center space-x-3 p-2 hover:bg-gray-100 rounded-lg cursor-pointer"
                onClick={() => handleNavigateProfile(friend.id)}
              >
                <img
                  src={
                    friend.avatar ||
                    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  }
                  alt="avatar"
                  className="w-10 h-10 rounded-full object-cover"
                />
                <div className="font-semibold">{friend.fullName}</div>
              </div>
            ))
          ) : (
            <div className="text-sm text-gray-500">No contacts</div>
          )}
        </div>
      </div>
    </aside>
  );
}
