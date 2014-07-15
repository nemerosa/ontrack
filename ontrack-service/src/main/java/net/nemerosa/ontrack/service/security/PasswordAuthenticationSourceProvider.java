package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import org.springframework.stereotype.Component;

@Component
public class PasswordAuthenticationSourceProvider implements AuthenticationSourceProvider {

    private final AuthenticationSource source = AuthenticationSource.of(
            "password",
            "Built-in"
    );

    @Override
    public AuthenticationSource getSource() {
        return source;
    }

}
