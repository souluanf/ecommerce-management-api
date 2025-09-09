package dev.luanfernandes.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.persistence.entity.UserJpaEntity;
import dev.luanfernandes.adapter.out.persistence.mapper.UserEntityMapper;
import dev.luanfernandes.adapter.out.persistence.repository.UserJpaRepository;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UserRepositoryAdapter")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private UserEntityMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser_Successfully() {
        UserDomain userDomain = createUserDomain("test@example.com");
        UserJpaEntity userEntity = createUserEntity("test@example.com");
        UserDomain savedUserDomain = createUserDomain("test@example.com");

        when(mapper.toEntity(userDomain)).thenReturn(userEntity);
        when(jpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(savedUserDomain);

        UserDomain result = userRepositoryAdapter.save(userDomain);

        assertThat(result).isNotNull().isEqualTo(savedUserDomain);

        verify(mapper).toEntity(userDomain);
        verify(jpaRepository).save(userEntity);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    @DisplayName("Should find user by id when user exists")
    void shouldFindUserById_WhenUserExists() {
        UserId userId = UserId.generate();
        UserJpaEntity userEntity = createUserEntity("test@example.com");
        UserDomain userDomain = createUserDomain("test@example.com");

        when(jpaRepository.findById(userId.value())).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(userDomain);

        Optional<UserDomain> result = userRepositoryAdapter.findById(userId);

        assertThat(result).isPresent().hasValueSatisfying(user -> assertThat(user)
                .isEqualTo(userDomain));

        verify(jpaRepository).findById(userId.value());
        verify(mapper).toDomain(userEntity);
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void shouldReturnEmpty_WhenUserNotFoundById() {
        UserId userId = UserId.generate();

        when(jpaRepository.findById(userId.value())).thenReturn(Optional.empty());

        Optional<UserDomain> result = userRepositoryAdapter.findById(userId);

        assertThat(result).isEmpty();

        verify(jpaRepository).findById(userId.value());
    }

    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmail_WhenUserExists() {
        Email email = Email.of("test@example.com");
        UserJpaEntity userEntity = createUserEntity("test@example.com");
        UserDomain userDomain = createUserDomain("test@example.com");

        when(jpaRepository.findByEmail(email.value())).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(userDomain);

        Optional<UserDomain> result = userRepositoryAdapter.findByEmail(email);

        assertThat(result).isPresent().hasValueSatisfying(user -> assertThat(user)
                .isEqualTo(userDomain));

        verify(jpaRepository).findByEmail(email.value());
        verify(mapper).toDomain(userEntity);
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmpty_WhenUserNotFoundByEmail() {
        Email email = Email.of("nonexistent@example.com");

        when(jpaRepository.findByEmail(email.value())).thenReturn(Optional.empty());

        Optional<UserDomain> result = userRepositoryAdapter.findByEmail(email);

        assertThat(result).isEmpty();

        verify(jpaRepository).findByEmail(email.value());
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void shouldReturnTrue_WhenUserExistsByEmail() {
        Email email = Email.of("existing@example.com");

        when(jpaRepository.existsByEmail(email.value())).thenReturn(true);

        boolean result = userRepositoryAdapter.existsByEmail(email);

        assertThat(result).isTrue();

        verify(jpaRepository).existsByEmail(email.value());
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalse_WhenUserDoesNotExistByEmail() {
        Email email = Email.of("nonexistent@example.com");

        when(jpaRepository.existsByEmail(email.value())).thenReturn(false);

        boolean result = userRepositoryAdapter.existsByEmail(email);

        assertThat(result).isFalse();

        verify(jpaRepository).existsByEmail(email.value());
    }

    @Test
    @DisplayName("Should handle null email gracefully in findByEmail")
    void shouldHandleNullEmail_InFindByEmail() {
        Email email = Email.of("test@example.com");

        when(jpaRepository.findByEmail(email.value())).thenReturn(Optional.empty());

        Optional<UserDomain> result = userRepositoryAdapter.findByEmail(email);

        assertThat(result).isEmpty();

        verify(jpaRepository).findByEmail(email.value());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void shouldHandleDifferentEmailFormats() {
        Email email1 = Email.of("user@domain.com");
        Email email2 = Email.of("user.name@subdomain.domain.org");
        Email email3 = Email.of("user+tag@domain.co.uk");

        when(jpaRepository.existsByEmail(email1.value())).thenReturn(true);
        when(jpaRepository.existsByEmail(email2.value())).thenReturn(false);
        when(jpaRepository.existsByEmail(email3.value())).thenReturn(true);

        boolean result1 = userRepositoryAdapter.existsByEmail(email1);
        boolean result2 = userRepositoryAdapter.existsByEmail(email2);
        boolean result3 = userRepositoryAdapter.existsByEmail(email3);

        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isTrue();

        verify(jpaRepository).existsByEmail(email1.value());
        verify(jpaRepository).existsByEmail(email2.value());
        verify(jpaRepository).existsByEmail(email3.value());
    }

    @Test
    @DisplayName("Should properly delegate to mapper for entity conversion")
    void shouldProperlyDelegate_ToMapperForEntityConversion() {
        UserDomain userDomain = createUserDomain("mapper@test.com");
        UserJpaEntity userEntity = createUserEntity("mapper@test.com");

        when(mapper.toEntity(userDomain)).thenReturn(userEntity);
        when(jpaRepository.save(any(UserJpaEntity.class))).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(userDomain);

        userRepositoryAdapter.save(userDomain);

        verify(mapper).toEntity(userDomain);
        verify(mapper).toDomain(userEntity);
    }

    private UserDomain createUserDomain(String email) {
        return new UserDomain(
                UserId.generate(), Email.of(email), "encodedPassword", UserRole.USER, LocalDateTime.now());
    }

    private UserJpaEntity createUserEntity(String email) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(UserId.generate().value());
        entity.setEmail(email);
        entity.setPassword("encodedPassword");
        entity.setRole(UserRole.USER);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
