package dev.luanfernandes.adapter.in.web.port.auth;

import static dev.luanfernandes.infrastructure.constants.PathConstants.AUTH_REFRESH;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
public interface RefreshTokenPort {

    @Operation(
            tags = "Auth",
            summary = "Renovar token de acesso",
            description = "Gera novo token de acesso usando o refresh token.")
    @ApiResponse(
            responseCode = "200",
            description = "Token renovado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                            {
                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                              "tokenType": "Bearer",
                              "expiresIn": 3600,
                              "userEmail": "user@example.com",
                              "userRole": USER.name()
                            }
                            """)))
    @ApiResponse(responseCode = "400", description = "Refresh token inválido")
    @ApiResponse(responseCode = "401", description = "Refresh token expirado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_REFRESH)
    ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);
}
