package com.nguyenthanhbang.Social_media.security;

import com.nguyenthanhbang.Social_media.common.enumeration.Role;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nguyenthanhbang.Social_media.event.UserUpdatePublisher;
import com.nguyenthanhbang.Social_media.common.event.UserUpdateEvent;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class HeaderAuthenticationConverter {

    private final UserRepository userRepository;
    private final UserUpdatePublisher userUpdatePublisher;

    /**
     * @return Authentication object nếu headers hợp lệ, null nếu không có header
     *         (request tới open endpoint – sẽ được permitAll() pass qua)
     */
    public Authentication convert(HttpServletRequest request) {
        String userId   = request.getHeader("X-User-Id");
        String email    = request.getHeader("X-User-Email");
        String role     = request.getHeader("X-User-Role");
        String username = request.getHeader("X-User-Name");
        String fullName = request.getHeader("X-User-FullName");

        // Không có header → open endpoint, không set auth
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            UUID keycloakId = UUID.fromString(userId);

            // Tìm hoặc auto-provision user trong local DB
            String finalUsername = username;
            String finalFullName = fullName;
            User user = userRepository.findByKeycloakId(keycloakId)
                    .map(existingUser -> syncUserDetails(existingUser, email, role, finalUsername, finalFullName))
                    .orElse(null);

            // Account Linking: nếu chưa có keycloakId nhưng có email trùng → link
            if (user == null && email != null && !email.isBlank()) {
                User existingByEmail = userRepository.findByEmailAndActiveTrue(email);
                if (existingByEmail != null) {
                    log.info("Account Linking: Binding existing user email={} to keycloakId={}", email, keycloakId);
                    existingByEmail.setKeycloakId(keycloakId);
                    user = syncUserDetails(existingByEmail, email, role, finalUsername, finalFullName);
                }
            }

            // Nếu vẫn không có → tạo mới (auto-provision)
            if (user == null) {
                user = autoProvisionUser(keycloakId, email, role, finalUsername, finalFullName);
            }

            if (!user.isEnabled()) {
                log.warn("Blocked login for disabled user: keycloakId={}", keycloakId);
                return null;
            }

            UserPrincipal principal  = new UserPrincipal(user);
            String        roleToUse  = (role != null && !role.isBlank()) ? role : "USER";
            var           authority  = new SimpleGrantedAuthority("ROLE_" + roleToUse);

            return new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));

        } catch (IllegalArgumentException e) {
            log.warn("X-User-Id is not a valid UUID: '{}'", userId);
            return null;
        } catch (Exception e) {
            log.error("Error resolving user from gateway headers: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Đồng bộ thông tin user từ Keycloak xuống local DB nếu có thay đổi.
     */
    private User syncUserDetails(User user, String email, String role, String username, String fullName) {
        boolean updated = false;

        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            user.setEmail(email);
            updated = true;
        }

        if (fullName != null && !fullName.isBlank() && !fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            updated = true;
        }

        if (role != null && !role.isBlank()) {
            Role parsedRole = parseRole(role);
            if (user.getRole() != parsedRole) {
                user.setRole(parsedRole);
                updated = true;
            }
        }

        if (username != null && !username.isBlank() && !username.equals(user.getUsername())) {
            // Đảm bảo username mới không bị trùng
            if (!userRepository.existsByUsername(username)) {
                user.setUsername(username);
                updated = true;
            } else {
                log.warn("Cannot sync updated username '{}' for keycloakId={}: username already exists in DB", username, user.getKeycloakId());
            }
        }

        if (updated) {
            log.info("Self-healing profile sync: updating user in DB for keycloakId={}", user.getKeycloakId());
            user = userRepository.save(user);

            try {
                userUpdatePublisher.publishUserUpdatedEvent(UserUpdateEvent.builder()
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .active(user.getActive())
                        .build());
            } catch (Exception e) {
                log.error("Failed to publish user update event on self-healing sync: {}", e.getMessage(), e);
            }
            return user;
        }

        return user;
    }

    /**
     * Tạo bản ghi User mới trong local DB khi user tồn tại trong Keycloak
     * nhưng chưa có trong local DB.
     *
     * Thông tin được lấy từ headers do Gateway inject – đây là thông tin
     * đã được xác thực bởi JWT validation tại Gateway.
     */
    private User autoProvisionUser(UUID keycloakId, String email, String role, String username, String fullName) {
        log.info("Auto-provisioning local user for keycloakId={}", keycloakId);

        String safeEmail = (email != null && !email.isBlank())
                ? email
                : keycloakId + "@unknown.local";

        String safeUsername = (username != null && !username.isBlank())
                ? username
                : safeEmail.split("@")[0];

        // Đảm bảo username không bị duplicate
        if (userRepository.existsByUsername(safeUsername)) {
            safeUsername = safeUsername + "_" + keycloakId.toString().substring(0, 8);
        }

        String safeFullName = (fullName != null && !fullName.isBlank())
                ? fullName
                : "Unknown";

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(safeEmail)
                .username(safeUsername)
                .fullName(safeFullName)
                .enabled(true)
                .emailVerified(false)
                .role(parseRole(role))
                .build();

        return userRepository.save(user);
    }

    private Role parseRole(String role) {
        if (role == null) return Role.USER;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
