import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

export default function GroupDetail() {
  const { id } = useParams();
  const [group, setGroup] = useState(null);
  const [members, setMembers] = useState([]);
  const [pendingMembers, setPendingMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [tab, setTab] = useState("members");
  const token = localStorage.getItem("token");
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  // Trạng thái của user trong nhóm
  const [membershipStatus, setMembershipStatus] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isCreator, setIsCreator] = useState(false);

  useEffect(() => {
    fetchGroup();
    fetchMembers();
    fetchMembershipStatus();
    // eslint-disable-next-line
  }, [id]);

  useEffect(() => {
    if (group?.privacy === "PRIVATE" && isAdmin) {
      fetchPendingMembers();
    }
    // eslint-disable-next-line
  }, [group?.privacy, isAdmin]);

  useEffect(() => {
    // Xác định user login có phải creator không
    if (group && group.creator && user) {
      setIsCreator(String(group.creator.id) === String(user.id));
    } else {
      setIsCreator(false);
    }
  }, [group, user]);

  const fetchGroup = async () => {
    setLoading(true);
    const res = await axios.get(`http://localhost:8080/api/v1/groups/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setGroup(res.data.data);
    setLoading(false);
  };

  const fetchMembers = async () => {
    const res = await axios.get(
      `http://localhost:8080/api/v1/groups/${id}/members`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setMembers(res.data.data || []);
    // Kiểm tra quyền admin
    const me = res.data.data.find(
      (m) => m.user && String(m.user.id) === String(user?.id)
    );
    setIsAdmin(!!me && me.role === "ADMIN");
  };

  const fetchPendingMembers = async () => {
    const res = await axios.post(
      `http://localhost:8080/api/v1/groups/${id}/pending-members`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setPendingMembers(res.data.data || []);
  };

  // Lấy trạng thái thành viên của user hiện tại
  const fetchMembershipStatus = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/v1/groups/${id}/members/status`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMembershipStatus(res.data.data); // "PENDING", "APPROVED", "REJECTED", "LEFT", null
    } catch (err) {
      setMembershipStatus(null);
    }
  };

  // Xử lý tham gia nhóm
  const handleJoin = async () => {
    setActionLoading(true);
    await axios.post(
      `http://localhost:8080/api/v1/groups/${id}/join`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchMembershipStatus();
    await fetchMembers();
    setActionLoading(false);
  };

  // Xử lý rời nhóm
  const handleLeave = async () => {
    setActionLoading(true);
    await axios.post(
      `http://localhost:8080/api/v1/groups/${id}/left`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchMembershipStatus();
    await fetchMembers();
    setActionLoading(false);
  };

  // Xóa nhóm (chỉ creator)
  const handleDeleteGroup = async () => {
    if (!window.confirm("Bạn có chắc muốn xóa nhóm này?")) return;
    await axios.delete(`http://localhost:8080/api/v1/groups/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    navigate("/groups");
  };

  // Duyệt thành viên (chỉ admin)
  const handleApprove = async (userId) => {
    await axios.post(
      `http://localhost:8080/api/v1/groups/${id}/members/${userId}/approve`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchMembers();
    await fetchPendingMembers();
  };

  // Xóa thành viên (chỉ admin)
  const handleDeleteMember = async (userId) => {
    if (!window.confirm("Xóa thành viên này khỏi nhóm?")) return;
    await axios.delete(
      `http://localhost:8080/api/v1/groups/${id}/members/${userId}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchMembers();
    await fetchPendingMembers();
  };

  // Đổi vai trò thành viên (chỉ creator)
  const handleChangeRole = async (userId, role) => {
    await axios.put(
      `http://localhost:8080/api/v1/groups/${id}/members/${userId}/role`,
      { role },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchMembers();
  };
  const handleRejectMember = async (userId) => {
    if (!window.confirm("Bạn có chắc muốn từ chối thành viên này?")) return;
    await axios.post(
      `http://localhost:8080/api/v1/groups/${id}/members/${userId}/reject`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    await fetchPendingMembers();
    await fetchMembers();
  };
  if (loading || !group) return <div>Đang tải nhóm...</div>;

  // Xác định trạng thái hiển thị nút thao tác
  let actionButton = null;
  if (!membershipStatus) {
    actionButton = (
      <button
        className="bg-blue-500 text-white px-4 py-2 rounded font-semibold"
        onClick={handleJoin}
        disabled={actionLoading}
      >
        {actionLoading ? "Đang xử lý..." : "Tham gia nhóm"}
      </button>
    );
  } else if (membershipStatus === "PENDING") {
    actionButton = (
      <button
        className="bg-gray-300 text-gray-600 px-4 py-2 rounded font-semibold cursor-not-allowed"
        disabled
      >
        Đang chờ duyệt
      </button>
    );
  } else if (membershipStatus === "APPROVED" || membershipStatus === "MEMBER") {
    actionButton = (
      <button
        className="bg-gray-200 text-gray-700 px-4 py-2 rounded font-semibold"
        onClick={handleLeave}
        disabled={actionLoading}
      >
        Rời nhóm
      </button>
    );
  } else if (membershipStatus === "REJECTED") {
    actionButton = (
      <span className="bg-red-100 text-red-600 px-4 py-2 rounded font-semibold">
        Yêu cầu tham gia bị từ chối
      </span>
    );
  } else if (membershipStatus === "LEFT") {
    actionButton = (
      <button
        className="bg-blue-500 text-white px-4 py-2 rounded font-semibold"
        onClick={handleJoin}
        disabled={actionLoading}
      >
        {actionLoading ? "Đang xử lý..." : "Tham gia lại"}
      </button>
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-8">
      <div className="flex gap-6 mb-6">
        <img
          src={
            group.groupImage ||
            "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png"
          }
          alt="group"
          className="w-32 h-32 object-cover rounded-lg"
          onError={(e) => {
            e.target.onerror = null;
            e.target.src =
              "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png";
          }}
        />
        <div>
          <h1 className="text-2xl font-bold text-blue-600">{group.name}</h1>
          <div className="text-gray-600 mb-2">{group.description}</div>
          <div className="text-xs text-gray-500 mb-2">
            Quyền riêng tư: {group.privacy}
          </div>
          <div className="flex gap-2">
            {actionButton}
            {isCreator && (
              <>
                <button
                  className="bg-yellow-500 text-white px-4 py-2 rounded font-semibold"
                  onClick={() => navigate(`/groups/${group.id}/edit`)}
                >
                  Sửa nhóm
                </button>
                <button
                  className="bg-red-500 text-white px-4 py-2 rounded font-semibold"
                  onClick={handleDeleteGroup}
                >
                  Xóa nhóm
                </button>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Tabs cho admin nhóm private */}
      {isAdmin && group.privacy === "PRIVATE" && (
        <div className="mb-4 flex gap-4">
          <button
            className={`px-4 py-2 rounded font-semibold ${
              tab === "members"
                ? "bg-blue-500 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => setTab("members")}
          >
            Thành viên nhóm
          </button>
          <button
            className={`px-4 py-2 rounded font-semibold ${
              tab === "pending"
                ? "bg-blue-500 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => setTab("pending")}
          >
            Thành viên chờ duyệt
          </button>
        </div>
      )}

      {/* Tab Thành viên nhóm */}
      {tab === "members" && (
        <>
          <h2 className="font-semibold mb-2 text-lg">Thành viên nhóm</h2>
          <div className="bg-white rounded-lg shadow p-4 mb-6">
            <ul>
              {members.map((m) => (
                <li
                  key={m.user?.id}
                  className="flex items-center gap-3 py-2 border-b"
                >
                  <img
                    src={m.user?.avatar || "/default-avatar.png"}
                    alt="avatar"
                    className="w-10 h-10 rounded-full object-cover"
                  />
                  <div className="flex-1">
                    <div className="font-semibold">{m.user?.fullName}</div>
                    <div className="text-xs text-gray-500">
                      {m.role} · {m.status}
                    </div>
                  </div>
                  {/* Chỉ creator mới được đổi role và xóa thành viên */}
                  {isCreator && m.user?.id !== user?.id && (
                    <div className="flex gap-2">
                      <select
                        value={m.role}
                        onChange={(e) =>
                          handleChangeRole(m.user?.id, e.target.value)
                        }
                        className="border rounded px-2 py-1 text-sm"
                      >
                        <option value="MEMBER">Thành viên</option>
                        <option value="ADMIN">Quản trị viên</option>
                      </select>
                      <button
                        className="px-3 py-1 rounded bg-red-500 text-white font-semibold"
                        onClick={() => handleDeleteMember(m.user?.id)}
                      >
                        Xóa
                      </button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          </div>
        </>
      )}

      {/* Tab Thành viên chờ duyệt */}
      {tab === "pending" && (
        <>
          <h2 className="font-semibold mb-2 text-lg">Thành viên chờ duyệt</h2>
          <div className="bg-white rounded-lg shadow p-4 mb-6">
            <ul>
              {pendingMembers.length === 0 ? (
                <div className="text-gray-500 italic">
                  Không có thành viên chờ duyệt.
                </div>
              ) : (
                pendingMembers.map((m) => (
                  <li
                    key={m.user?.id}
                    className="flex items-center gap-3 py-2 border-b"
                  >
                    <img
                      src={m.user?.avatar || "/default-avatar.png"}
                      alt="avatar"
                      className="w-10 h-10 rounded-full object-cover"
                    />
                    <div className="flex-1">
                      <div className="font-semibold">{m.user?.fullName}</div>
                      <div className="text-xs text-gray-500">
                        {m.role} · {m.status}
                      </div>
                    </div>
                    {/* Chỉ admin mới được duyệt hoặc xóa thành viên chờ duyệt */}
                    {isAdmin && (
                      <div className="flex gap-2">
                        <button
                          className="px-3 py-1 rounded bg-blue-500 text-white font-semibold"
                          onClick={() => handleApprove(m.user?.id)}
                        >
                          Duyệt
                        </button>
                        <button
                          className="px-3 py-1 rounded bg-red-500 text-white font-semibold"
                          onClick={() => handleRejectMember(m.user?.id)}
                        >
                          Xóa
                        </button>
                      </div>
                    )}
                  </li>
                ))
              )}
            </ul>
          </div>
        </>
      )}
    </div>
  );
}
