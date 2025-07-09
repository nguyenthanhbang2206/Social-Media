
package com.nguyenthanhbang.Social_media.util;

import com.nguyenthanhbang.Social_media.dto.response.AuthenticationResponse;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


@Service
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Value("${jwt.base64-secret}")
    private String jwtKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long jwtAccessExpiration;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshExpiration;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, MacAlgorithm.HS512.getName());
    }
    public Jwt checkValidToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(MacAlgorithm.HS512).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw e;
        }
    }
    public String createAccessToken(String email, AuthenticationResponse authenticationResponse) {
        AuthenticationResponse.UserToken userToken = new AuthenticationResponse.UserToken(authenticationResponse.getUser().getId(), authenticationResponse.getUser().getEmail(), authenticationResponse.getUser().getFullName());
        String role = "ROLE_" + authenticationResponse.getUser().getRole().name();
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessExpiration, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("role", role)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    public String createRefreshToken(String email, AuthenticationResponse authenticationResponse){
        AuthenticationResponse.UserToken userToken = new AuthenticationResponse.UserToken(authenticationResponse.getUser().getId(), authenticationResponse.getUser().getEmail(), authenticationResponse.getUser().getFullName());
        String role = "ROLE_" + authenticationResponse.getUser().getRole().name();
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessExpiration, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("role", role)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication. getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

}
