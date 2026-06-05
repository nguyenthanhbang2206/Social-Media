import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { getAdminUsers, updateAdminUser } from "../api/AdminUser/Action";
import AppLayout from "../components/layout/AppLayout";

export default function AdminDashboard() {
  const dispatch = useDispatch();
  const { users, loading } = useSelector((state) => state.adminUser);

  useEffect(() => {
    dispatch(getAdminUsers());
  }, [dispatch]);

  const handleToggleStatus = async (userId) => {
    try {
      await dispatch(updateAdminUser(userId));
      dispatch(getAdminUsers());
    } catch (err) {
      alert(err?.response?.data?.message || "Không đổi được trạng thái");
    }
  };

  return (
    <AppLayout showRightSidebar={false}>
      <div className="max-w-6xl mx-auto">
        <h2 className="text-2xl font-bold mb-6 text-blue-700">
          Quản lý người dùng
        </h2>
        {loading ? (
          <div className="text-gray-500">Đang tải...</div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="text-left px-4 py-3">ID</th>
                  <th className="text-left px-4 py-3">Họ tên</th>
                  <th className="text-left px-4 py-3">Email</th>
                  <th className="text-left px-4 py-3">Vai trò</th>
                  <th className="text-left px-4 py-3">Trạng thái</th>
                  <th className="text-left px-4 py-3">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id} className="border-t hover:bg-gray-50">
                    <td className="px-4 py-3">{user.id}</td>
                    <td className="px-4 py-3">{user.fullName || "-"}</td>
                    <td className="px-4 py-3">{user.email}</td>
                    <td className="px-4 py-3">{user.role}</td>
                    <td className="px-4 py-3">
                      {user.active ? (
                        <span className="text-green-600 font-semibold">
                          Active
                        </span>
                      ) : (
                        <span className="text-gray-500">Inactive</span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <button
                        className="px-3 py-1 rounded bg-blue-600 text-white hover:bg-blue-700"
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
    </AppLayout>
  );
}
