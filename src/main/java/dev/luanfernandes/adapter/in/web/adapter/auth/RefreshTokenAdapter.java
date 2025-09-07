package dev.luanfernandes.adapter.in.web.adapter.auth;

import dev.luanfernandes.adapter.in.web.port.auth.RefreshTokenPort;
import dev.luanfernandes.application.usecase.auth.RefreshTokenUseCase;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshTokenAdapter implements RefreshTokenPort {

    private final RefreshTokenUseCase refreshTokenUseCase;

    @Override
    public ResponseEntity<AuthenticationResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request received");
        AuthenticationResponse response = refreshTokenUseCase.execute(request);
        log.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }
}
