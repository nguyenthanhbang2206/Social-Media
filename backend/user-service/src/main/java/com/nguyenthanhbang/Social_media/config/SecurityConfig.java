package com.nguyenthanhbang.Social_media.config;

import com.nguyenthanhbang.Social_media.security.HeaderAuthenticationConverter;
import com.nguyenthanhbang.Social_media.security.HeaderAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration cho user-service.
 *
 * ─── THIẾT KẾ ──────────────────────────────────────────────────────────────
 *
 * user-service KHÔNG tự validate JWT. Việc này đã được API Gateway xử lý
 * (Spring Cloud Gateway + NimbusReactiveJwtDecoder với JWKS của Keycloak).
 *
 * Sau khi Gateway xác thực xong, nó inject 3 header xuống downstream:
 *   X-User-Id    → Keycloak subject (UUID)
 *   X-User-Email → email claim
 *   X-User-Role  → primary realm role
 *
 * user-service chỉ cần đọc và tin tưởng các header này vì:
 *   1. Các request từ bên ngoài phải đi qua Gateway trước khi đến service
 *   2. Header X-User-* chỉ được Gateway inject sau khi JWT hợp lệ
 *   3. Trong môi trường production, các services không expose port ra ngoài
 *
 * Flow xác thực trong user-service:
 *   Request → HeaderAuthenticationFilter → HeaderAuthenticationConverter
 *           → resolve User từ DB (hoặc auto-provision)
 *           → set SecurityContext
 * ────────────────────────────────────────────────────────────────────────────
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // cho phép @PreAuthorize, @Secured trên method
@RequiredArgsConstructor
public class SecurityConfig {

    private final HeaderAuthenticationConverter headerAuthenticationConverter;

    /**
     * Danh sách endpoints không yêu cầu xác thực.
     * Phải khớp với OPEN_PATHS trong API Gateway SecurityConfig.
     */
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**",          // register, login, refresh, logout
            "/actuator/**",             // health check
            "/api/v1/users/internal/**", // internal Feign call giữa các services
            "/api/v1/users/{id:\\d+}",  // allow internal get user by numeric id
            "/api/v1/users/email/{email}", // allow internal get user by email
            "/api/v1/blocks/exists"      // allow internal block status checks
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF không cần thiết với JWT / stateless
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless – không dùng HttpSession
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/profile", "/api/v1/users/search").authenticated()
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                )

                // Filter đọc X-User-* headers từ Gateway
                .addFilterBefore(
                        new HeaderAuthenticationFilter(headerAuthenticationConverter),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
