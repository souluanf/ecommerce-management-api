package dev.luanfernandes.application.usecase.auth;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RegisterRequest;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.exception.UserAlreadyExistsException;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse execute(RegisterRequest request) {
        log.info("Attempting registration for user: {}", request.email());

        Email email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(request.email());
        }

        UserDomain user = new UserDomain(
                UserId.generate(),
                email,
                passwordEncoder.encode(request.password()),
                request.role(),
                LocalDateTime.now());

        UserDomain savedUser = userRepository.save(user);

        JwtToken accessToken = jwtTokenProvider.generateAccessToken(
                savedUser.getEmail().value(), savedUser.getRole().getAuthority());
        RefreshToken refreshToken =
                jwtTokenProvider.generateRefreshToken(savedUser.getEmail().value());

        log.info("Registration successful for user: {}", request.email());

        return AuthenticationResponse.of(
                accessToken.token(),
                refreshToken.token(),
                ChronoUnit.SECONDS.between(accessToken.issuedAt(), accessToken.expiresAt()),
                savedUser.getEmail().value(),
                savedUser.getRole().getRoleName());
    }
}
