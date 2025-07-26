import React, { useEffect, useState } from "react";
import axios from "axios";
import { useSelector } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";

export default function FriendRequest() {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const token = localStorage.getItem("token");
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState({}); // { [userId]: boolean }

  const location = useLocation();

  useEffect(() => {
    fetchRequests();
    // eslint-disable-next-line
  }, [token]);

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const res = await axios.get(
        "http://localhost:8080/api/v1/friend-requests/received",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setRequests(res.data.data || []);
    } catch (err) {
      setRequests([]);
    }
    setLoading(false);
  };

  const handleUserClick = (id) => {
    navigate("/users/" + id);
  };

  const handleAccept = async (senderId) => {
    setActionLoading((prev) => ({ ...prev, [senderId]: true }));
    try {
      await axios.put(
        `http://localhost:8080/api/v1/friend-requests/${senderId}/accept`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Xoá lời mời khỏi danh sách sau khi đồng ý
      setRequests((prev) => prev.filter((req) => req.sender?.id !== senderId));
    } catch (err) {
      // Có thể hiển thị thông báo lỗi nếu muốn
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: false }));
  };

  const handleRefuse = async (senderId) => {
    setActionLoading((prev) => ({ ...prev, [senderId]: true }));
    try {
      await axios.put(
        `http://localhost:8080/api/v1/friend-requests/${senderId}/refuse`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // Xoá lời mời khỏi danh sách sau khi từ chối
      setRequests((prev) => prev.filter((req) => req.sender?.id !== senderId));
    } catch (err) {
      // Có thể hiển thị thông báo lỗi nếu muốn
    }
    setActionLoading((prev) => ({ ...prev, [senderId]: false }));
  };

  return (
    <div className="flex max-w-5xl mx-auto pt-8">
      {/* Left Sidebar */}
      <aside className="w-1/4 pr-4">
        <div className="bg-white rounded-lg shadow p-4 sticky top-24">
          <div className="font-bold text-lg mb-4 text-blue-600">Bạn bè</div>
          <ul className="space-y-2">
            <li>
              <Link
                to="/friend-list"
                className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                  location.pathname === "/friend-list"
                    ? "bg-blue-100 text-blue-700"
                    : "text-gray-700"
                }`}
              >
                Danh sách bạn bè
              </Link>
            </li>
            <li>
              <Link
                to="/friend-requests"
                className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                  location.pathname === "/friend-requests"
                    ? "bg-blue-100 text-blue-700"
                    : "text-gray-700"
                }`}
              >
                Lời mời kết bạn
              </Link>
            </li>
            <li>
              <Link
                to="/suggestion-friends"
                className={`block px-3 py-2 rounded hover:bg-blue-50 font-medium ${
                  location.pathname === "/suggestion-friends"
                    ? "bg-blue-100 text-blue-700"
                    : "text-gray-700"
                }`}
              >
                Bạn bè gợi ý
              </Link>
            </li>
          </ul>
        </div>
      </aside>

      {/* Main Content */}
      <main className="w-3/4">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold mb-4 text-blue-700">
            Lời mời kết bạn đã nhận
          </h2>
          {loading ? (
            <div className="text-center text-gray-500 py-8">
              Đang tải lời mời kết bạn...
            </div>
          ) : requests.length === 0 ? (
            <div className="text-center text-gray-500 py-8">
              Không có lời mời kết bạn nào.
            </div>
          ) : (
            <ul>
              {requests.map((req) => (
                <li
                  key={req.id}
                  className="flex items-center gap-3 px-4 py-2 hover:bg-gray-100 rounded"
                >
                  <img
                    src={
                      req.sender?.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-10 h-10 rounded-full object-cover"
                    onClick={() => handleUserClick(req.sender?.id)}
                    style={{ cursor: "pointer" }}
                  />
                  <div className="flex-1 min-w-0">
                    <div
                      className="font-semibold cursor-pointer"
                      onClick={() => handleUserClick(req.sender?.id)}
                    >
                      {req.sender?.fullName}
                    </div>
                    <div className="text-xs text-gray-500">
                      {req.sender?.gender === "MALE"
                        ? "Nam"
                        : req.sender?.gender === "FEMALE"
                        ? "Nữ"
                        : "Khác"}
                      {req.sender?.dateOfBirth && (
                        <>
                          {" "}
                          ·{" "}
                          {new Date(
                            req.sender.dateOfBirth
                          ).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button
                      className="px-3 py-1 rounded bg-blue-500 text-white font-semibold hover:bg-blue-600 transition"
                      disabled={actionLoading[req.sender?.id]}
                      onClick={() => handleAccept(req.sender?.id)}
                    >
                      {actionLoading[req.sender?.id]
                        ? "Đang xử lý..."
                        : "Đồng ý"}
                    </button>
                    <button
                      className="px-3 py-1 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 transition"
                      disabled={actionLoading[req.sender?.id]}
                      onClick={() => handleRefuse(req.sender?.id)}
                    >
                      Từ chối
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </main>
    </div>
  );
}
