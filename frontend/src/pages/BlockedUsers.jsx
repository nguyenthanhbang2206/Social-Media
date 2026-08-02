import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { getBlockedUsers, unblockUser } from "../api/Block/Action";
import AppLayout from "../components/layout/AppLayout";

export default function BlockedUsers() {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const { blockedUsers, loading } = useSelector((state) => state.block);

  useEffect(() => {
    if (user && user.id) {
      dispatch(getBlockedUsers());
    }
  }, [user, dispatch]);

  const handleUnblock = async (blockedUserId) => {
    try {
      await dispatch(unblockUser(blockedUserId));
    } catch (err) {
      alert("Không thể bỏ chặn người dùng này");
    }
  };

  return (
    <AppLayout>
      <div className="max-w-4xl mx-auto">
        <h2 className="text-2xl font-bold mb-6 text-blue-700">Danh sách chặn</h2>
        {loading ? (
          <div className="text-center text-gray-500 py-8">
            Đang tải danh sách chặn...
          </div>
        ) : !blockedUsers || blockedUsers.length === 0 ? (
          <div className="text-center text-gray-500 py-8">
            Bạn chưa chặn người dùng nào.
          </div>
        ) : (
          <ul className="space-y-4">
            {blockedUsers.map((blockedUser) => (
              <li
                key={blockedUser.id}
                className="flex items-center justify-between bg-white shadow rounded-lg px-6 py-4"
              >
                <div className="flex items-center gap-4">
                  <img
                    src={
                      blockedUser.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-14 h-14 rounded-full object-cover border"
                  />
                  <div>
                    <div className="font-semibold text-lg">
                      {blockedUser.fullName}
                    </div>
                    <div className="text-sm text-gray-500">
                      {blockedUser.gender === "MALE"
                        ? "Nam"
                        : blockedUser.gender === "FEMALE"
                        ? "Nữ"
                        : "Khác"}
                      {blockedUser.dateOfBirth && (
                        <>
                          {" "}
                          · {new Date(blockedUser.dateOfBirth).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                </div>
                <button
                  className="px-4 py-2 rounded bg-red-600 text-white font-semibold hover:bg-red-700"
                  onClick={() => handleUnblock(blockedUser.id)}
                >
                  Bỏ chặn
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </AppLayout>
  );
}
