import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getPosts,
  createPost,
  uploadFiles,
  reactPost,
  unreactPost,
  getReactPostByMeAndPostId,
  getReactionsOfPost,
} from "../api/Post/Action";
import UserSearch from "./UserSearch";
import { useNavigate } from "react-router-dom";
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

export default function Home() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [friends, setFriends] = useState([]);
  const [loadingFriends, setLoadingFriends] = useState(false);
  const { posts, loading, error, createSuccess, uploadLoading, uploadedFiles } =
    useSelector((state) => state.post);

  const [content, setContent] = useState("");
  const [media, setMedia] = useState([]);
  const [postReactionsData, setPostReactionsData] = useState({});
  const [myReactionsData, setMyReactionsData] = useState({});
  const [showReactionModal, setShowReactionModal] = useState(false);
  const [modalReactions, setModalReactions] = useState([]);
  const [modalPostId, setModalPostId] = useState(null);
  const fileInputRef = useRef();
  const { user } = useSelector((state) => state.auth);

  const token = localStorage.getItem("token");
  useEffect(() => {
    dispatch(getPosts());
  }, [dispatch, createSuccess]);

  // Load reactions for all posts after posts loaded
  useEffect(() => {
    if (posts && posts.length > 0) {
      posts.forEach((post) => {
        console.log(post.id);
        loadPostReactions(post.id);
      });
    }
  }, [posts]);

  useEffect(() => {
    if (uploadedFiles && uploadedFiles.length > 0) {
      setMedia((prev) => [...prev, ...uploadedFiles]);
    }
  }, [uploadedFiles]);
  useEffect(() => {
    console.log(user);

    const fetchFriends = async () => {
      if (!user || !user.id) return;
      setLoadingFriends(true);
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
      setLoadingFriends(false);
    };
    fetchFriends();
  }, [user, token]);

  // Lấy media url đầy đủ
  const getFullMediaUrl = (mediaUrl) => {
    if (!mediaUrl) return "";
    if (mediaUrl.startsWith("http")) return mediaUrl;
    return BASE_FILE_URL + mediaUrl;
  };

  // Tạo bài viết mới
  const handleSubmit = (e) => {
    e.preventDefault();
    const postMedia = media.map((file) => ({
      mediaUrl: file.mediaUrl,
      mediaType: file.mediaType,
      uploadOrder: file.uploadOrder,
    }));

    dispatch(
      createPost({
        content,
        privacy: "PUBLIC",
        media: postMedia,
      })
    );

    setContent("");
    setMedia([]);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  // Upload file
  const handleFileChange = (e) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      dispatch(uploadFiles(files, "post-media"));
    }
  };

  // Xóa media đã chọn
  const handleRemoveMedia = (idx) => {
    setMedia((prev) => prev.filter((_, i) => i !== idx));
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  // Gọi API react
  const handleReact = async (postId, reactionType) => {
    console.log(postId, reactionType);
    await dispatch(reactPost(postId, reactionType));
    await loadPostReactions(postId);
  };

  // Gọi API unreact
  const handleUnreact = async (postId) => {
    console.log(postId);
    await dispatch(unreactPost(postId));
    await loadPostReactions(postId);
  };

  // Lấy tất cả reactions và reaction của mình cho post
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
            <div className="flex flex-col items-center">
              <img
                src="https://randomuser.me/api/portraits/men/32.jpg"
                alt="Avatar"
                className="w-16 h-16 rounded-full mb-2"
              />
              <span className="font-semibold text-lg">Xin chào, User!</span>
            </div>
            <ul className="mt-6 space-y-2 text-gray-700">
              <li className="hover:bg-gray-100 rounded px-2 py-1 cursor-pointer">
                Bảng tin
              </li>
              <li
                onClick={() => navigate("/friend-list")}
                className="hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
              >
                Bạn bè
              </li>
              <li className="hover:bg-gray-100 rounded px-2 py-1 cursor-pointer">
                Nhóm
              </li>
              <li className="hover:bg-gray-100 rounded px-2 py-1 cursor-pointer">
                Marketplace
              </li>
              <li className="hover:bg-gray-100 rounded px-2 py-1 cursor-pointer">
                Kỷ niệm
              </li>
            </ul>
          </div>
        </aside>

        {/* Feed */}
        <main className="w-full md:w-2/4 p-4 space-y-4">
          {/* Create Post */}
          <div className="bg-white rounded-lg shadow p-4 mb-4">
            <div className="flex items-center mb-3">
              <img
                src="https://randomuser.me/api/portraits/men/32.jpg"
                alt="Avatar"
                className="w-10 h-10 rounded-full mr-3"
              />
              <input
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="Bạn đang nghĩ gì?"
                className="flex-1 border rounded-full px-4 py-2 bg-gray-100 focus:outline-none"
                required
              />
            </div>
            {/* Upload file */}
            <div className="mb-2 flex items-center space-x-2">
              <input
                type="file"
                multiple
                accept="image/*,video/*,audio/*"
                onChange={handleFileChange}
                ref={fileInputRef}
                className="block"
              />
              {uploadLoading && (
                <span className="text-blue-500 text-sm">Đang tải file...</span>
              )}
            </div>
            {/* Hiển thị media đã chọn */}
            <div className="flex flex-wrap gap-2 mb-2">
              {media.map((file, idx) => (
                <div key={idx} className="relative">
                  {file.mediaType === "IMAGE" && (
                    <img
                      src={getFullMediaUrl(file.mediaUrl)}
                      alt="media"
                      className="w-20 h-20 object-cover rounded"
                    />
                  )}
                  {file.mediaType === "VIDEO" && (
                    <video
                      src={getFullMediaUrl(file.mediaUrl)}
                      controls
                      className="w-20 h-20 object-cover rounded"
                    />
                  )}
                  {file.mediaType === "AUDIO" && (
                    <audio
                      src={getFullMediaUrl(file.mediaUrl)}
                      controls
                      className="w-20"
                    />
                  )}
                  <button
                    type="button"
                    className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs"
                    onClick={() => handleRemoveMedia(idx)}
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
            <button
              type="submit"
              onClick={handleSubmit}
              className="w-full bg-blue-500 text-white px-4 py-2 rounded-full hover:bg-blue-600 font-semibold"
              disabled={loading}
            >
              {loading ? "Đang đăng..." : "Đăng"}
            </button>
          </div>

          {/* Hiển thị bài viết từ database */}
          {loading && <div>Đang tải bài viết...</div>}
          {error && <div className="text-red-500">{error}</div>}
          {posts && posts.length === 0 && !loading && (
            <div>Chưa có bài viết nào.</div>
          )}
          {posts &&
            posts.map((post) => {
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
        </main>

        {/* Right Sidebar: Danh sách bạn bè */}
        <aside className="w-1/4 hidden lg:block p-4">
          <div className="bg-white rounded-lg shadow p-4 sticky top-24">
            <div className="font-semibold mb-3 text-lg text-blue-600 flex items-center gap-2">
              <svg
                width="22"
                height="22"
                fill="none"
                viewBox="0 0 24 24"
                className="inline-block mr-1 text-blue-500"
              >
                <circle
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="2"
                />
                <path
                  d="M8 15c0-1.657 2.686-2 4-2s4 .343 4 2v1H8v-1Z"
                  fill="currentColor"
                />
                <circle cx="12" cy="10" r="3" fill="currentColor" />
              </svg>
              Bạn bè
            </div>
            {loadingFriends ? (
              <div className="text-center text-gray-500 py-4">
                Đang tải danh sách bạn bè...
              </div>
            ) : friends.length === 0 ? (
              <div className="text-center text-gray-500 py-4">
                Chưa có bạn bè nào.
              </div>
            ) : (
              <ul className="space-y-3 max-h-[400px] overflow-y-auto">
                {friends.map((friend) => (
                  <li
                    key={friend.id}
                    className="flex items-center gap-3 hover:bg-gray-50 rounded px-2 py-2 cursor-pointer"
                  >
                    <img
                      src={
                        friend.avatar ||
                        "https://static.xx.fbcdn.net/rsrc.php/yo/r/iRmz9lCMBD2.ico"
                      }
                      alt="avatar"
                      className="w-10 h-10 rounded-full object-cover border"
                    />
                    <div>
                      <div className="font-semibold">{friend.fullName}</div>
                      <div className="text-xs text-gray-500">
                        {friend.gender === "MALE"
                          ? "Nam"
                          : friend.gender === "FEMALE"
                          ? "Nữ"
                          : "Khác"}
                        {friend.dateOfBirth && (
                          <>
                            {" "}
                            ·{" "}
                            {new Date(friend.dateOfBirth).toLocaleDateString()}
                          </>
                        )}
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </aside>
      </div>
    </div>
  );
}
