import React from "react";
import CommentList from "./CommentList";

export default function PostModal({ post, open, onClose }) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-xl max-h-[80vh] flex flex-col relative">
        <button
          className="absolute top-2 right-2 text-2xl text-gray-400 hover:text-red-500"
          onClick={onClose}
        >
          ×
        </button>
        {/* Nội dung bài viết */}
        <div className="p-4 border-b">
          {/* Hiển thị avatar, tên, ngày, nội dung, media... */}
          {/* ... */}
        </div>
        {/* Danh sách bình luận (scrollable) */}
        <div className="flex-1 overflow-y-auto p-4">
          <CommentList postId={post.id} />
        </div>
      </div>
    </div>
  );
}
