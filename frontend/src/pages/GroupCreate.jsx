import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function GroupCreate() {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [privacy, setPrivacy] = useState("PUBLIC");
  const [groupImage, setGroupImage] = useState("");
  const [coverImage, setCoverImage] = useState("");
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    await axios.post(
      "http://localhost:8080/api/v1/groups",
      { name, description, privacy, groupImage, coverImage },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setLoading(false);
    navigate("/groups");
  };

  return (
    <div className="max-w-xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6 text-blue-600">Tạo nhóm mới</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Tên nhóm"
          className="border rounded px-3 py-2 w-full"
          required
        />
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Mô tả nhóm"
          className="border rounded px-3 py-2 w-full"
        />
        <select
          value={privacy}
          onChange={(e) => setPrivacy(e.target.value)}
          className="border rounded px-3 py-2 w-full"
        >
          <option value="PUBLIC">Công khai</option>
          <option value="PRIVATE">Riêng tư</option>
        </select>
        <input
          value={groupImage}
          onChange={(e) => setGroupImage(e.target.value)}
          placeholder="Link ảnh nhóm"
          className="border rounded px-3 py-2 w-full"
        />
        <input
          value={coverImage}
          onChange={(e) => setCoverImage(e.target.value)}
          placeholder="Link ảnh cover"
          className="border rounded px-3 py-2 w-full"
        />
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded font-semibold w-full"
          disabled={loading}
        >
          {loading ? "Đang tạo..." : "Tạo nhóm"}
        </button>
      </form>
    </div>
  );
}