package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.AccountUserDetails;
import net.nemerosa.ontrack.model.security.AuthenticatedAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public abstract class AbstractOntrackAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final AccountService accountService;

    protected AbstractOntrackAuthenticationProvider(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Optional<AuthenticatedAccount> t = findUser(username, authentication);
        return t
                .map(accountService::withACL)
                .map(AccountUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s cannot be found", username)));
    }

    protected abstract Optional<AuthenticatedAccount> findUser(String username, UsernamePasswordAuthenticationToken authentication);
}
