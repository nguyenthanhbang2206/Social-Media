import { Routes, Route } from "react-router-dom";
import AdminLayout from "../layout/AdminLayout";
import AdminDashboard from "../pages/AdminDashBoard";
import UserLayout from "../layout/UserLayout";
import Home from "../pages/Home";
import { Login } from "../pages/Login";
import { Register } from "../pages/Register";

export default function AppRouter() {
  return (
    <Routes>
      {/* Admin routes */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
        {/* Các route admin khác */}
      </Route>

      {/* User routes */}
        <Route path="/" element={<Home />} />
        
      
      <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
    </Routes>
  );
}
