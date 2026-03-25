import React, { useEffect, useState, useRef } from "react";
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
  const [groupPosts, setGroupPosts] = useState([]);
  const [pendingPosts, setPendingPosts] = useState([]);
  const [postContent, setPostContent] = useState("");
  const [postMedia, setPostMedia] = useState([]);
  const fileInputRef = useRef(null);

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
    // fetchGroupPosts();
    // eslint-disable-next-line
  }, [id]);
  useEffect(() => {
    if (tab === "posts") {
      fetchGroupPosts();
    }
    if (tab === "pendingPosts" && isAdmin && group?.privacy === "PRIVATE") {
      fetchPendingPosts();
    }
    if (tab === "pending" && isAdmin && group?.privacy === "PRIVATE") {
      fetchPendingMembers();
    }
  }, [tab, isAdmin, group?.privacy]);

  useEffect(() => {
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

  const fetchMembershipStatus = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/v1/groups/${id}/members/status`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMembershipStatus(res.data.data);
    } catch (err) {
      setMembershipStatus(null);
    }
  };

  // Lấy bài viết đã duyệt
  const fetchGroupPosts = async () => {
    try{
      const res = await axios.get(
      `http://localhost:8080/api/v1/groups/${id}/posts`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setGroupPosts(res.data.data || []);
    }catch(err){
      alert(
        "Lỗi: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  // Lấy bài viết chờ duyệt (chỉ admin)
  const fetchPendingPosts = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/v1/groups/${id}/posts/pending`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setPendingPosts(res.data.data || []);
    } catch (err) {
      alert(
        "Lỗi: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  // Tạo bài viết mới trong group
  const handleCreatePost = async (e) => {
    e.preventDefault();
    try {
      await axios.post(
        `http://localhost:8080/api/v1/groups/${id}/posts`,
        {
          content: postContent,
          postType: "GROUP_POST",
          media: postMedia,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setPostContent("");
      setPostMedia([]);
      if (fileInputRef.current) fileInputRef.current.value = "";
      await fetchGroupPosts();
      if (isAdmin) await fetchPendingPosts();
    } catch (err) {
      alert(
        "Lỗi đăng bài: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  // Xử lý upload media (giả lập, bạn cần xử lý upload thực tế)
  const handleMediaChange = (e) => {
    const files = Array.from(e.target.files);
    setPostMedia(
      files.map((file, idx) => ({
        mediaUrl: URL.createObjectURL(file), // Chỉ demo, cần upload lên server để lấy url thực tế
        mediaType: file.type.startsWith("image") ? "IMAGE" : "VIDEO",
        uploadOrder: idx,
      }))
    );
  };

  // Duyệt bài viết
  const handleApprovePost = async (postId) => {
    try {
      await axios.put(
        `http://localhost:8080/api/v1/groups/${id}/posts/${postId}/approve`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchPendingPosts();
      await fetchGroupPosts();
    } catch (err) {
      alert(
        "Lỗi duyệt bài viết: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };
  // Các hàm thành viên giữ nguyên...

  const handleJoin = async () => {
    setActionLoading(true);
    try {
      await axios.post(
        `http://localhost:8080/api/v1/groups/${id}/join`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchMembershipStatus();
      await fetchMembers();
    } catch (err) {
      alert(
        "Lỗi tham gia nhóm: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
    setActionLoading(false);
  };

  const handleLeave = async () => {
    setActionLoading(true);
    try {
      await axios.post(
        `http://localhost:8080/api/v1/groups/${id}/left`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchMembershipStatus();
      await fetchMembers();
    } catch (err) {
      alert(
        "Lỗi rời nhóm: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
    setActionLoading(false);
  };

  const handleDeleteGroup = async () => {
    if (!window.confirm("Bạn có chắc muốn xóa nhóm này?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/v1/groups/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      navigate("/groups");
    } catch (err) {
      alert(
        "Lỗi xóa nhóm: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleApprove = async (userId) => {
    try {
      await axios.post(
        `http://localhost:8080/api/v1/groups/${id}/members/${userId}/approve`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchMembers();
      await fetchPendingMembers();
    } catch (err) {
      alert(
        "Lỗi duyệt thành viên: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleDeleteMember = async (userId) => {
    if (!window.confirm("Xóa thành viên này khỏi nhóm?")) return;
    try {
      await axios.delete(
        `http://localhost:8080/api/v1/groups/${id}/members/${userId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchMembers();
      await fetchPendingMembers();
    } catch (err) {
      alert(
        "Lỗi xóa thành viên: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };
  const handleChangeRole = async (userId, role) => {
    try {
      await axios.put(
        `http://localhost:8080/api/v1/groups/${id}/members/${userId}/role`,
        { role },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchMembers();
    } catch (err) {
      alert(
        "Lỗi đổi vai trò: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleRejectMember = async (userId) => {
    if (!window.confirm("Bạn có chắc muốn từ chối thành viên này?")) return;
    try {
      await axios.post(
        `http://localhost:8080/api/v1/groups/${id}/members/${userId}/reject`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      await fetchPendingMembers();
      await fetchMembers();
    } catch (err) {
      alert(
        "Lỗi từ chối thành viên: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  if (loading || !group) return <div>Đang tải nhóm...</div>;

  // Xác định trạng thái hiển thị nút thao tác
  let actionButton = null;
  if (
    !membershipStatus ||
    membershipStatus === "REJECTED" ||
    membershipStatus === "LEFT"
  ) {
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
  }

  // Tabs cho group: Thành viên, Bài viết, Chờ duyệt (admin)
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

      {/* Tabs */}
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
            tab === "posts"
              ? "bg-blue-500 text-white"
              : "bg-gray-100 text-gray-700"
          }`}
          onClick={() => setTab("posts")}
        >
          Bài viết nhóm
        </button>
        {isAdmin && group.privacy === "PRIVATE" && (
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
        )}
        {isAdmin && group.privacy === "PRIVATE" && (
          <button
            className={`px-4 py-2 rounded font-semibold ${
              tab === "pendingPosts"
                ? "bg-blue-500 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => setTab("pendingPosts")}
          >
            Bài viết chờ duyệt
          </button>
        )}
      </div>

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

      {/* Tab Bài viết nhóm */}
      {tab === "posts" && (
        <>
          <h2 className="font-semibold mb-2 text-lg">Bài viết nhóm</h2>
          {/* Chỉ thành viên đã duyệt mới được tạo bài viết */}
          {(membershipStatus === "APPROVED" ||
            membershipStatus === "MEMBER") && (
            <form onSubmit={handleCreatePost} className="mb-6 space-y-2">
              <textarea
                value={postContent}
                onChange={(e) => setPostContent(e.target.value)}
                placeholder="Nội dung bài viết..."
                className="border rounded px-3 py-2 w-full"
                required
              />
              <input
                type="file"
                multiple
                ref={fileInputRef}
                onChange={handleMediaChange}
                className="border rounded px-3 py-2 w-full"
                accept="image/*,video/*"
              />
              <button
                className="bg-blue-500 text-white px-4 py-2 rounded font-semibold"
                type="submit"
              >
                Đăng bài
              </button>
            </form>
          )}
          <div className="bg-white rounded-lg shadow p-4 mb-6">
            {groupPosts.length === 0 ? (
              <div className="text-gray-500 italic">Chưa có bài viết nào.</div>
            ) : (
              groupPosts.map((post) => (
                <div key={post.id} className="mb-4 border-b pb-2">
                  <div className="font-bold">{post.content}</div>
                  {/* Hiển thị media nếu có */}
                  {post.media && post.media.length > 0 && (
                    <div className="flex gap-2 mt-2">
                      {post.media.map((m, idx) =>
                        m.mediaType === "IMAGE" ? (
                          <img
                            key={idx}
                            src={m.mediaUrl}
                            alt="media"
                            className="w-24 h-24 object-cover rounded"
                          />
                        ) : (
                          <video
                            key={idx}
                            src={m.mediaUrl}
                            controls
                            className="w-24 h-24 rounded"
                          />
                        )
                      )}
                    </div>
                  )}
                </div>
              ))
            )}
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
                          Từ chối
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

      {/* Tab Bài viết chờ duyệt (chỉ admin) */}
      {tab === "pendingPosts" && isAdmin && (
        <>
          <h2 className="font-semibold mb-2 text-lg">Bài viết chờ duyệt</h2>
          <div className="bg-white rounded-lg shadow p-4 mb-6">
            {pendingPosts.length === 0 ? (
              <div className="text-gray-500 italic">
                Không có bài viết chờ duyệt.
              </div>
            ) : (
              pendingPosts.map((post) => (
                <div key={post.id} className="mb-4 border-b pb-2">
                  <div className="font-bold">{post.content}</div>
                  {post.media && post.media.length > 0 && (
                    <div className="flex gap-2 mt-2">
                      {post.media.map((m, idx) =>
                        m.mediaType === "IMAGE" ? (
                          <img
                            key={idx}
                            src={m.mediaUrl}
                            alt="media"
                            className="w-24 h-24 object-cover rounded"
                          />
                        ) : (
                          <video
                            key={idx}
                            src={m.mediaUrl}
                            controls
                            className="w-24 h-24 rounded"
                          />
                        )
                      )}
                    </div>
                  )}
                  <button
                    className="bg-blue-500 text-white px-3 py-1 rounded mt-2"
                    onClick={() => handleApprovePost(post.id)}
                  >
                    Duyệt
                  </button>
                </div>
              ))
            )}
          </div>
        </>
      )}
    </div>
  );
}
