package dev.luanfernandes.adapter.in.web.adapter.auth;

import static dev.luanfernandes.domain.enums.UserRole.USER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.auth.RegisterUseCase;
import dev.luanfernandes.domain.dto.RegisterRequest;
import dev.luanfernandes.domain.dto.UserResponse;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.exception.UserAlreadyExistsException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {RegisterAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class RegisterAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @Test
    void shouldRegisterSuccessfully_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "password123", USER);
        UserResponse response = UserResponse.from(
                "123e4567-e89b-12d3-a456-426614174000",
                "newuser@example.com",
                USER,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(registerUseCase.execute(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value(USER.name()));
    }

    @Test
    void shouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123", USER);

        when(registerUseCase.execute(request)).thenThrow(new UserAlreadyExistsException("existing@example.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid-email", "password123", USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_WhenEmptyPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("user@example.com", "", USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_WhenMissingFields() throws Exception {
        String incompleteJson = "{ \"email\": \"user@example.com\" }";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRegisterWithAdminRole_WhenRequested() throws Exception {
        RegisterRequest request = new RegisterRequest("admin@example.com", "password123", UserRole.ADMIN);
        UserResponse response = UserResponse.from(
                "123e4567-e89b-12d3-a456-426614174001",
                "admin@example.com",
                UserRole.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(registerUseCase.execute(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
