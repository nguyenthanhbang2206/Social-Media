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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity          
@RequiredArgsConstructor
public class SecurityConfig {

    private final HeaderAuthenticationConverter headerAuthenticationConverter;

   
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**",          
            "/actuator/**",             
            "/api/v1/users/internal/**", // internal Feign call giữa các services
            "/api/v1/users/{id:\\d+}",  // allow internal get user by numeric id
            "/api/v1/users/email/{email}", // allow internal get user by email
            "/api/v1/blocks/exists"      // allow internal block status checks
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

        .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/profile", "/api/v1/users/search").authenticated()
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new HeaderAuthenticationFilter(headerAuthenticationConverter),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
