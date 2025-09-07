package dev.luanfernandes.adapter.out.security;

import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        try {
            Email emailVO = new Email(email);
            UserDomain user = userRepository
                    .findByEmail(emailVO)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            Collection<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));

            UserDetails userDetails = User.builder()
                    .username(user.getEmail().value())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

            log.debug("User loaded successfully: {} with role: {}", email, user.getRole());
            return userDetails;

        } catch (Exception e) {
            log.error("Error loading user by email: {}", email, e);
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}
