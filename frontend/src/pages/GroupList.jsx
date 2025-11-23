import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

export default function GroupList() {
  const [groups, setGroups] = useState([]);
  const [myGroups, setMyGroups] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    fetchGroups();
    fetchMyGroups();
  }, []);

  const fetchGroups = async () => {
    setLoading(true);
    const res = await axios.get("http://localhost:8080/api/v1/groups", {
      headers: { Authorization: `Bearer ${token}` },
    });
    setGroups(res.data.data || []);
    setLoading(false);
  };

  const fetchMyGroups = async () => {
    const res = await axios.get(
      "http://localhost:8080/api/v1/groups/my-group",
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    setMyGroups(res.data.data || []);
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    const res = await axios.get(
      `http://localhost:8080/api/v1/groups/search?keyword=${encodeURIComponent(
        keyword
      )}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setGroups(res.data.data || []);
    setLoading(false);
  };

  return (
    <div className="max-w-4xl mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-blue-600">Danh sách nhóm</h1>
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded font-semibold"
          onClick={() => navigate("/groups/create")}
        >
          Tạo nhóm mới
        </button>
      </div>
      <form onSubmit={handleSearch} className="mb-4 flex gap-2">
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="Tìm kiếm nhóm..."
          className="border rounded px-3 py-1 flex-1"
        />
        <button className="bg-blue-500 text-white px-4 py-1 rounded">
          Tìm kiếm
        </button>
      </form>
      <h2 className="font-semibold mb-2 text-lg">Nhóm của tôi</h2>
      <div className="flex flex-wrap gap-4 mb-6">
        {myGroups.length === 0 ? (
          <div className="text-gray-500 mb-6 italic">
            Bạn chưa tham gia nhóm nào.
          </div>
        ) : (
          <div className="flex flex-wrap gap-4 mb-6">
            {myGroups.map((group) => (
              <Link
                key={group.id}
                to={`/groups/${group.id}`}
                className="block bg-gray-100 rounded-lg p-4 w-48 hover:shadow"
              >
                <img
                  src={
                    group.groupImage ||
                    "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png"
                  }
                  alt="group"
                  className="w-full h-24 object-cover rounded mb-2"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src =
                      "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png";
                  }}
                />
                <div className="font-bold">{group.name}</div>
                <div className="text-xs text-gray-500">{group.privacy}</div>
              </Link>
            ))}
          </div>
        )}
      </div>
      <h2 className="font-semibold mb-2 text-lg">Tất cả nhóm</h2>
      {loading ? (
        <div>Đang tải...</div>
      ) : (
        <div className="flex flex-wrap gap-4">
          {groups.map((group) => (
            <Link
              key={group.id}
              to={`/groups/${group.id}`}
              className="block bg-white rounded-lg p-4 w-48 hover:shadow"
            >
              <img
                src={
                  group.groupImage ||
                  "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png"
                }
                alt="group"
                className="w-full h-24 object-cover rounded mb-2"
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src =
                    "https://globalcastingresources.com/wp-content/uploads/2019/03/image1-5.png";
                }}
              />

              <div className="font-bold">{group.name}</div>
              <div className="text-xs text-gray-500">{group.privacy}</div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
