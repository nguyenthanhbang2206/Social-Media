import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

export default function GroupEdit() {
  const { id } = useParams();
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    fetchGroup();
  }, [id]);

  const fetchGroup = async () => {
    setLoading(true);
    const res = await axios.get(`http://localhost:8080/api/v1/groups/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setGroup(res.data.data);
    setLoading(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    await axios.put(
      `http://localhost:8080/api/v1/groups/${id}`,
      {
        name: group.name,
        description: group.description,
        privacy: group.privacy,
        groupImage: group.groupImage,
        coverImage: group.coverImage,
      },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setLoading(false);
    navigate(`/groups/${id}`);
  };

  if (!group) return <div>Đang tải...</div>;

  return (
    <div className="max-w-xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6 text-blue-600">Sửa nhóm</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          value={group.name}
          onChange={(e) => setGroup({ ...group, name: e.target.value })}
          placeholder="Tên nhóm"
          className="border rounded px-3 py-2 w-full"
          required
        />
        <textarea
          value={group.description}
          onChange={(e) => setGroup({ ...group, description: e.target.value })}
          placeholder="Mô tả nhóm"
          className="border rounded px-3 py-2 w-full"
        />
        <select
          value={group.privacy}
          onChange={(e) => setGroup({ ...group, privacy: e.target.value })}
          className="border rounded px-3 py-2 w-full"
        >
          <option value="PUBLIC">Công khai</option>
          <option value="PRIVATE">Riêng tư</option>
        </select>
        <input
          value={group.groupImage}
          onChange={(e) => setGroup({ ...group, groupImage: e.target.value })}
          placeholder="Link ảnh nhóm"
          className="border rounded px-3 py-2 w-full"
        />
        <input
          value={group.coverImage}
          onChange={(e) => setGroup({ ...group, coverImage: e.target.value })}
          placeholder="Link ảnh cover"
          className="border rounded px-3 py-2 w-full"
        />
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded font-semibold w-full"
          disabled={loading}
        >
          {loading ? "Đang lưu..." : "Lưu thay đổi"}
        </button>
      </form>
    </div>
  );
}
