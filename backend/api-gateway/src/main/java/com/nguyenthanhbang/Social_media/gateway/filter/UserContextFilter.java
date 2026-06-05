package com.nguyenthanhbang.Social_media.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserContextFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                    .map(securityContext -> {
                        Authentication authentication = securityContext.getAuthentication();
                        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                            Jwt jwt = (Jwt) authentication.getPrincipal();
                            
                            // Extract user information from JWT
                            String userId = jwt.getSubject();
                            String email = jwt.getClaimAsString("email");
                            String preferredUsername = jwt.getClaimAsString("preferred_username");
                            String roles = jwt.getClaimAsString("roles");
                            
                            // Add headers to forward to downstream services
                            HttpHeaders headers = exchange.getRequest().getHeaders();
                            HttpHeaders newHeaders = new HttpHeaders();
                            newHeaders.putAll(headers);
                            
                            if (userId != null) {
                                newHeaders.add("X-User-Id", userId);
                            }
                            if (email != null) {
                                newHeaders.add("X-User-Email", email);
                            }
                            if (preferredUsername != null) {
                                newHeaders.add("X-User-Name", preferredUsername);
                            }
                            if (roles != null) {
                                newHeaders.add("X-User-Roles", roles);
                            }
                            
                            // Replace request with new headers
                            exchange = exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            .headers(h -> h.addAll(newHeaders))
                                            .build())
                                    .build();
                        }
                        return exchange;
                    })
                    .defaultIfEmpty(exchange)
                    .flatMap(chain::filter);
        };
    }
}
