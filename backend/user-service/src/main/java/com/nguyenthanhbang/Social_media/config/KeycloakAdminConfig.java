package com.nguyenthanhbang.Social_media.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cung cấp Keycloak Admin Client bean.
 *
 * Tất cả credentials được đọc từ application.properties / environment variables.
 * KHÔNG bao giờ hardcode password trong source code.
 *
 * Keycloak Admin Client sử dụng Service Account "admin-cli" trong realm "master"
 * để có quyền quản lý users trong realm "social-media-realm".
 *
 * ─── Cách lấy credentials ───────────────────────────────────────────────────
 *   keycloak.admin.username  = KEYCLOAK_ADMIN (env var của docker-compose)
 *   keycloak.admin.password  = KEYCLOAK_ADMIN_PASSWORD (env var của docker-compose)
 * ────────────────────────────────────────────────────────────────────────────
 */
@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    /** Realm chứa admin – luôn là "master". */
    @Value("${keycloak.admin.realm:master}")
    private String adminRealm;

    /** Client ID dùng để admin login – mặc định là "admin-cli". */
    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    /**
     * Keycloak Admin Client singleton.
     *
     * Bean này được dùng bởi {@link com.nguyenthanhbang.Social_media.service.KeycloakService}
     * để thực hiện các thao tác quản trị (tạo user, gán role, đặt lại password...).
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(adminClientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}
