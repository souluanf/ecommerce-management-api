package dev.luanfernandes.application.usecase.auth;

import dev.luanfernandes.domain.dto.RegisterRequest;
import dev.luanfernandes.domain.dto.UserResponse;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.exception.UserAlreadyExistsException;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(RegisterRequest request) {
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

        log.info("Registration successful for user: {}", request.email());

        return UserResponse.from(
                savedUser.getId().value(),
                savedUser.getEmail().value(),
                savedUser.getRole(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt());
    }
}
