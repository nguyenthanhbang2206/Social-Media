import React, { useEffect, useState } from "react";
import CommentItem from "./CommentItem";
import { useSelector, useDispatch } from "react-redux";
import { getComments, createComment } from "../api/Comment/Action";

export default function CommentList({ postId }) {
  const dispatch = useDispatch();
  const [content, setContent] = useState("");
  const { comments, loading } = useSelector((state) => state.comment);
  const { user } = useSelector((state) => state.auth);

  useEffect(() => {
    if (postId) {
      dispatch(getComments(postId));
    }
  }, [postId, dispatch]);

  const rootComments = comments.filter((c) => !c.parentCommentId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;
    try {
      await dispatch(createComment(postId, content));
      setContent("");
      await dispatch(getComments(postId));
    } catch (err) {
      alert("Lỗi đăng bình luận: " + (err?.response?.data?.message || "Vui lòng thử lại"));
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="flex gap-2 mb-4">
        <input
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="flex-1 border rounded-full px-3 py-1 text-sm"
          placeholder="Viết bình luận..."
        />
        <button className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm">
          Gửi
        </button>
      </form>
      {loading ? (
        <div className="text-gray-500 text-sm">Đang tải bình luận...</div>
      ) : (
        rootComments.map((c) => (
          <CommentItem
            key={c.id}
            comment={c}
            allComments={comments}
            depth={0}
            reloadComments={() => dispatch(getComments(postId))}
            currentUserId={user?.id}
          />
        ))
      )}
    </div>
  );
}
