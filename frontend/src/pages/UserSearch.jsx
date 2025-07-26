import React, { useState } from "react";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";

const API_URL = "http://localhost:8080/api/v1/users/search";

export default function UserSearch() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const initialKeyword = params.get("keyword") || "";
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState(initialKeyword);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  // Lấy token từ localStorage (hoặc nơi bạn lưu token)
  const token = localStorage.getItem("token");

  // Gọi API khi vào trang nếu có keyword trên URL
  React.useEffect(() => {
    if (initialKeyword) {
      handleSearch(null, initialKeyword);
    }
    // eslint-disable-next-line
  }, [initialKeyword]);

  const handleSearch = async (e, customKeyword) => {
    if (e) e.preventDefault();
    const searchValue = customKeyword !== undefined ? customKeyword : keyword;
    if (!searchValue.trim()) return;
    setLoading(true);
    try {
      const res = await axios.get(API_URL, {
        params: { keyword: searchValue },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setUsers(res.data.data || []);
    } catch (err) {
      setUsers([]);
    }
    setLoading(false);
  };

  const handleInputChange = (e) => {
    setKeyword(e.target.value);
    if (!e.target.value) {
      setUsers([]);
    }
  };
  const handleUserClick = (id) => {
    navigate("/users/" + id);
  };
  return (
    <div className="max-w-xl mx-auto mt-8">
      <form onSubmit={handleSearch} className="flex items-center mb-4">
        <input
          type="text"
          value={keyword}
          onChange={handleInputChange}
          placeholder="Tìm kiếm người dùng Facebook"
          className="w-full px-4 py-2 border rounded-l-full focus:outline-none"
        />
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded-r-full hover:bg-blue-600"
        >
          Tìm kiếm
        </button>
      </form>
      <div className="bg-white rounded-lg shadow p-4">
        {loading ? (
          <div className="text-center text-gray-500">Đang tìm kiếm...</div>
        ) : users.length === 0 && keyword ? (
          <div className="text-center text-gray-500">
            Không tìm thấy người dùng nào.
          </div>
        ) : (
          <ul>
            {users.map((user) => (
              <li
                onClick={() => handleUserClick(user.id)}
                key={user.id}
                className="flex items-center gap-3 px-4 py-2 hover:bg-gray-100 cursor-pointer rounded"
              >
                <img
                  src={
                    user.avatar ||
                    "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  }
                  alt="avatar"
                  className="w-10 h-10 rounded-full object-cover"
                />

                <div>
                  <div className="font-semibold">{user.fullName}</div>
                  <div className="text-xs text-gray-500">
                    {user.gender === "MALE"
                      ? "Nam"
                      : user.gender === "FEMALE"
                      ? "Nữ"
                      : "Khác"}
                    {user.dateOfBirth && (
                      <> · {new Date(user.dateOfBirth).toLocaleDateString()}</>
                    )}
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
