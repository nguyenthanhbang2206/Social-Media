/**
 * Keycloak singleton instance + helpers.
 *
 * ─── VẤN ĐỀ VỚI BẢN GỐC ───────────────────────────────────────────────────
 *
 * 1. `onLoad: 'login-required'`  → App BẮT BUỘC đăng nhập ngay khi load.
 *    Mọi truy cập vào bất kỳ page nào (kể cả /login) đều bị redirect sang
 *    Keycloak. Điều này sai vì:
 *    - Trang /login của React sẽ không bao giờ hiển thị
 *    - User không thể xem gì trước khi login
 *    Fix: dùng `onLoad: 'check-sso'` → chỉ check session hiện có, không force login
 *
 * 2. `pkceMethod: 'S256'` là đúng – PKCE bảo vệ Authorization Code flow
 *    Giữ nguyên.
 *
 * 3. `initPromise` singleton pattern là đúng – tránh khởi tạo nhiều lần.
 *    Giữ nguyên.
 *
 * ─── FLOW CHUẨN ────────────────────────────────────────────────────────────
 *
 *  App load
 *    → initKeycloak() với check-sso
 *      → Nếu có session cũ: authenticated = true → sync với backend
 *      → Nếu không có session: authenticated = false → hiển thị app bình thường
 *          → User click "Login" → login() → redirect sang Keycloak
 *          → Keycloak xác thực xong → redirect về app → initKeycloak() lại
 *          → authenticated = true → sync với backend
 *
 * ─── TOKEN REFRESH ─────────────────────────────────────────────────────────
 *
 * axios interceptor gọi updateToken(5) trước mỗi request:
 *   - Nếu token hết hạn trong vòng 5 giây → Keycloak tự refresh bằng iframe
 *   - Nếu refresh_token cũng hết hạn → throw error → logout
 * ────────────────────────────────────────────────────────────────────────────
 */
import Keycloak from "keycloak-js";

const keycloakConfig = {
  url: process.env.REACT_APP_KEYCLOAK_URL || "http://localhost:8088",
  realm: process.env.REACT_APP_KEYCLOAK_REALM || "social-media-realm",
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID || "social-media-frontend",
};

const keycloak = new Keycloak(keycloakConfig);

let initPromise = null;

/**
 * Khởi tạo Keycloak.
 *
 * Dùng `check-sso` thay vì `login-required`:
 *   - check-sso: kiểm tra xem có SSO session cũ không, không force login
 *   - login-required: force redirect sang Keycloak nếu chưa login (sai cho SPA)
 *
 * `silentCheckSsoRedirectUri`: Keycloak dùng iframe ẩn để check session
 * mà không làm gián đoạn UX. File này phải tồn tại trong /public/.
 *
 * @returns {Promise<boolean>} true nếu user đã authenticated
 */
export const initKeycloak = () => {
  if (initPromise) return initPromise;

  initPromise = keycloak
      .init({
        onLoad: "check-sso",
        silentCheckSsoRedirectUri:
            window.location.origin + "/silent-check-sso.html",
        checkLoginIframe: false,
        pkceMethod: "S256",
      })
      .catch((error) => {
        initPromise = null; // reset để có thể retry
        throw error;
      });

  return initPromise;
};

/** Redirect user sang Keycloak login page. */
export const login = () => keycloak.login();

/**
 * Logout: revoke session tại Keycloak và redirect về /login.
 * Keycloak sẽ xóa cookie session – sau đó initKeycloak() sẽ trả về false.
 */
export const logout = () =>
    keycloak.logout({ redirectUri: window.location.origin + "/login" });

/**
 * Redirect sang Keycloak registration page.
 * Sau khi register xong, Keycloak tự redirect về app (theo redirectUri đã config).
 */
export const register = () => keycloak.register();

/** Access token hiện tại (JWT string). */
export const getToken = () => keycloak.token;

/**
 * Refresh token nếu sẽ hết hạn trong `minValidity` giây.
 *
 * @param {number} minValidity - số giây còn lại tối thiểu
 * @returns {Promise<boolean>} true nếu token đã được refresh, false nếu vẫn còn hạn
 */
export const updateToken = (minValidity = 5) =>
    keycloak.updateToken(minValidity);

/** Lấy thông tin user từ Keycloak UserInfo endpoint. */
export const getUserInfo = () => keycloak.loadUserInfo();

/** Preferred username (username trong Keycloak). */
export const getUsername = () => keycloak.tokenParsed?.preferred_username;

/** Email claim từ JWT. */
export const getEmail = () => keycloak.tokenParsed?.email;

/** Full name (name claim = firstName + lastName). */
export const getFullName = () => keycloak.tokenParsed?.name;

/** Subject = Keycloak user UUID. */
export const getUserId = () => keycloak.tokenParsed?.sub;

/**
 * Lấy tất cả realm roles từ JWT.
 * Keycloak đặt roles tại: tokenParsed.realm_access.roles
 */
export const getRoles = () =>
    keycloak.tokenParsed?.realm_access?.roles || [];

/** Kiểm tra user có role cụ thể không. */
export const hasRole = (role) => getRoles().includes(role);

/** true nếu user đã authenticated với Keycloak. */
export const isAuthenticated = () => keycloak.authenticated === true;

export default keycloak;
