import React, { useEffect, useState } from "react";
import api from "../config/api";

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get("/admin/users");
      setUsers(res.data.data || []);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được danh sách người dùng");
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleStatus = async (userId) => {
    try {
      await api.put(`/admin/users/${userId}`);
      fetchUsers();
    } catch (err) {
      alert(err?.response?.data?.message || "Không đổi được trạng thái");
    }
  };

  return (
    <div className="max-w-5xl mx-auto py-8">
      <h2 className="text-2xl font-bold mb-4 text-blue-700">Quản lý người dùng</h2>
      {loading ? (
        <div className="text-gray-500">Đang tải...</div>
      ) : error ? (
        <div className="text-red-500">{error}</div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-100 text-gray-700">
              <tr>
                <th className="text-left px-4 py-2">ID</th>
                <th className="text-left px-4 py-2">Họ tên</th>
                <th className="text-left px-4 py-2">Email</th>
                <th className="text-left px-4 py-2">Vai trò</th>
                <th className="text-left px-4 py-2">Trạng thái</th>
                <th className="text-left px-4 py-2">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id} className="border-t">
                  <td className="px-4 py-2">{user.id}</td>
                  <td className="px-4 py-2">{user.fullName || "-"}</td>
                  <td className="px-4 py-2">{user.email}</td>
                  <td className="px-4 py-2">{user.role}</td>
                  <td className="px-4 py-2">
                    {user.active ? (
                      <span className="text-green-600 font-semibold">Active</span>
                    ) : (
                      <span className="text-gray-500">Inactive</span>
                    )}
                  </td>
                  <td className="px-4 py-2">
                    <button
                      className="px-3 py-1 rounded bg-blue-500 text-white hover:bg-blue-600"
                      onClick={() => handleToggleStatus(user.id)}
                    >
                      {user.active ? "Vô hiệu hóa" : "Kích hoạt"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}