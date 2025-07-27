import React, { useEffect, useState } from "react";
import CommentItem from "./CommentItem";
import axios from "axios";

export default function CommentList({ postId }) {
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState(""); // input cho bình luận gốc
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "null");

  // Fetch all comments for the post
  const fetchComments = () => {
    axios
      .get(`http://localhost:8080/api/v1/posts/${postId}/comments`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setComments(res.data.data || []));
  };

  useEffect(() => {
    fetchComments();
    // eslint-disable-next-line
  }, [postId, token]);

  // Lọc comment gốc (parentCommentId == null)
  const rootComments = comments.filter((c) => !c.parentCommentId);

  // Thêm bình luận gốc
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;
    await axios.post(
      `http://localhost:8080/api/v1/posts/${postId}/comments`,
      { content },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setContent("");
    fetchComments();
  };

  return (
    <div>
      {/* Form nhập bình luận gốc */}
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
      {/* Danh sách comment gốc */}
      {rootComments.map((c) => (
        <CommentItem
          key={c.id}
          comment={c}
          allComments={comments}
          depth={0}
          reloadComments={fetchComments}
          currentUserId={user?.id}
        />
      ))}
    </div>
  );
}
