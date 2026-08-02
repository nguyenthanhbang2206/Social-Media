import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { searchUsers } from "../api/User/Action";
import AppLayout from "../components/layout/AppLayout";
import { navigateToUserProfile } from "../utils/profileNavigation";

export default function UserSearch() {
  const dispatch = useDispatch();
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const initialKeyword = params.get("keyword") || "";
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState(initialKeyword);
  const { searchResults, loading } = useSelector((state) => state.user);
  const { user } = useSelector((state) => state.auth);

  // Gọi API khi vào trang nếu có keyword trên URL
  useEffect(() => {
    if (initialKeyword) {
      dispatch(searchUsers(initialKeyword));
    }
  }, [initialKeyword, dispatch]);

  const handleSearch = async (e, customKeyword) => {
    if (e) e.preventDefault();
    const searchValue = customKeyword !== undefined ? customKeyword : keyword;
    if (!searchValue.trim()) return;
    try {
      await dispatch(searchUsers(searchValue));
    } catch (err) {
      console.error("Search failed:", err);
    }
  };

  const handleInputChange = (e) => {
    setKeyword(e.target.value);
    if (!e.target.value) {
      dispatch(searchUsers(""));
    }
  };

  const handleUserClick = (id) => {
    navigateToUserProfile(navigate, user?.id, id);
  };

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto">
        <form onSubmit={handleSearch} className="flex items-center mb-6">
          <input
            type="text"
            value={keyword}
            onChange={handleInputChange}
            placeholder="Tìm kiếm người dùng Facebook"
            className="w-full px-4 py-3 border rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-3 rounded-r-lg hover:bg-blue-700"
          >
            Tìm kiếm
          </button>
        </form>
        <div className="bg-white rounded-lg shadow p-6">
          {loading ? (
            <div className="text-center text-gray-500 py-8">
              Đang tìm kiếm...
            </div>
          ) : !searchResults || searchResults.length === 0 ? (
            keyword ? (
              <div className="text-center text-gray-500 py-8">
                Không tìm thấy người dùng nào.
              </div>
            ) : null
          ) : (
            <ul className="space-y-3">
              {searchResults.map((user) => (
                <li
                  onClick={() => handleUserClick(user.id)}
                  key={user.id}
                  className="flex items-center gap-4 px-4 py-3 hover:bg-gray-50 cursor-pointer rounded-lg border"
                >
                  <img
                    src={
                      user.avatar ||
                      "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                    }
                    alt="avatar"
                    className="w-14 h-14 rounded-full object-cover"
                  />

                  <div>
                    <div className="font-semibold text-lg">{user.fullName}</div>
                    <div className="text-sm text-gray-500">
                      {user.gender === "MALE"
                        ? "Nam"
                        : user.gender === "FEMALE"
                          ? "Nữ"
                          : "Khác"}
                      {user.dateOfBirth && (
                        <>
                          {" "}
                          · {new Date(user.dateOfBirth).toLocaleDateString()}
                        </>
                      )}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
