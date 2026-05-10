import React, { useEffect, useState } from "react";
import axios from "axios";

export default function CommentItem({
  comment,
  allComments = [],
  depth,
  reloadComments,
  currentUserId, // truyền vào nếu muốn kiểm tra quyền sửa/xóa
}) {
  const REACTION_ORDER = ["LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"];
  const REACTION_EMOJIS = {
    LIKE: "👍",
    LOVE: "❤️",
    HAHA: "😂",
    WOW: "😮",
    SAD: "😢",
    ANGRY: "😠",
  };
  const [showReply, setShowReply] = useState(false);
  const [replyContent, setReplyContent] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);
  const [myReaction, setMyReaction] = useState(null);
  const [reactions, setReactions] = useState([]);
  const [reactionLoading, setReactionLoading] = useState(false);
  const token = localStorage.getItem("token");

  // Lấy replies của comment này
  const children = allComments.filter((c) => c.parentCommentId === comment.id);

  const fetchReactions = async () => {
    setReactionLoading(true);
    try {
      const [meRes, listRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/v1/comments/${comment.id}/me`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        axios.get(`http://localhost:8080/api/v1/comments/${comment.id}/reactions`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);
      setMyReaction(meRes.data.data || null);
      setReactions(listRes.data.data || []);
    } catch (err) {
      setMyReaction(null);
      setReactions([]);
    }
    setReactionLoading(false);
  };

  useEffect(() => {
    fetchReactions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [comment.id]);

  // Gửi reply
  const handleReply = async (e) => {
    e.preventDefault();
    if (!replyContent.trim()) return;
    await axios.post(
      `http://localhost:8080/api/v1/comments/${comment.id}/reply`,
      { content: replyContent },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setReplyContent("");
    setShowReply(false);
    reloadComments && reloadComments();
  };

  // Sửa comment
  const handleEdit = async (e) => {
    e.preventDefault();
    if (!editContent.trim()) return;
    await axios.put(
      `http://localhost:8080/api/v1/comments/${comment.id}`,
      { content: editContent },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setEditMode(false);
    reloadComments && reloadComments();
  };

  // Xóa comment
  const handleDelete = async () => {
    if (!window.confirm("Bạn có chắc muốn xóa bình luận này?")) return;
    await axios.delete(`http://localhost:8080/api/v1/comments/${comment.id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    reloadComments && reloadComments();
  };

  const handleReact = async (reactionType) => {
    if (!reactionType) return;
    await axios.post(
      `http://localhost:8080/api/v1/comments/${comment.id}/react`,
      { reactionType },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    fetchReactions();
  };

  const handleUnreact = async () => {
    await axios.delete(
      `http://localhost:8080/api/v1/comments/${comment.id}/un-react`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    fetchReactions();
  };

  return (
    <div
      style={{
        marginLeft: depth * 24,
        maxWidth: 600,
        marginTop: 8,
      }}
    >
      <div className="flex items-start gap-2">
        <img
          src={comment.user?.avatar || "/default.png"}
          alt=""
          className="w-9 h-9 rounded-full object-cover border"
        />
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <span className="font-semibold text-sm text-gray-800">
              {comment.user?.fullName}
            </span>
            {/* Thời gian */}
            <span className="text-xs text-gray-400">
              {comment.createdDate
                ? new Date(comment.createdDate).toLocaleString()
                : ""}
            </span>
          </div>
          {/* Nội dung hoặc input sửa */}
          {editMode ? (
            <form className="flex gap-2 mt-1" onSubmit={handleEdit}>
              <input
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                className="flex-1 border rounded-full px-3 py-1 text-sm"
                autoFocus
              />
              <button className="bg-blue-500 text-white px-3 py-1 rounded-full text-sm font-semibold">
                Lưu
              </button>
              <button
                type="button"
                className="bg-gray-200 px-3 py-1 rounded-full text-sm font-semibold"
                onClick={() => {
                  setEditMode(false);
                  setEditContent(comment.content);
                }}
              >
                Hủy
              </button>
            </form>
          ) : (
            <div className="bg-gray-100 text-gray-900 px-4 py-2 rounded-2xl text-sm">
              {comment.content}
            </div>
          )}
          <div className="flex gap-3 mt-1 text-xs text-gray-500">
            <button
              className="hover:underline font-medium"
              onClick={() => setShowReply((v) => !v)}
            >
              Trả lời
            </button>
            <div className="flex items-center gap-2">
              <select
                className="border rounded-full px-2 py-0.5 text-xs bg-gray-100"
                value={myReaction?.reactionType || ""}
                onChange={(e) => handleReact(e.target.value)}
                disabled={reactionLoading}
              >
                <option value="">Cảm xúc</option>
                {REACTION_ORDER.map((reactionType) => (
                  <option key={reactionType} value={reactionType}>
                    {REACTION_EMOJIS[reactionType]} {reactionType}
                  </option>
                ))}
              </select>
              {myReaction?.reactionType && (
                <button
                  className="hover:underline font-medium text-blue-600"
                  onClick={handleUnreact}
                  disabled={reactionLoading}
                >
                  Bỏ cảm xúc
                </button>
              )}
              <span className="text-xs text-gray-400">
                {reactions.length} cảm xúc
              </span>
            </div>
            {/* Chỉ cho phép sửa/xóa nếu là chủ comment */}
            {(currentUserId === comment.user?.id ||
              comment.user?.id === currentUserId) && (
              <>
                <button
                  className="hover:underline font-medium"
                  onClick={() => setEditMode(true)}
                >
                  Sửa
                </button>
                <button
                  className="hover:underline font-medium text-red-500"
                  onClick={handleDelete}
                >
                  Xóa
                </button>
              </>
            )}
          </div>
          {showReply && (
            <form className="flex gap-2 mt-2" onSubmit={handleReply}>
              <input
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                className="flex-1 border rounded-full px-3 py-1 text-sm"
                placeholder="Nhập trả lời..."
                autoFocus
              />
              <button className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm font-semibold">
                Gửi
              </button>
            </form>
          )}
        </div>
      </div>
      {/* Đệ quy hiển thị replies */}
      {children.length > 0 && (
        <div className="mt-2">
          {children.map((r) => (
            <CommentItem
              key={r.id}
              comment={r}
              allComments={allComments}
              depth={depth + 1}
              reloadComments={reloadComments}
              currentUserId={currentUserId}
            />
          ))}
        </div>
      )}
    </div>
  );
}
