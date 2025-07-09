package com.nguyenthanhbang.Social_media.config;

import com.nguyenthanhbang.Social_media.repository.InvalidTokenRepository;
import com.nguyenthanhbang.Social_media.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.getTokenFromRequest(request);
        if (token != null) {
            if(invalidTokenRepository.existsByToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
                return;
            }
            Jwt jwt = null;
            try {
                jwt = securityUtil.checkValidToken(token);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            String email = jwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}