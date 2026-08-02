package com.nguyenthanhbang.Social_media.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;


@Slf4j
public final class RequestHeaderUtil {

    private RequestHeaderUtil() {}

    public static Optional<String> getHeader(String name) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        return Optional.ofNullable(request.getHeader(name));
    }

  
    public static Optional<String> getUserId() {
        return getHeader("X-User-Id");
    }

    public static Optional<String> getUserEmail() {
        return getHeader("X-User-Email");
    }

    public static Optional<String> getUserRole() {
        return getHeader("X-User-Role");
    }

    public static Optional<String> getUserName() {
        return getHeader("X-User-Name");
    }

    public static Optional<String> getUserFullName() {
        return getHeader("X-User-FullName");
    }

    public static Optional<java.util.List<String>> getUserRoles() {
        return getHeader("X-User-Roles").map(rolesStr -> {
            if (rolesStr.isBlank()) {
                return java.util.Collections.emptyList();
            }
            return java.util.Arrays.asList(rolesStr.split(","));
        });
    }
}
