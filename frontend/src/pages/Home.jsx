import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getPosts,
  createPost,
  uploadFiles,
  reactPost,
  unreactPost,
  getReactPostByMeAndPostId,
} from "../api/Post/Action";
import ThumbUpAltIcon from "@mui/icons-material/ThumbUpAlt";
import ThumbUpOffAltIcon from "@mui/icons-material/ThumbUpOffAlt";
import FavoriteIcon from "@mui/icons-material/Favorite";
import EmojiEmotionsIcon from "@mui/icons-material/EmojiEmotions";
import SentimentSatisfiedAltIcon from "@mui/icons-material/SentimentSatisfiedAlt";
import SentimentDissatisfiedIcon from "@mui/icons-material/SentimentDissatisfied";
import SentimentVeryDissatisfiedIcon from "@mui/icons-material/SentimentVeryDissatisfied";

const REACTION_ICONS = {
  LIKE: {
    filled: <ThumbUpAltIcon sx={{ color: "#1877f2", fontSize: 22 }} />,
    outline: <ThumbUpOffAltIcon sx={{ color: "#65676b", fontSize: 22 }} />,
    label: "Thích",
  },
  LOVE: {
    filled: <FavoriteIcon sx={{ color: "#f33e58", fontSize: 22 }} />,
    outline: <FavoriteIcon sx={{ color: "#65676b", fontSize: 22 }} />,
    label: "Yêu thích",
  },
  HAHA: {
    filled: <EmojiEmotionsIcon sx={{ color: "#f7b125", fontSize: 22 }} />,
    outline: <EmojiEmotionsIcon sx={{ color: "#65676b", fontSize: 22 }} />,
    label: "Haha",
  },
  WOW: {
    filled: (
      <SentimentSatisfiedAltIcon sx={{ color: "#f7b125", fontSize: 22 }} />
    ),
    outline: (
      <SentimentSatisfiedAltIcon sx={{ color: "#65676b", fontSize: 22 }} />
    ),
    label: "Wow",
  },
  SAD: {
    filled: (
      <SentimentDissatisfiedIcon sx={{ color: "#f7b125", fontSize: 22 }} />
    ),
    outline: (
      <SentimentDissatisfiedIcon sx={{ color: "#65676b", fontSize: 22 }} />
    ),
    label: "Buồn",
  },
  ANGRY: {
    filled: (
      <SentimentVeryDissatisfiedIcon sx={{ color: "#e9710f", fontSize: 22 }} />
    ),
    outline: (
      <SentimentVeryDissatisfiedIcon sx={{ color: "#65676b", fontSize: 22 }} />
    ),
    label: "Phẫn nộ",
  },
};

const REACTION_ORDER = ["LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"];
const BASE_FILE_URL = "http://localhost:8080/images/post-media/";

export default function Home() {
  const dispatch = useDispatch();
  const {
    posts,
    loading,
    error,
    createSuccess,
    uploadLoading,
    uploadedFiles,
    myReact,
    reactLoading,
    reactError,
  } = useSelector((state) => state.post);

  const [content, setContent] = useState("");
  const [media, setMedia] = useState([]);
  const fileInputRef = useRef();
  const [reactedPostId, setReactedPostId] = useState(null);
  const [showReactionMenu, setShowReactionMenu] = useState(false);

  useEffect(() => {
    dispatch(getPosts());
  }, [dispatch, createSuccess]);

  useEffect(() => {
    if (uploadedFiles && uploadedFiles.length > 0) {
      setMedia((prev) => [...prev, ...uploadedFiles]);
    }
  }, [uploadedFiles]);

  // Lấy trạng thái react của user với post khi hover vào nút like
  const handleShowReactionMenu = (postId) => {
    setReactedPostId(postId);
    setShowReactionMenu(true);
    dispatch(getReactPostByMeAndPostId(postId));
  };

  const handleHideReactionMenu = () => {
    setShowReactionMenu(false);
  };

  // Gọi API react
  const handleReact = (postId, reactionType) => {
    setReactedPostId(postId);
    setShowReactionMenu(false);
    dispatch(reactPost(postId, reactionType)).then(() => {
      dispatch(getReactPostByMeAndPostId(postId));
    });
  };

  // Gọi API unreact
  const handleUnreact = (postId) => {
    setReactedPostId(postId);
    dispatch(unreactPost(postId)).then(() => {
      dispatch(getReactPostByMeAndPostId(postId));
    });
  };

  const getFullMediaUrl = (mediaUrl) => {
    if (!mediaUrl) return "";
    if (mediaUrl.startsWith("http")) return mediaUrl;
    return BASE_FILE_URL + mediaUrl;
  };

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

  const handleFileChange = (e) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      dispatch(uploadFiles(files, "post-media"));
    }
  };

  const handleRemoveMedia = (idx) => {
    setMedia((prev) => prev.filter((_, i) => i !== idx));
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  return (
    <div className="bg-gray-100 min-h-screen">
      {/* Navbar */}
      <nav className="bg-white shadow px-4 py-2 flex items-center justify-between fixed w-full z-10">
        <div className="flex items-center space-x-2">
          <img
            src="https://static.xx.fbcdn.net/rsrc.php/yo/r/iRmz9lCMBD2.ico"
            alt="Logo"
            className="w-8 h-8"
          />
          <span className="text-blue-600 font-bold text-xl">Facebook</span>
        </div>
        <div className="flex items-center space-x-4">
          <input
            type="text"
            placeholder="Tìm kiếm trên Facebook"
            className="px-3 py-1 rounded-full bg-gray-100 border border-gray-300 focus:outline-none"
          />
          <img
            src="https://randomuser.me/api/portraits/men/32.jpg"
            alt="Avatar"
            className="w-8 h-8 rounded-full"
          />
        </div>
      </nav>

      {/* Main Content */}
      <div className="flex pt-16 max-w-7xl mx-auto">
        {/* Left Sidebar */}
        <aside className="w-1/4 hidden md:block p-4">
          {/* ...sidebar code... */}
        </aside>

        {/* Feed */}
        <main className="w-full md:w-2/4 p-4 space-y-4">
          {/* Create Post */}
          <form
            onSubmit={handleSubmit}
            className="bg-white rounded-lg shadow p-4 mb-4"
          >
            <input
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Bạn đang nghĩ gì?"
              className="w-full border rounded px-3 py-2 mb-2"
              required
            />
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
          </form>

          {/* Hiển thị bài viết từ database */}
          {loading && <div>Đang tải bài viết...</div>}
          {error && <div className="text-red-500">{error}</div>}
          {posts && posts.length === 0 && !loading && (
            <div>Chưa có bài viết nào.</div>
          )}
          {posts &&
            posts.map((post) => (
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
                <div className="flex justify-between text-gray-500 text-sm mt-2 items-center relative">
                  {/* Menu reaction */}
                  <div 
                    className="flex items-center gap-2 relative"
                    onMouseEnter={() => handleShowReactionMenu(post.id)}
                    onMouseLeave={handleHideReactionMenu}
                  >
                    <div 
                      className={`absolute bottom-full left-0 bg-white rounded-lg shadow-lg p-2 items-center gap-2 transition-opacity duration-200 mb-2 z-50 ${showReactionMenu && reactedPostId === post.id ? 'opacity-100 visible' : 'opacity-0 invisible'}`}
                      style={{
                        transform: `translateY(-4px) scale(${showReactionMenu && reactedPostId === post.id ? '1' : '0.95'})`,
                        transformOrigin: 'bottom left',
                        pointerEvents: showReactionMenu && reactedPostId === post.id ? 'auto' : 'none',
                        transition: 'all 0.2s ease-in-out'
                      }}
                    >
                      {REACTION_ORDER.map((type) => (
                        <button
                          key={type}
                          className="hover:scale-150 transform transition-all duration-200 p-1"
                          onClick={() => handleReact(post.id, type)}
                          title={REACTION_ICONS[type].label}
                        >
                          {REACTION_ICONS[type].filled}
                        </button>
                      ))}
                    </div>
                    {/* Hiển thị icon react của user */}
                    {myReact && reactedPostId === post.id && myReact.reactionType && (
                      <span className="flex items-center gap-1 font-bold" style={{ minWidth: 40 }}>
                        {REACTION_ICONS[myReact.reactionType].filled}
                        <span className="text-xs text-gray-700">
                          {REACTION_ICONS[myReact.reactionType].label}
                        </span>
                      </span>
                    )}
                    {/* Nút Like */}
                    <button
                      className={`flex items-center gap-1 px-2 py-1 rounded transition hover:bg-gray-100 ${myReact && reactedPostId === post.id && myReact.reactionType ? "text-blue-600 font-bold" : "text-gray-700"}`}
                      disabled={reactLoading && reactedPostId === post.id}
                      onClick={() =>
                        myReact && reactedPostId === post.id && myReact.reactionType
                          ? handleUnreact(post.id)
                          : handleReact(post.id, "LIKE")
                      }
                    >
                      {myReact && reactedPostId === post.id && myReact.reactionType
                        ? REACTION_ICONS[myReact.reactionType].filled
                        : REACTION_ICONS.LIKE.outline}
                      <span>
                        {myReact && reactedPostId === post.id && myReact.reactionType
                          ? REACTION_ICONS[myReact.reactionType].label
                          : REACTION_ICONS.LIKE.label}
                      </span>
                    </button>
                  </div>
                  <button className="hover:text-blue-500">💬 Bình luận</button>
                  <button className="hover:text-blue-500">↗️ Chia sẻ</button>
                </div>
                {/* Thông báo lỗi hoặc trạng thái */}
                {reactedPostId === post.id && (
                  <div className="mt-2 flex gap-2 items-center">
                    {reactError && (
                      <span className="text-red-500 ml-2">{reactError}</span>
                    )}
                  </div>
                )}
              </div>
            ))}
        </main>

        {/* Right Sidebar */}
        <aside className="w-1/4 hidden lg:block p-4">
          {/* ...right sidebar code... */}
        </aside>
      </div>
    </div>
  );
}
