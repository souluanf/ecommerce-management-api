package dev.luanfernandes.adapter.in.web.adapter.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.auth.LoginUseCase;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {LoginAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class LoginAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @Test
    void shouldLoginSuccessfully_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin@example.com", "password123");
        AuthenticationResponse response = AuthenticationResponse.of(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access.token",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.token",
                3600L,
                "admin@example.com",
                "ADMIN");

        when(loginUseCase.execute(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.userEmail").value("admin@example.com"))
                .andExpect(jsonPath("$.userRole").value("ADMIN"));
    }

    @Test
    void shouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin@example.com", "wrongpassword");

        when(loginUseCase.execute(request)).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidRequestFormat() throws Exception {
        String invalidJson = "{ \"email\": \"\", \"password\": \"\" }";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_WhenEmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_WhenMissingFields() throws Exception {
        String incompleteJson = "{ \"email\": \"admin@example.com\" }";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteJson))
                .andExpect(status().isBadRequest());
    }
}
