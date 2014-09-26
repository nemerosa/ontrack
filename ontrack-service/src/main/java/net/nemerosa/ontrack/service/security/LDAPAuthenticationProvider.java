package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("ldap")
public class LDAPAuthenticationProvider extends AbstractOntrackAuthenticationProvider {

    @Autowired
    public LDAPAuthenticationProvider(AccountService accountService) {
        super(accountService);
    }

    @Override
    protected Optional<Account> findUser(String username, UsernamePasswordAuthenticationToken authentication) {
        // FIXME Method net.nemerosa.ontrack.service.security.LDAPAuthenticationProvider.findUser
        return Optional.empty();
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // FIXME Method net.nemerosa.ontrack.service.security.LDAPAuthenticationProvider.additionalAuthenticationChecks

    }
}
