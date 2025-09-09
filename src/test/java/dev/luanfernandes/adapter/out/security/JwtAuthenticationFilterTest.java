package dev.luanfernandes.adapter.out.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {

        String validToken = "valid.jwt.token";
        String bearerToken = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getAuthenticationFromToken(validToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(validToken);
        verify(jwtTokenProvider).getAuthenticationFromToken(validToken);
        verify(securityContext).setAuthentication(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @ParameterizedTest
    @MethodSource("invalidAuthorizationHeaderProvider")
    void shouldNotAuthenticateWithInvalidAuthorizationHeaders(String headerValue, String testDescription)
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(headerValue);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(any());
        verify(jwtTokenProvider, never()).getAuthenticationFromToken(any());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    private static Stream<Arguments> invalidAuthorizationHeaderProvider() {
        return Stream.of(
                Arguments.of(null, "null header"),
                Arguments.of("Basic sometoken", "non-Bearer token"),
                Arguments.of("Bearer ", "empty token after Bearer"),
                Arguments.of("", "empty header"),
                Arguments.of("Bearer", "only Bearer prefix"));
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {

        String invalidToken = "invalid.jwt.token";
        String bearerToken = "Bearer " + invalidToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(invalidToken);
        verify(jwtTokenProvider, never()).getAuthenticationFromToken(any());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearSecurityContextWhenExceptionOccurs() throws ServletException, IOException {

        String validToken = "valid.jwt.token";
        String bearerToken = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(validToken)).thenThrow(new RuntimeException("Token validation error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(validToken);
        SecurityContextHolder.clearContext();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainEvenWhenAuthenticationFails() throws ServletException, IOException {

        String validToken = "valid.jwt.token";
        String bearerToken = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getAuthenticationFromToken(validToken))
                .thenThrow(new RuntimeException("Authentication error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(validToken);
        verify(jwtTokenProvider).getAuthenticationFromToken(validToken);
        SecurityContextHolder.clearContext();
        verify(filterChain).doFilter(request, response);
    }
}
