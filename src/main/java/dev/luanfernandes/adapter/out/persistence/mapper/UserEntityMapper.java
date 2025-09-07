package dev.luanfernandes.adapter.out.persistence.mapper;

import dev.luanfernandes.adapter.out.persistence.entity.UserJpaEntity;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserDomain toDomain(UserJpaEntity entity) {
        return new UserDomain(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getPassword(),
                entity.getRole(),
                entity.getCreatedAt());
    }

    public UserJpaEntity toEntity(UserDomain domain) {
        return new UserJpaEntity(
                domain.getId().value(),
                domain.getEmail().value(),
                domain.getPassword(),
                domain.getRole(),
                domain.getCreatedAt());
    }
}
