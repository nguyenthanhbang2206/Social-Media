/**
 * Register page.
 *
 * VẤN ĐỀ VỚI BẢN GỐC:
 * - Page render form đăng ký riêng (email/password/username) và POST trực
 *   tiếp lên backend /auth/register. Điều này tạo ra HAI nguồn sự thật về
 *   user creation: Keycloak registration page VÀ form riêng của app.
 *   Vấn đề: nếu dùng PKCE flow (Authorization Code), KHÔNG nên có form
 *   đăng ký riêng trên app – mọi user creation phải qua Keycloak để đảm bảo
 *   password được hash đúng chuẩn, validate theo password policy của Keycloak,
 *   và để event "REGISTER" được Keycloak ghi lại trong audit log.
 *
 * Fix: Register page giờ chỉ là một trang trung gian, redirect sang
 * Keycloak's hosted registration page bằng register() helper.
 * Đây CHÍNH LÀ chuẩn OAuth2/OIDC: Identity Provider (Keycloak) chịu trách
 * nhiệm toàn bộ vòng đời credential, app chỉ tiêu thụ token.
 */
import React, { useEffect } from "react";
import { register } from "../utils/keycloak";

export const Register = () => {
  useEffect(() => {
    // Tự động redirect ngay khi vào trang – không cần user bấm thêm nút
    register();
  }, []);

  return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Đang chuyển hướng đến trang đăng ký...</p>
        </div>
      </div>
  );
};