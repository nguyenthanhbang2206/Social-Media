import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getPosts,
  createPost,
  uploadFiles,
  reactPost,
  unreactPost,
  getReactionsOfPost,
} from "../api/Post/Action";
import { useNavigate } from "react-router-dom";
import api from "../config/api";
import PostModal from "../components/PostModal";
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

export default function Home() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { posts, loading, error, createSuccess, uploadLoading, uploadedFiles } =
    useSelector((state) => state.post);
  const [openPost, setOpenPost] = useState(null);
  const [content, setContent] = useState("");
  const [media, setMedia] = useState([]);
  const [showReactionModal, setShowReactionModal] = useState(false);
  const [modalReactions, setModalReactions] = useState([]);
  const fileInputRef = useRef();

  useEffect(() => {
    dispatch(getPosts());
  }, [dispatch, createSuccess]);

  useEffect(() => {
    if (uploadedFiles && uploadedFiles.length > 0) {
      setMedia((prev) => [...prev, ...uploadedFiles]);
    }
  }, [uploadedFiles]);

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
      }),
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
    await dispatch(getPosts());
  };

  // Gọi API unreact
  const handleUnreact = async (postId) => {
    console.log(postId);
    await dispatch(unreactPost(postId));
    await dispatch(getPosts());
  };

  const handleShare = async (postId) => {
    const shareContent = window.prompt("Nội dung chia sẻ (tùy chọn):", "");
    if (shareContent === null) return;
    try {
      await api.post(`/posts/${postId}/shares`, { shareContent });
      dispatch(getPosts());
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

  const [searchKeyword, setSearchKeyword] = useState("");
  const handleNavbarSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(searchKeyword.trim())}`);
    }
  };

  return (
    <AppLayout>
      {/* Feed */}
      <div className="max-w-2xl mx-auto space-y-4">
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

                <div className="flex items-center pt-2 border-t border-gray-200 space-x-4">
                  {/* Reaction Select giữ nguyên */}
                  <select
                    className="border rounded-full px-3 py-1 text-sm bg-gray-100"
                    value={myReactionType || ""}
                    onChange={async (e) => {
                      const value = e.target.value;
                      if (value) {
                        await handleReact(post.id, value);
                      }
                    }}
                  >
                    <option value="">Chọn cảm xúc</option>
                    {REACTION_ORDER.map((reactionType) => (
                      <option key={reactionType} value={reactionType}>
                        {REACTION_EMOJIS[reactionType]} {reactionType}
                      </option>
                    ))}
                  </select>
                  {/* Unreact Button nếu đã có reaction */}
                  {myReactionType && (
                    <button
                      className="ml-2 text-xs text-blue-600 hover:underline"
                      onClick={() => handleUnreact(post.id)}
                    >
                      Bỏ cảm xúc
                    </button>
                  )}
                  <button
                    className="flex items-center space-x-1 px-3 py-2 rounded-lg hover:bg-gray-100 text-gray-600"
                    onClick={() => setOpenPost(post)}
                  >
                    💬 Bình luận
                  </button>
                  {openPost && (
                    <PostModal
                      post={openPost}
                      open={!!openPost}
                      onClose={() => setOpenPost(null)}
                    />
                  )}
                  {/* Chia sẻ */}
                  <button
                    className="flex items-center space-x-1 px-3 py-2 rounded-lg hover:bg-gray-100 text-gray-600"
                    onClick={() => handleShare(post.id)}
                  >
                    <span>↗️</span>
                    <span className="text-sm">Chia sẻ</span>
                  </button>
                </div>
                {/* <CommentBox postId={post.id} /> */}
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
      </div>
    </AppLayout>
  );
}
