package net.nemerosa.ontrack.service.security.ldap;

import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import org.springframework.stereotype.Component;

@Component
public class LDAPAuthenticationSourceProvider implements AuthenticationSourceProvider {

    private final AuthenticationSource source = AuthenticationSource.of(
            "ldap",
            "LDAP authentication"
    );

    @Override
    public AuthenticationSource getSource() {
        return source;
    }

}
