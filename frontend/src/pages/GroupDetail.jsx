import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import {
  getGroupById,
  getGroupPosts,
  getGroupPendingPosts,
  createGroupPost,
  approveGroupPost,
  deleteGroup,
} from "../api/Group/Action";
import {
  getGroupMembers,
  getPendingMembers,
  getMembershipStatus,
  joinGroup,
  leaveGroup,
  approveMember,
  deleteMember,
  updateMemberRole,
  rejectMember,
  checkIsAdmin,
} from "../api/GroupMember/Action";
import AppLayout from "../components/layout/AppLayout";

export default function GroupDetail() {
  const dispatch = useDispatch();
  const { id } = useParams();
  const navigate = useNavigate();
  const [tab, setTab] = useState("members");
  const [postContent, setPostContent] = useState("");
  const [postMedia, setPostMedia] = useState([]);
  const [actionLoading, setActionLoading] = useState(false);
  const fileInputRef = useRef(null);

  const user = JSON.parse(localStorage.getItem("user") || "null");

  const { currentGroup, groupPosts, pendingPosts, loading } = useSelector((state) => state.group);
  const { members, pendingMembers, membershipStatus, isAdmin } = useSelector((state) => state.groupMember);

  const group = currentGroup;
  const isCreator = group && group.creator && user ? String(group.creator.id) === String(user.id) : false;

  useEffect(() => {
    dispatch(getGroupById(id));
    dispatch(getGroupMembers(id));
    if (user && user.id) {
      dispatch(getMembershipStatus(id)).catch(() => {
        // Ignore error if user is not a member
      });
      dispatch(checkIsAdmin(id)).catch(() => {
        // Ignore error if user is not an admin
      });
    }
    // eslint-disable-next-line
  }, [id, dispatch]);

  useEffect(() => {
    if (tab === "posts") {
      dispatch(getGroupPosts(id)).catch((err) => {
        console.error("Error fetching group posts:", err);
      });
    }
    if (tab === "pendingPosts" && isAdmin && group?.privacy === "PRIVATE") {
      dispatch(getGroupPendingPosts(id)).catch((err) => {
        console.error("Error fetching pending group posts:", err);
      });
    }
    if (tab === "pending" && isAdmin && group?.privacy === "PRIVATE") {
      dispatch(getPendingMembers(id)).catch((err) => {
        console.error("Error fetching pending members:", err);
      });
    }
  }, [tab, isAdmin, group?.privacy, id, dispatch]);

  // Tạo bài viết mới trong group
  const handleCreatePost = async (e) => {
    e.preventDefault();
    try {
      await dispatch(
        createGroupPost(id, {
          content: postContent,
          postType: "GROUP_POST",
          media: postMedia,
        })
      );
      setPostContent("");
      setPostMedia([]);
      if (fileInputRef.current) fileInputRef.current.value = "";
      await dispatch(getGroupPosts(id));
      if (isAdmin) await dispatch(getGroupPendingPosts(id));
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
      await dispatch(approveGroupPost(id, postId));
      await dispatch(getGroupPendingPosts(id));
      await dispatch(getGroupPosts(id));
    } catch (err) {
      alert(
        "Lỗi duyệt bài viết: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleJoin = async () => {
    setActionLoading(true);
    try {
      await dispatch(joinGroup(id));
      await dispatch(getMembershipStatus(id));
      await dispatch(getGroupMembers(id));
    } catch (err) {
      console.error("Error joining group:", err);
      const errorMessage = err?.response?.data?.message || "Không thể tham gia nhóm. Vui lòng thử lại.";
      alert("Lỗi tham gia nhóm: " + errorMessage);
    }
    setActionLoading(false);
  };

  const handleLeave = async () => {
    setActionLoading(true);
    try {
      await dispatch(leaveGroup(id));
      await dispatch(getMembershipStatus(id));
      await dispatch(getGroupMembers(id));
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
      await dispatch(deleteGroup(id));
      navigate("/groups");
    } catch (err) {
      alert(
        "Lỗi xóa nhóm: " + (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleApprove = async (userId) => {
    if (!userId) {
      console.error("Cannot approve member: userId is undefined");
      return;
    }
    try {
      await dispatch(approveMember(id, userId));
      await dispatch(getGroupMembers(id));
      await dispatch(getPendingMembers(id));
    } catch (err) {
      alert(
        "Lỗi duyệt thành viên: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleDeleteMember = async (userId) => {
    if (!userId) {
      console.error("Cannot delete member: userId is undefined");
      return;
    }
    if (!window.confirm("Xóa thành viên này khỏi nhóm?")) return;
    try {
      await dispatch(deleteMember(id, userId));
      await dispatch(getGroupMembers(id));
      await dispatch(getPendingMembers(id));
    } catch (err) {
      alert(
        "Lỗi xóa thành viên: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleChangeRole = async (userId, role) => {
    if (!userId) {
      console.error("Cannot change role: userId is undefined");
      return;
    }
    try {
      await dispatch(updateMemberRole(id, userId, role));
      await dispatch(getGroupMembers(id));
    } catch (err) {
      alert(
        "Lỗi đổi vai trò: " +
          (err?.response?.data?.message || "Vui lòng thử lại")
      );
    }
  };

  const handleRejectMember = async (userId) => {
    if (!userId) {
      console.error("Cannot reject member: userId is undefined");
      return;
    }
    if (!window.confirm("Bạn có chắc muốn từ chối thành viên này?")) return;
    try {
      await dispatch(rejectMember(id, userId));
      await dispatch(getPendingMembers(id));
      await dispatch(getGroupMembers(id));
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
    <AppLayout>
      <div className="max-w-4xl mx-auto">
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
                  key={m.id || m.user?.id || Math.random()}
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
                    key={m.id || m.user?.id || Math.random()}
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
    </AppLayout>
  );
}
