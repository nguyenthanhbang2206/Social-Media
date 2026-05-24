package com.nguyenthanhbang.Social_media.config;

import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignHeaderConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            RequestHeaderUtil.getHeader("X-User-Id")
                    .ifPresent(value -> template.header("X-User-Id", value));
            RequestHeaderUtil.getHeader("X-User-Email")
                    .ifPresent(value -> template.header("X-User-Email", value));
        };
    }
}
