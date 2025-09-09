package dev.luanfernandes.adapter.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luanfernandes.adapter.out.persistence.entity.UserJpaEntity;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityMapperTest {

    private UserEntityMapper userEntityMapper;

    @BeforeEach
    void setUp() {
        userEntityMapper = new UserEntityMapper();
    }

    @Test
    void shouldMapUserJpaEntityToDomain() {

        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        UserJpaEntity userEntity =
                new UserJpaEntity(userId.toString(), "user@example.com", "hashedPassword", UserRole.USER, createdAt);

        UserDomain userDomain = userEntityMapper.toDomain(userEntity);

        assertThat(userDomain).isNotNull();
        assertThat(userDomain.getId().value()).isEqualTo(userId.toString());
        assertThat(userDomain.getEmail().value()).isEqualTo("user@example.com");
        assertThat(userDomain.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDomain.getRole()).isEqualTo(UserRole.USER);
        assertThat(userDomain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldMapUserDomainToEntity() {

        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        UserDomain userDomain = new UserDomain(
                UserId.of(userId), Email.of("user@example.com"), "hashedPassword", UserRole.USER, createdAt);

        UserJpaEntity userEntity = userEntityMapper.toEntity(userDomain);

        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isEqualTo(userId.toString());
        assertThat(userEntity.getEmail()).isEqualTo("user@example.com");
        assertThat(userEntity.getPassword()).isEqualTo("hashedPassword");
        assertThat(userEntity.getRole()).isEqualTo(UserRole.USER);
        assertThat(userEntity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldMapAdminUser() {

        UUID adminId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        UserJpaEntity adminEntity =
                new UserJpaEntity(adminId.toString(), "admin@example.com", "adminPassword", UserRole.ADMIN, createdAt);

        UserDomain adminDomain = userEntityMapper.toDomain(adminEntity);

        assertThat(adminDomain.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(adminDomain.getEmail().value()).isEqualTo("admin@example.com");
        assertThat(adminDomain.isAdmin()).isTrue();
    }

    @Test
    void shouldMapRegularUser() {

        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        UserDomain userDomain = new UserDomain(
                UserId.of(userId), Email.of("regular@example.com"), "userPassword", UserRole.USER, createdAt);

        UserJpaEntity userEntity = userEntityMapper.toEntity(userDomain);

        assertThat(userEntity.getRole()).isEqualTo(UserRole.USER);
        assertThat(userEntity.getEmail()).isEqualTo("regular@example.com");
    }

    @Test
    void shouldPreserveExactTimestamp() {

        UUID userId = UUID.randomUUID();
        LocalDateTime specificTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45, 123456789);

        UserJpaEntity userEntity =
                new UserJpaEntity(userId.toString(), "time@example.com", "password", UserRole.USER, specificTime);

        UserDomain userDomain = userEntityMapper.toDomain(userEntity);
        UserJpaEntity mappedBackEntity = userEntityMapper.toEntity(userDomain);

        assertThat(userDomain.getCreatedAt()).isEqualTo(specificTime);
        assertThat(mappedBackEntity.getCreatedAt()).isEqualTo(specificTime);
    }

    @Test
    void shouldHandleComplexEmail() {

        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        String complexEmail = "user+test@sub.domain.example.com";

        UserJpaEntity userEntity =
                new UserJpaEntity(userId.toString(), complexEmail, "password123", UserRole.USER, createdAt);

        UserDomain userDomain = userEntityMapper.toDomain(userEntity);

        assertThat(userDomain.getEmail().value()).isEqualTo(complexEmail);
    }

    @Test
    void shouldHandleSpecialCharactersInPassword() {

        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        String specialPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456789!@#$%^&*()";

        UserDomain userDomain = new UserDomain(
                UserId.of(userId), Email.of("special@example.com"), specialPassword, UserRole.USER, createdAt);

        UserJpaEntity userEntity = userEntityMapper.toEntity(userDomain);

        assertThat(userEntity.getPassword()).isEqualTo(specialPassword);
    }

    @Test
    void shouldBidirectionalMappingBeConsistent() {

        UUID originalId = UUID.randomUUID();
        LocalDateTime originalTime = LocalDateTime.now();
        String originalEmail = "consistency@example.com";
        String originalPassword = "originalPassword";
        UserRole originalRole = UserRole.ADMIN;

        UserDomain originalDomain = new UserDomain(
                UserId.of(originalId), Email.of(originalEmail), originalPassword, originalRole, originalTime);

        UserJpaEntity entity = userEntityMapper.toEntity(originalDomain);
        UserDomain mappedBackDomain = userEntityMapper.toDomain(entity);

        assertThat(mappedBackDomain.getId().value()).isEqualTo(originalId.toString());
        assertThat(mappedBackDomain.getEmail().value()).isEqualTo(originalEmail);
        assertThat(mappedBackDomain.getPassword()).isEqualTo(originalPassword);
        assertThat(mappedBackDomain.getRole()).isEqualTo(originalRole);
        assertThat(mappedBackDomain.getCreatedAt()).isEqualTo(originalTime);
    }
}
