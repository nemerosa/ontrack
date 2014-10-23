package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

import java.io.Serializable;

/**
 * Authentication source for an account or group.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationSource implements Serializable {

    private final String id;
    private final String name;
    @Wither
    private final boolean allowingPasswordChange;

    public static AuthenticationSource of(String id, String name) {
        return new AuthenticationSource(id, name, false);
    }

    /**
     * Authentication source used for tests
     */
    public static AuthenticationSource none() {
        return of("none", "Not defined");
    }
}
