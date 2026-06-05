import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  replyComment,
  updateComment,
  deleteComment,
} from "../api/Comment/Action";
import {
  reactComment,
  unreactComment,
  getCommentReactions,
  getMyCommentReaction,
} from "../api/CommentLike/Action";

export default function CommentItem({
  comment,
  allComments = [],
  depth,
  reloadComments,
  currentUserId,
}) {
  const dispatch = useDispatch();
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
  const [reactionLoading, setReactionLoading] = useState(false);

  const { myReaction, reactions } = useSelector((state) => state.commentLike);

  const children = allComments.filter((c) => c.parentCommentId === comment.id);

  useEffect(() => {
    const fetchReactions = async () => {
      setReactionLoading(true);
      try {
        await Promise.all([
          dispatch(getMyCommentReaction(comment.id)),
          dispatch(getCommentReactions(comment.id)),
        ]);
      } catch (err) {
        console.error("Failed to fetch reactions:", err);
      }
      setReactionLoading(false);
    };
    fetchReactions();
  }, [comment.id, dispatch]);

  const handleReply = async (e) => {
    e.preventDefault();
    if (!replyContent.trim()) return;
    try {
      await dispatch(replyComment(comment.id, replyContent));
      setReplyContent("");
      setShowReply(false);
      reloadComments && reloadComments();
    } catch (err) {
      alert("Lỗi trả lời: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
  };

  const handleEdit = async (e) => {
    e.preventDefault();
    if (!editContent.trim()) return;
    try {
      await dispatch(updateComment(comment.id, editContent));
      setEditMode(false);
      reloadComments && reloadComments();
    } catch (err) {
      alert("Lỗi sửa: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("Bạn có chắc muốn xóa bình luận này?")) return;
    try {
      await dispatch(deleteComment(comment.id));
      reloadComments && reloadComments();
    } catch (err) {
      alert("Lỗi xóa: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
  };

  const handleReact = async (reactionType) => {
    if (!reactionType) return;
    try {
      await dispatch(reactComment(comment.id, reactionType));
      await Promise.all([
        dispatch(getMyCommentReaction(comment.id)),
        dispatch(getCommentReactions(comment.id)),
      ]);
    } catch (err) {
      alert("Lỗi cảm xúc: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
  };

  const handleUnreact = async () => {
    try {
      await dispatch(unreactComment(comment.id));
      await Promise.all([
        dispatch(getMyCommentReaction(comment.id)),
        dispatch(getCommentReactions(comment.id)),
      ]);
    } catch (err) {
      alert("Lỗi bỏ cảm xúc: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
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
            <span className="text-xs text-gray-400">
              {comment.createdDate
                ? new Date(comment.createdDate).toLocaleString()
                : ""}
            </span>
          </div>
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
                {reactions?.length || 0} cảm xúc
              </span>
            </div>
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
