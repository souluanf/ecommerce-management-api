package dev.luanfernandes.adapter.in.web.port.auth;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
public interface RegisterPort {

    @Operation(
            tags = "Auth",
            summary = "Registro de usuário",
            description = "Registra novo usuário no sistema com email, senha e role.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Usuário registrado com sucesso",
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
                                  "userEmail": "newuser@example.com",
                                  "userRole": "USER"
                                }
                                """))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário já existe"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping(AUTH_REGISTER)
    ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request);
}
