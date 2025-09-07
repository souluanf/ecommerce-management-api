package dev.luanfernandes.adapter.in.web.adapter.auth;

import dev.luanfernandes.adapter.in.web.port.auth.RegisterPort;
import dev.luanfernandes.application.usecase.auth.RegisterUseCase;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterAdapter implements RegisterPort {

    private final RegisterUseCase registerUseCase;

    @Override
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {
        log.info("Register request received for email: {}", request.email());
        AuthenticationResponse response = registerUseCase.execute(request);
        log.info("Registration successful for email: {}", request.email());
        return ResponseEntity.status(201).body(response);
    }
}
