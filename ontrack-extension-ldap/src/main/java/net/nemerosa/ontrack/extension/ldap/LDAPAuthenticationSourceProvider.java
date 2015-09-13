package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import org.springframework.stereotype.Component;

@Component
public class LDAPAuthenticationSourceProvider implements AuthenticationSourceProvider {

    public static final String LDAP_AUTHENTICATION_SOURCE = "ldap";

    private final AuthenticationSource source = AuthenticationSource.of(
            LDAP_AUTHENTICATION_SOURCE,
            "LDAP authentication"
    );

    @Override
    public AuthenticationSource getSource() {
        return source;
    }

}
