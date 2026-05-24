package com.nguyenthanhbang.Social_media.common.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public final class RequestHeaderUtil {
    private RequestHeaderUtil() {}

    public static Optional<String> getHeader(String name) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }
        ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
        HttpServletRequest request = servletAttrs.getRequest();
        if (request == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(request.getHeader(name));
    }

    public static Optional<Long> getUserId() {
        return getHeader("X-User-Id").map(Long::valueOf);
    }

    public static Optional<String> getUserEmail() {
        return getHeader("X-User-Email");
    }
}
