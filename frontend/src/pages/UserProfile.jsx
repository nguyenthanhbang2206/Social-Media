import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../config/api";
import { useDispatch, useSelector } from "react-redux";
import {
  getPostsByUser,
  getReactionsOfPost,
  getReactPostByMeAndPostId,
  reactPost,
  unreactPost,
} from "../api/Post/Action";
import axios from "axios";

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

export default function UserProfile() {
  const { userId } = useParams();
  const dispatch = useDispatch();
  const { posts, loading } = useSelector((state) => state.post);
  const { user: userLogin } = useSelector((state) => state.auth);
  const [postReactionsData, setPostReactionsData] = useState({});
  const [myReactionsData, setMyReactionsData] = useState({});
  const [showReactionModal, setShowReactionModal] = useState(false);
  const [modalReactions, setModalReactions] = useState([]);
  const [modalPostId, setModalPostId] = useState(null);
  const [user, setUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  // Friend status
  const [friendStatus, setFriendStatus] = useState(null);
  const [friendShipId, setFriendShipId] = useState(null);
  const [friendActionLoading, setFriendActionLoading] = useState(false);

  const token = localStorage.getItem("token");

  // Load reactions for all posts after posts loaded
  useEffect(() => {
    if (posts && posts.length > 0) {
      posts.forEach((post) => {
        loadPostReactions(post.id);
      });
    }
  }, [posts]);

  useEffect(() => {
    // Lấy thông tin user
    const fetchUser = async () => {
      setLoadingUser(true);
      try {
        const res = await api.get(`/users/${userId}`);
        setUser(res.data.data);
      } catch (err) {
        setUser(null);
      }
      setLoadingUser(false);
    };

    fetchUser();
    dispatch(getPostsByUser(userId));
    fetchFriendStatus();
    // eslint-disable-next-line
  }, [userId, dispatch]);

  // Lấy trạng thái bạn bè
  const fetchFriendStatus = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/v1/friends/status/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setFriendStatus(res.data.data?.status || null);
      setFriendShipId(res.data.data?.id || null);
    } catch (err) {
      setFriendStatus(null);
      setFriendShipId(null);
    }
  };

  // Gửi lời mời kết bạn
  const handleSendRequest = async () => {
    setFriendActionLoading(true);
    try {
      await axios.post(
        `http://localhost:8080/api/v1/friend-requests/${userId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await fetchFriendStatus();
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Hủy lời mời kết bạn đã gửi
  const handleCancelRequest = async () => {
    setFriendActionLoading(true);
    try {
      await axios.delete(
        `http://localhost:8080/api/v1/friend-requests/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await fetchFriendStatus();
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Chấp nhận lời mời kết bạn
  const handleAccept = async () => {
    setFriendActionLoading(true);
    try {
      await axios.put(
        `http://localhost:8080/api/v1/friend-requests/${userId}/accept`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await fetchFriendStatus();
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Từ chối lời mời kết bạn
  const handleRefuse = async () => {
    setFriendActionLoading(true);
    try {
      await axios.put(
        `http://localhost:8080/api/v1/friend-requests/${userId}/refuse`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      await fetchFriendStatus();
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Hủy kết bạn
  const handleUnfriend = async () => {
    setFriendActionLoading(true);
    try {
      await axios.delete(`http://localhost:8080/api/v1/friends/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      await fetchFriendStatus();
    } catch (err) {}
    setFriendActionLoading(false);
  };

  // Gọi API react
  const handleReact = async (postId, reactionType) => {
    await dispatch(reactPost(postId, reactionType));
    await loadPostReactions(postId);
  };

  // Gọi API unreact
  const handleUnreact = async (postId) => {
    await dispatch(unreactPost(postId));
    await loadPostReactions(postId);
  };

  const loadPostReactions = async (postId) => {
    try {
      const reactions = await dispatch(getReactionsOfPost(postId));
      const myReaction = await dispatch(getReactPostByMeAndPostId(postId));

      setPostReactionsData((prev) => ({
        ...prev,
        [postId]: Array.isArray(reactions) ? reactions : [],
      }));

      setMyReactionsData((prev) => ({
        ...prev,
        [postId]: myReaction || null,
      }));
    } catch (error) {
      setPostReactionsData((prev) => ({
        ...prev,
        [postId]: [],
      }));
      setMyReactionsData((prev) => ({
        ...prev,
        [postId]: null,
      }));
    }
  };

  const handleShowReactionsModal = async (postId) => {
    setModalPostId(postId);
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
    setModalPostId(null);
  };

  // Lấy media url đầy đủ
  const getFullMediaUrl = (mediaUrl) => {
    if (!mediaUrl) return "";
    if (mediaUrl.startsWith("http")) return mediaUrl;
    return BASE_FILE_URL + mediaUrl;
  };

  // Hiển thị button theo trạng thái bạn bè
  const renderFriendButton = () => {
    if (!userLogin || userLogin.id === Number(userId)) return null;
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
        // Nếu userLogin là người gửi thì cho phép hủy lời mời, nếu là người nhận thì cho phép chấp nhận/từ chối
        // Để xác định, cần so sánh sender.id === userLogin.id
        return (
          <>
            {userLogin.id === user?.id
              ? null
              : friendShipId && (
                  <>
                    {userLogin.id === userId /* always true, fix below */ ? (
                      // Nếu là người gửi lời mời
                      <button
                        className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
                        disabled={friendActionLoading}
                        onClick={handleCancelRequest}
                      >
                        {friendActionLoading
                          ? "Đang xử lý..."
                          : "Hủy lời mời kết bạn"}
                      </button>
                    ) : (
                      // Nếu là người nhận lời mời
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
                    )}
                  </>
                )}
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
    <div className="bg-gray-100 min-h-screen">
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
            <div className="mt-4">{renderFriendButton()}</div>
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
              const myReaction = myReactionsData[post.id];

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
                        {post.createdBy || "Người dùng"}
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
                    {myReaction && myReaction.reactionType ? (
                      <button
                        className="flex items-center space-x-1 px-3 py-2 rounded-lg bg-gray-100 text-blue-600"
                        onClick={() => handleUnreact(post.id)}
                      >
                        <span className="text-lg">
                          {REACTION_EMOJIS[myReaction.reactionType]}
                        </span>
                        <span className="text-sm">
                          Bỏ {myReaction.reactionType.toLowerCase()}
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
                    <button className="flex items-center space-x-1 px-3 py-2 rounded-lg hover:bg-gray-100 text-gray-600">
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
    </div>
  );
}
