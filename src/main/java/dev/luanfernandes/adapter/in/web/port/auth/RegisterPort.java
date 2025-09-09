package dev.luanfernandes.adapter.in.web.port.auth;

import static dev.luanfernandes.infrastructure.constants.PathConstants.AUTH_REGISTER;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.RegisterRequest;
import dev.luanfernandes.domain.dto.UserResponse;
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
public interface RegisterPort {

    @Operation(
            tags = "Auth",
            summary = "Registro de usuário",
            description = "Registra novo usuário no sistema com email, senha e role.")
    @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                {
                                  "id": "550e8400-e29b-41d4-a716-446655440000",
                                  "email": "newuser@example.com",
                                  "role": USER.name(),
                                  "createdAt": "2024-01-15T10:30:00",
                                  "updatedAt": "2024-01-15T10:30:00"
                                }
                                """)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário já existe")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_REGISTER)
    ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request);
}
