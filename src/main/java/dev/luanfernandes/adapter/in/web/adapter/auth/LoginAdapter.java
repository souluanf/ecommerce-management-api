package dev.luanfernandes.adapter.in.web.adapter.auth;

import dev.luanfernandes.adapter.in.web.port.auth.LoginPort;
import dev.luanfernandes.application.usecase.auth.LoginUseCase;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginAdapter implements LoginPort {

    private final LoginUseCase loginUseCase;

    @Override
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        log.info("Login request received for email: {}", request.email());
        AuthenticationResponse response = loginUseCase.execute(request);
        log.info("Login successful for email: {}", request.email());
        return ResponseEntity.ok(response);
    }
}
