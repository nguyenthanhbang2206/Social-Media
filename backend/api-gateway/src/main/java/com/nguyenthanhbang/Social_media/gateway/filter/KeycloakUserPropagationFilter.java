package com.nguyenthanhbang.Social_media.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KeycloakUserPropagationFilter implements GlobalFilter, Ordered {

    public static final String HEADER_USER_ID        = "X-User-Id";
    public static final String HEADER_USER_EMAIL     = "X-User-Email";
    public static final String HEADER_USER_ROLE      = "X-User-Role";
    public static final String HEADER_USER_NAME      = "X-User-Name";
    public static final String HEADER_USER_FULL_NAME = "X-User-FullName";
    public static final String HEADER_USER_ROLES     = "X-User-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(ctx -> ctx.getAuthentication() instanceof JwtAuthenticationToken)
                .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> {
                    ServerHttpRequest mutated = buildMutatedRequest(exchange, jwt);
                    log.debug("User headers injected: userId={}", jwt.getSubject());
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    ServerHttpRequest sanitized = removeUserHeaders(exchange);
                    return chain.filter(exchange.mutate().request(sanitized).build());
                }));
    }

  
    private ServerHttpRequest buildMutatedRequest(ServerWebExchange exchange, Jwt jwt) {
        String userId = jwt.getSubject();
        String email  = jwt.getClaimAsString("email");
        String role   = extractPrimaryRole(jwt);
        String username = jwt.getClaimAsString("preferred_username");
        String fullName = jwt.getClaimAsString("name");
        List<String> roles = extractAllRoles(jwt);
        String rolesStr = roles != null ? String.join(",", roles) : "USER";

        return exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER_ID);
                    headers.remove(HEADER_USER_EMAIL);
                    headers.remove(HEADER_USER_ROLE);
                    headers.remove(HEADER_USER_NAME);
                    headers.remove(HEADER_USER_FULL_NAME);
                    headers.remove(HEADER_USER_ROLES);
                })
                .header(HEADER_USER_ID,        userId != null ? userId : "")
                .header(HEADER_USER_EMAIL,     email  != null ? email  : "")
                .header(HEADER_USER_ROLE,      role   != null ? role   : "USER")
                .header(HEADER_USER_NAME,      username != null ? username : "")
                .header(HEADER_USER_FULL_NAME, fullName != null ? fullName : "")
                .header(HEADER_USER_ROLES,     rolesStr)
                .build();
    }

  
    private ServerHttpRequest removeUserHeaders(ServerWebExchange exchange) {
        return exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER_ID);
                    headers.remove(HEADER_USER_EMAIL);
                    headers.remove(HEADER_USER_ROLE);
                    headers.remove(HEADER_USER_NAME);
                    headers.remove(HEADER_USER_FULL_NAME);
                    headers.remove(HEADER_USER_ROLES);
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    private String extractPrimaryRole(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return "USER";

            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles == null || roles.isEmpty()) return "USER";

            if (roles.contains("ADMIN")) return "ADMIN";

            return roles.stream()
                    .filter(r -> !r.startsWith("default-roles")
                            && !r.equals("offline_access")
                            && !r.equals("uma_authorization"))
                    .findFirst()
                    .orElse("USER");

        } catch (ClassCastException e) {
            log.warn("Cannot parse realm_access.roles from JWT: {}", e.getMessage());
            return "USER";
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> extractAllRoles(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of("USER");

            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles == null || roles.isEmpty()) return List.of("USER");

            return roles;
        } catch (Exception e) {
            log.warn("Cannot parse realm_access.roles from JWT for all roles: {}", e.getMessage());
            return List.of("USER");
        }
    }

   
    @Override
    public int getOrder() {
        return -100;
    }
}
