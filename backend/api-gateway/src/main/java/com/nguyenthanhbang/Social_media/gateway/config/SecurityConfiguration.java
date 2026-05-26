package com.nguyenthanhbang.Social_media.gateway.config;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // FIX CORS
                .cors(Customizer.withDefaults())

                .authorizeExchange(exchange -> exchange

                        // FIX PREFLIGHT
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/ws/notifications/**").permitAll()

                        .anyExchange().authenticated())

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults()))

                .build();
    }

    // FIX CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:3000"));

        configuration.setAllowedMethods(List.of("*"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        byte[] keyBytes = Base64.from(jwtKey).decode();

        SecretKey secretKey =
                new SecretKeySpec(
                        keyBytes,
                        0,
                        keyBytes.length,
                        MacAlgorithm.HS512.getName());

        return NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}