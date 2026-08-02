import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../config/api";
import { useDispatch, useSelector } from "react-redux";
import {
  getPostsByUser,
  getReactionsOfPost,
  reactPost,
  unreactPost,
} from "../api/Post/Action";
import { getProfile } from "../api/Auth/Action";
import {
  getFriendStatus,
  sendFriendRequest,
  cancelFriendRequest,
  acceptFriendRequest,
  refuseFriendRequest,
  unfriend,
} from "../api/FriendShip/Action";
import { getBlockedUsers, blockUser, unblockUser } from "../api/Block/Action";
import AppLayout from "../components/layout/AppLayout";

const BASE_FILE_URL = "http://localhost:8080/images/post-media/";
const REACTION_ORDER = ["LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"];
const REACTION_EMOJIS = {
  LIKE: "👍",
  LOVE: "❤️",
  HAHA: "😂",
  WOW: "😮",
  SAD: "😢",
  ANGRY: "😠",
};

const getPostOwnerName = (post) =>
  post?.ownerName || post?.createdBy || post?.createdByName || "Người dùng";

export default function UserProfile() {
  const { userId } = useParams();
  const profileId = Number(userId);
  const dispatch = useDispatch();
  const { posts, loading } = useSelector((state) => state.post);
  const { user: userLogin } = useSelector((state) => state.auth);
  const { friendStatus, friendship } = useSelector((state) => state.friendship);
  const { blockedUsers } = useSelector((state) => state.block);
  const [showReactionModal, setShowReactionModal] = useState(false);
  const [modalReactions, setModalReactions] = useState([]);
  const [user, setUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [editFullName, setEditFullName] = useState("");
  const [editGender, setEditGender] = useState("OTHER");
  const [updateLoading, setUpdateLoading] = useState(false);
  const [friendActionLoading, setFriendActionLoading] = useState(false);
  const [blockLoading, setBlockLoading] = useState(false);
  const storedUser = JSON.parse(localStorage.getItem("user") || "null");
  const currentUserId = Number(userLogin?.id || storedUser?.id || 0);
  const isSelfProfile = currentUserId > 0 && currentUserId === profileId;

  const token = localStorage.getItem("token");

  const fetchUser = async () => {
    if (!profileId) {
      setUser(null);
      setLoadingUser(false);
      return;
    }

    if (isSelfProfile) {
      setUser(userLogin || storedUser);
      setLoadingUser(false);
      return;
    }

    setLoadingUser(true);
    try {
      const res = await api.get(`/users/${profileId}`);
      setUser(res.data.data);
    } catch (err) {
      setUser(null);
    }
    setLoadingUser(false);
  };

  useEffect(() => {
    fetchUser();
    dispatch(getPostsByUser(profileId));
    if (!isSelfProfile) {
      dispatch(getFriendStatus(profileId));
      dispatch(getBlockedUsers());
    }
    // eslint-disable-next-line
  }, [profileId, dispatch, isSelfProfile]);

  useEffect(() => {
    if (user && currentUserId && currentUserId === profileId) {
      setEditFullName(user.fullName || "");
      setEditGender(user.gender || "OTHER");
    }
  }, [user, currentUserId, profileId]);

  const isBlocked =
    blockedUsers?.some((item) => Number(item?.blocked?.id) === profileId) ||
    false;

  // Gửi lời mời kết bạn
  const handleSendRequest = async () => {
    setFriendActionLoading(true);
    try {
      await dispatch(sendFriendRequest(profileId));
      dispatch(getFriendStatus(profileId));
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Hủy lời mời kết bạn đã gửi
  const handleCancelRequest = async () => {
    setFriendActionLoading(true);
    try {
      await dispatch(cancelFriendRequest(profileId));
      dispatch(getFriendStatus(profileId));
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Chấp nhận lời mời kết bạn
  const handleAccept = async () => {
    if (!profileId) {
      console.error("Cannot accept friend request: profileId is undefined");
      return;
    }
    setFriendActionLoading(true);
    try {
      await dispatch(acceptFriendRequest(profileId));
      dispatch(getFriendStatus(profileId));
    } catch (err) {
      console.error("Error accepting friend request:", err);
    }
    setFriendActionLoading(false);
  };

  // Từ chối lời mời kết bạn
  const handleRefuse = async () => {
    if (!profileId) {
      console.error("Cannot refuse friend request: profileId is undefined");
      return;
    }
    setFriendActionLoading(true);
    try {
      await dispatch(refuseFriendRequest(profileId));
      dispatch(getFriendStatus(profileId));
    } catch (err) {
      console.error("Error refusing friend request:", err);
    }
    setFriendActionLoading(false);
  };

  // Hủy kết bạn
  const handleUnfriend = async () => {
    if (!profileId) {
      console.error("Cannot unfriend: profileId is undefined");
      return;
    }
    setFriendActionLoading(true);
    try {
      await dispatch(unfriend(profileId));
      dispatch(getFriendStatus(profileId));
    } catch (err) {
      console.error("Error unfriending:", err);
    }
    setFriendActionLoading(false);
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    if (!currentUserId || currentUserId !== profileId) return;
    setUpdateLoading(true);
    try {
      const res = await api.put("/users/profile", {
        fullName: editFullName,
        gender: editGender,
      });
      if (res?.data?.data) {
        localStorage.setItem("user", JSON.stringify(res.data.data));
      }
      dispatch(getProfile(token));
      await fetchUser();
      setIsEditing(false);
    } catch (err) {}
    setUpdateLoading(false);
  };

  const handleBlock = async () => {
    if (!currentUserId || currentUserId === profileId) return;
    const reason = window.prompt("Lý do chặn (tùy chọn):") || "";
    setBlockLoading(true);
    try {
      await dispatch(blockUser(profileId, reason));
    } catch (err) {}
    setBlockLoading(false);
  };

  const handleUnblock = async () => {
    if (!currentUserId || currentUserId === profileId) return;
    setBlockLoading(true);
    try {
      await dispatch(unblockUser(profileId));
    } catch (err) {}
    setBlockLoading(false);
  };

  // Gọi API react
  const handleReact = async (postId, reactionType) => {
    await dispatch(reactPost(postId, reactionType));
    await dispatch(getPostsByUser(profileId));
  };

  // Gọi API unreact
  const handleUnreact = async (postId) => {
    await dispatch(unreactPost(postId));
    await dispatch(getPostsByUser(profileId));
  };

  const handleShare = async (postId) => {
    const shareContent = window.prompt("Nội dung chia sẻ (tùy chọn):", "");
    if (shareContent === null) return;
    try {
      await api.post(`/posts/${postId}/shares`, { shareContent });
      dispatch(getPostsByUser(profileId));
    } catch (err) {
      alert(
        "Lỗi chia sẻ: " + (err?.response?.data?.message || "Vui lòng thử lại"),
      );
    }
  };

  const handleShowReactionsModal = async (postId) => {
    setShowReactionModal(true);
    try {
      const reactions = await dispatch(getReactionsOfPost(postId));
      setModalReactions(Array.isArray(reactions) ? reactions : []);
    } catch (err) {
      setModalReactions([]);
    }
  };

  const handleCloseModal = () => {
    setShowReactionModal(false);
    setModalReactions([]);
  };

  // Lấy media url đầy đủ
  const getFullMediaUrl = (mediaUrl) => {
    if (!mediaUrl) return "";
    if (mediaUrl.startsWith("http")) return mediaUrl;
    return BASE_FILE_URL + mediaUrl;
  };

  // Hiển thị button theo trạng thái bạn bè
  const renderFriendButton = () => {
    if (!currentUserId || currentUserId === profileId) return null;
    switch (friendStatus) {
      case "ACCEPTED":
        return (
          <button
            className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
            disabled={friendActionLoading}
            onClick={handleUnfriend}
          >
            {friendActionLoading ? "Đang xử lý..." : "Hủy kết bạn"}
          </button>
        );
      case "PENDING":
        if (!friendship?.id) return null;
        if (friendship?.sender?.id === currentUserId) {
          return (
            <button
              className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
              disabled={friendActionLoading}
              onClick={handleCancelRequest}
            >
              {friendActionLoading ? "Đang xử lý..." : "Hủy lời mời kết bạn"}
            </button>
          );
        }
        return (
          <>
            <button
              className="px-4 py-2 rounded bg-blue-500 text-white font-semibold hover:bg-blue-600 transition mr-2"
              disabled={friendActionLoading}
              onClick={handleAccept}
            >
              {friendActionLoading ? "Đang xử lý..." : "Chấp nhận"}
            </button>
            <button
              className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
              disabled={friendActionLoading}
              onClick={handleRefuse}
            >
              Từ chối
            </button>
          </>
        );
      case "DECLINED":
      case "BLOCKED":
      case null:
      default:
        return (
          <button
            className="px-4 py-2 rounded bg-blue-500 text-white font-semibold hover:bg-blue-600 transition"
            disabled={friendActionLoading}
            onClick={handleSendRequest}
          >
            {friendActionLoading ? "Đang xử lý..." : "Gửi lời kết bạn"}
          </button>
        );
    }
  };

  return (
    <AppLayout>
      {/* Cover Photo */}
      <div className="relative h-64 bg-gray-300">
        {user && user.coverPhoto && (
          <img
            src={user.coverPhoto}
            alt="cover"
            className="w-full h-64 object-cover"
          />
        )}
        <div className="absolute left-1/2 bottom-0 transform -translate-x-1/2 translate-y-1/2">
          <img
            src={
              user && user.avatar
                ? user.avatar
                : "https://static.xx.fbcdn.net/rsrc.php/yo/r/iRmz9lCMBD2.ico"
            }
            alt="avatar"
            className="w-36 h-36 rounded-full border-4 border-white object-cover bg-white"
          />
        </div>
      </div>
      {/* User Info */}
      <div className="flex flex-col items-center mt-20 mb-8">
        {loadingUser ? (
          <div className="text-gray-500">Đang tải thông tin người dùng...</div>
        ) : user ? (
          <>
            <div className="text-2xl font-bold">{user.fullName}</div>
            <div className="text-gray-600">
              {user.gender === "MALE"
                ? "Nam"
                : user.gender === "FEMALE"
                  ? "Nữ"
                  : "Khác"}
              {user.dateOfBirth && (
                <> · {new Date(user.dateOfBirth).toLocaleDateString()}</>
              )}
            </div>
            {currentUserId && currentUserId === profileId && (
              <div className="mt-3 w-full max-w-md">
                {isEditing ? (
                  <form
                    onSubmit={handleUpdateProfile}
                    className="flex flex-col gap-2"
                  >
                    <input
                      className="border rounded px-3 py-2"
                      value={editFullName}
                      onChange={(e) => setEditFullName(e.target.value)}
                      placeholder="Họ và tên"
                      required
                    />
                    <select
                      className="border rounded px-3 py-2"
                      value={editGender}
                      onChange={(e) => setEditGender(e.target.value)}
                    >
                      <option value="MALE">Nam</option>
                      <option value="FEMALE">Nữ</option>
                      <option value="OTHER">Khác</option>
                    </select>
                    <div className="flex gap-2">
                      <button
                        className="px-4 py-2 rounded bg-blue-500 text-white font-semibold"
                        disabled={updateLoading}
                      >
                        {updateLoading ? "Đang lưu..." : "Lưu"}
                      </button>
                      <button
                        type="button"
                        className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold"
                        onClick={() => setIsEditing(false)}
                      >
                        Hủy
                      </button>
                    </div>
                  </form>
                ) : (
                  <button
                    className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300"
                    onClick={() => setIsEditing(true)}
                  >
                    Chỉnh sửa hồ sơ
                  </button>
                )}
              </div>
            )}
            <div className="mt-4 flex items-center gap-2">
              {renderFriendButton()}
              {currentUserId && currentUserId !== profileId && (
                <button
                  className={`px-4 py-2 rounded font-semibold transition ${
                    isBlocked
                      ? "bg-gray-200 text-gray-700 hover:bg-gray-300"
                      : "bg-red-500 text-white hover:bg-red-600"
                  }`}
                  disabled={blockLoading}
                  onClick={isBlocked ? handleUnblock : handleBlock}
                >
                  {blockLoading
                    ? "Đang xử lý..."
                    : isBlocked
                      ? "Bỏ chặn"
                      : "Chặn"}
                </button>
              )}
            </div>
          </>
        ) : (
          <div className="text-red-500">Không tìm thấy người dùng.</div>
        )}
      </div>
      {/* User's Posts */}
      <div className="max-w-2xl mx-auto space-y-4">
        {loading ? (
          <div className="text-center text-gray-500">Đang tải bài viết...</div>
        ) : posts.length === 0 ? (
          <div className="text-center text-gray-500">Chưa có bài viết nào.</div>
        ) : (
          <>
            {posts.map((post) => {
              const myReactionType = post.myReactionType;

              return (
                <div
                  key={post.id}
                  className="bg-white rounded-lg shadow p-4 mb-4"
                >
                  <div className="flex items-center space-x-3 mb-2">
                    <img
                      src="https://randomuser.me/api/portraits/men/32.jpg"
                      alt="Avatar"
                      className="w-10 h-10 rounded-full"
                    />
                    <div>
                      <div className="font-semibold">
                        {getPostOwnerName(post)}
                      </div>
                      <div className="text-xs text-gray-500">
                        {post.createdDate
                          ? new Date(post.createdDate).toLocaleString()
                          : ""}
                      </div>
                    </div>
                  </div>
                  <div className="mb-2">
                    <p>{post.content}</p>
                  </div>
                  {/* Hiển thị media nếu có */}
                  {post.media &&
                    post.media.length > 0 &&
                    post.media.map((media, idx) => (
                      <div key={idx} className="mb-2">
                        {media.mediaType === "IMAGE" && (
                          <img
                            src={getFullMediaUrl(media.mediaUrl)}
                            alt="media"
                            className="rounded-lg max-h-60"
                          />
                        )}
                        {media.mediaType === "VIDEO" && (
                          <video
                            src={getFullMediaUrl(media.mediaUrl)}
                            controls
                            className="rounded-lg max-h-60"
                          />
                        )}
                        {media.mediaType === "AUDIO" && (
                          <audio
                            src={getFullMediaUrl(media.mediaUrl)}
                            controls
                            className="w-full"
                          />
                        )}
                      </div>
                    ))}

                  {/* Reaction Summary */}
                  <div className="flex items-center py-2 border-t border-gray-200 text-sm text-gray-600 gap-4">
                    <button
                      className="flex items-center gap-1 px-2 py-1 rounded hover:bg-gray-100 font-semibold"
                      onClick={() => handleShowReactionsModal(post.id)}
                    >
                      <strong>{post.totalReactions}</strong>
                      <span>lượt reaction</span>
                    </button>
                    <span>
                      <strong>{post.totalComments}</strong> bình luận
                    </span>
                    <span>
                      <strong>{post.totalShares}</strong> chia sẻ
                    </span>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex items-center pt-2 border-t border-gray-200 space-x-4">
                    {/* Like/Unreact Button */}
                    {myReactionType ? (
                      <button
                        className="flex items-center space-x-1 px-3 py-2 rounded-lg bg-gray-100 text-blue-600"
                        onClick={() => handleUnreact(post.id)}
                      >
                        <span className="text-lg">
                          {REACTION_EMOJIS[myReactionType]}
                        </span>
                        <span className="text-sm">
                          Bỏ {myReactionType.toLowerCase()}
                        </span>
                      </button>
                    ) : (
                      <div className="flex space-x-1">
                        {REACTION_ORDER.map((reactionType) => (
                          <button
                            key={reactionType}
                            className="flex items-center px-2 py-1 rounded hover:bg-gray-100"
                            onClick={() => handleReact(post.id, reactionType)}
                          >
                            <span className="text-lg">
                              {REACTION_EMOJIS[reactionType]}
                            </span>
                            <span className="text-xs ml-1">
                              {reactionType.toLowerCase()}
                            </span>
                          </button>
                        ))}
                      </div>
                    )}
                    <button className="flex items-center space-x-1 px-3 py-2 rounded-lg hover:bg-gray-100 text-gray-600">
                      <span>💬</span>
                      <span className="text-sm">Bình luận</span>
                    </button>
                    <button
                      className="flex items-center space-x-1 px-3 py-2 rounded-lg hover:bg-gray-100 text-gray-600"
                      onClick={() => handleShare(post.id)}
                    >
                      <span>↗️</span>
                      <span className="text-sm">Chia sẻ</span>
                    </button>
                  </div>
                </div>
              );
            })}
            {/* Modal hiển thị danh sách reactions */}
            {showReactionModal && (
              <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg shadow-lg p-6 min-w-[320px] max-w-[90vw]">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-bold">
                      Danh sách reaction ({modalReactions.length})
                    </h2>
                    <button
                      className="text-gray-500 hover:text-red-500 text-xl"
                      onClick={handleCloseModal}
                    >
                      ×
                    </button>
                  </div>
                  {modalReactions.length === 0 ? (
                    <div className="text-gray-500 text-center py-4">
                      Chưa có ai reaction bài viết này.
                    </div>
                  ) : (
                    <ul className="divide-y divide-gray-200 max-h-[400px] overflow-y-auto">
                      {modalReactions.map((r, idx) => (
                        <li
                          key={r.id || idx}
                          className="py-2 flex items-center gap-2"
                        >
                          <span className="text-lg">
                            {REACTION_EMOJIS[r.reactionType]}
                          </span>
                          <span className="font-semibold">
                            {r.username || r.createdBy || "Người dùng"}
                          </span>
                          <span className="text-xs text-gray-400 ml-2">
                            {r.reactionType}
                          </span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </AppLayout>
  );
}
