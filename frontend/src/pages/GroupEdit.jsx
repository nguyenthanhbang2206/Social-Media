import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import { getGroupById, updateGroup } from "../api/Group/Action";
import AppLayout from "../components/layout/AppLayout";

export default function GroupEdit() {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(false);
  const { currentGroup, loading: groupLoading } = useSelector((state) => state.group);

  useEffect(() => {
    dispatch(getGroupById(id));
  }, [id, dispatch]);

  useEffect(() => {
    if (currentGroup) {
      setGroup(currentGroup);
    }
  }, [currentGroup]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await dispatch(
        updateGroup(id, {
          name: group.name,
          description: group.description,
          privacy: group.privacy,
          groupImage: group.groupImage,
          coverImage: group.coverImage,
        })
      );
      navigate(`/groups/${id}`);
    } catch (err) {
      alert("Không thể cập nhật nhóm");
    }
    setLoading(false);
  };

  if (groupLoading || !group) return <div>Đang tải...</div>;

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto">
        <h1 className="text-2xl font-bold mb-6 text-blue-600">Sửa nhóm</h1>
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Tên nhóm</label>
            <input
              value={group.name}
              onChange={(e) => setGroup({ ...group, name: e.target.value })}
              placeholder="Nhập tên nhóm"
              className="border rounded-lg px-4 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Mô tả nhóm</label>
            <textarea
              value={group.description}
              onChange={(e) => setGroup({ ...group, description: e.target.value })}
              placeholder="Nhập mô tả nhóm"
              className="border rounded-lg px-4 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows={4}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Quyền riêng tư</label>
            <select
              value={group.privacy}
              onChange={(e) => setGroup({ ...group, privacy: e.target.value })}
              className="border rounded-lg px-4 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="PUBLIC">Công khai</option>
              <option value="PRIVATE">Riêng tư</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Link ảnh nhóm</label>
            <input
              value={group.groupImage}
              onChange={(e) => setGroup({ ...group, groupImage: e.target.value })}
              placeholder="Nhập link ảnh nhóm"
              className="border rounded-lg px-4 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Link ảnh cover</label>
            <input
              value={group.coverImage}
              onChange={(e) => setGroup({ ...group, coverImage: e.target.value })}
              placeholder="Nhập link ảnh cover"
              className="border rounded-lg px-4 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button
            className="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold w-full hover:bg-blue-700"
            disabled={loading}
          >
            {loading ? "Đang lưu..." : "Lưu thay đổi"}
          </button>
        </form>
      </div>
    </AppLayout>
  );
}
