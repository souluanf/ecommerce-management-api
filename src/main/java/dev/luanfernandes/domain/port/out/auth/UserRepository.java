package dev.luanfernandes.domain.port.out.auth;

import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.UserId;
import java.util.Optional;

public interface UserRepository {

    UserDomain save(UserDomain user);

    Optional<UserDomain> findById(UserId id);

    Optional<UserDomain> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
