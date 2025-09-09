package dev.luanfernandes.adapter.in.web.port.auth;

import static dev.luanfernandes.infrastructure.constants.PathConstants.AUTH_LOGIN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.LoginRequest;
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
public interface LoginPort {

    @Operation(
            tags = "Auth",
            summary = "Login de usuário",
            description = "Autentica usuário com email e senha, retornando tokens JWT de acesso e refresh.")
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
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
                                          "userEmail": "admin@example.com",
                                          "userRole": "ADMIN"
                                        }
                                        """)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_LOGIN)
    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request);
}
