package dev.luanfernandes.application.usecase.auth;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.LoginRequest;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthenticationResponse execute(LoginRequest request) {
        log.info("Attempting login for user: {}", request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            Email email = new Email(request.email());
            UserDomain user =
                    userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("User not found"));

            JwtToken accessToken = jwtTokenProvider.generateAccessToken(authentication);
            RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(request.email());

            log.info("Login successful for user: {}", request.email());

            return AuthenticationResponse.of(
                    accessToken.token(),
                    refreshToken.token(),
                    ChronoUnit.SECONDS.between(accessToken.issuedAt(), accessToken.expiresAt()),
                    user.getEmail().value(),
                    user.getRole().getRoleName());

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", request.email(), e);
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
