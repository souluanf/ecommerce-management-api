package dev.luanfernandes.domain.enums;

import java.util.Set;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public String getRoleName() {
        return authority.substring(5); // Remove "ROLE_" prefix
    }

    public static UserRole fromAuthority(String authority) {
        for (UserRole role : UserRole.values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown authority: " + authority);
    }

    public static UserRole fromRoleName(String roleName) {
        return fromAuthority("ROLE_" + roleName);
    }

    public Set<String> getAuthorities() {
        return Set.of(this.authority);
    }
}
