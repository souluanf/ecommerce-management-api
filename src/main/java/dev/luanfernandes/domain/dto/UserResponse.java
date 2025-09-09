package dev.luanfernandes.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.luanfernandes.domain.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String email,
        UserRole role,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {

    public static UserResponse from(
            String id, String email, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserResponse(id, email, role, createdAt, updatedAt);
    }
}
