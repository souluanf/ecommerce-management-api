package dev.luanfernandes.adapter.out.security;

import dev.luanfernandes.domain.exception.InvalidTokenException;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProviderAdapter implements JwtTokenProvider {

    private final SecretKey accessTokenKey;
    private final SecretKey refreshTokenKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtTokenProviderAdapter(
            @Value("${app.jwt.access-token.secret}") String accessTokenSecret,
            @Value("${app.jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${app.jwt.access-token.expiration-minutes:60}") long accessTokenExpirationMinutes,
            @Value("${app.jwt.refresh-token.expiration-days:30}") long refreshTokenExpirationDays) {

        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Override
    public JwtToken generateAccessToken(Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        return generateAccessToken(email, role);
    }

    @Override
    public JwtToken generateAccessToken(String email, String role) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        String token = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(accessTokenKey)
                .compact();

        log.debug("Generated access token for user: {}", email);
        return new JwtToken(token, issuedAt, expiresAt);
    }

    @Override
    public RefreshToken generateRefreshToken(String email) {
        Instant expiresAt = Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS);

        String token = Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiresAt))
                .signWith(refreshTokenKey)
                .compact();

        log.debug("Generated refresh token for user: {}", email);
        return new RefreshToken(token, expiresAt);
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    @Override
    public String extractRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(accessTokenKey).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(refreshTokenKey).build().parseSignedClaims(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Refresh token is expired: {}", e.getMessage());
            throw new InvalidTokenException("Refresh token expired", e);
        } catch (Exception e) {
            log.error("Refresh token validation error: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token", e);
        }
    }

    @Override
    public Authentication getAuthenticationFromToken(String token) {
        try {
            String email = extractEmailFromToken(token);
            String role = extractRoleFromToken(token);

            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        } catch (Exception e) {
            log.error("Error creating authentication from token", e);
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(refreshTokenKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from refresh token", e);
            throw new InvalidTokenException("Invalid refresh token", e);
        }
    }
}
