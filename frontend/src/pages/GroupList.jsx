import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { getGroups, getMyGroups, searchGroups } from "../api/Group/Action";
import AppLayout from "../components/layout/AppLayout";

export default function GroupList() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const { groups, myGroups, loading } = useSelector((state) => state.group);

  useEffect(() => {
    dispatch(getGroups());
    dispatch(getMyGroups());
  }, [dispatch]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!keyword.trim()) {
      dispatch(getGroups());
      setIsSearching(false);
      return;
    }
    setIsSearching(true);
    try {
      await dispatch(searchGroups(keyword));
    } catch (err) {
      console.error("Search failed:", err);
    }
  };

  return (
    <AppLayout>
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-blue-600">Danh sách nhóm</h1>
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700"
            onClick={() => navigate("/groups/create")}
          >
            Tạo nhóm mới
          </button>
        </div>
        <form onSubmit={handleSearch} className="mb-6 flex gap-2">
          <input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Tìm kiếm nhóm..."
            className="border rounded-lg px-4 py-2 flex-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">
            Tìm kiếm
          </button>
        </form>
        <h2 className="font-semibold mb-4 text-lg text-gray-700">Nhóm của tôi</h2>
        <div className="flex flex-wrap gap-4 mb-8">
          {!myGroups || myGroups.length === 0 ? (
            <div className="text-gray-500 italic">
              Bạn chưa tham gia nhóm nào.
            </div>
          ) : (
            myGroups.map((group) => (
              <Link
                key={group.id}
                to={`/groups/${group.id}`}
                className="block bg-white rounded-lg shadow p-4 w-56 hover:shadow-lg transition"
              >
                <img
                  src={
                    group.groupImage ||
                    "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png"
                  }
                  alt="group"
                  className="w-full h-32 object-cover rounded-lg mb-3"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src =
                      "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png";
                  }}
                />
                <div className="font-bold">{group.name}</div>
                <div className="text-xs text-gray-500">{group.privacy}</div>
              </Link>
            ))
          )}
        </div>
        <h2 className="font-semibold mb-4 text-lg text-gray-700">
          {isSearching ? "Kết quả tìm kiếm" : "Tất cả nhóm"}
        </h2>
        {loading ? (
          <div>Đang tải...</div>
        ) : (
          <div className="flex flex-wrap gap-4">
            {!groups || groups.length === 0 ? (
              <div className="text-gray-500">
                {isSearching ? "Không tìm thấy nhóm nào." : "Không có nhóm nào."}
              </div>
            ) : (
              groups.map((group) => (
                <Link
                  key={group.id}
                  to={`/groups/${group.id}`}
                  className="block bg-white rounded-lg shadow p-4 w-56 hover:shadow-lg transition"
                >
                  <img
                    src={
                      group.groupImage ||
                      "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png"
                    }
                    alt="group"
                    className="w-full h-32 object-cover rounded-lg mb-3"
                    onError={(e) => {
                      e.target.onerror = null;
                      e.target.src =
                        "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png";
                    }}
                  />

                  <div className="font-bold">{group.name}</div>
                  <div className="text-xs text-gray-500">{group.privacy}</div>
                </Link>
              ))
            )}
          </div>
        )}
      </div>
    </AppLayout>
  );
}
