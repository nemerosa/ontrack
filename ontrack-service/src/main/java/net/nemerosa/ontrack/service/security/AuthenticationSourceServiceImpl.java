package net.nemerosa.ontrack.service.security;

import com.google.common.collect.Maps;
import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceProviderNotFoundException;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import net.nemerosa.ontrack.model.security.AuthenticationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationSourceServiceImpl implements AuthenticationSourceService {

    private final Map<String, ? extends AuthenticationSourceProvider> providers;

    @Autowired
    public AuthenticationSourceServiceImpl(Collection<? extends AuthenticationSourceProvider> providers) {
        this.providers = Maps.uniqueIndex(
                providers,
                p -> p.getSource().getId()
        );
    }

    @Override
    public AuthenticationSourceProvider getAuthenticationSourceProvider(String mode)
            throws AuthenticationSourceProviderNotFoundException {
        return Optional.ofNullable(providers.get(mode)).orElseThrow(
                () -> new AuthenticationSourceProviderNotFoundException(mode)
        );
    }

}
