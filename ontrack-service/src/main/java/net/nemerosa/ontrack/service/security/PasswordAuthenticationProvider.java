package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.AccountUserDetails;
import net.nemerosa.ontrack.model.security.AuthenticatedAccount;
import net.nemerosa.ontrack.model.security.UserSource;
import net.nemerosa.ontrack.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// TODO #756 Disable custom security
//@Component
//@Qualifier("password")
public class PasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements UserSource {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordAuthenticationSourceProvider passwordAuthenticationSourceProvider;

    @Autowired
    public PasswordAuthenticationProvider(AccountService accountService, AccountRepository accountRepository, PasswordEncoder passwordEncoder, PasswordAuthenticationSourceProvider passwordAuthenticationSourceProvider) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordAuthenticationSourceProvider = passwordAuthenticationSourceProvider;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String rawPassword = (String) authentication.getCredentials();
        boolean ok = accountRepository.checkPassword(
                ((AccountUserDetails) userDetails).getAccount().id(),
                encodedPassword -> passwordEncoder.matches(rawPassword, encodedPassword)
        );
        if (!ok) {
            throw new BadCredentialsException("Incorrect password");
        }
    }

    @Override
    public Optional<AccountUserDetails> loadUser(String username) {
        return accountRepository.findUserByNameAndSource(username, passwordAuthenticationSourceProvider)
                .map(AuthenticatedAccount::of)
                .map(accountService::withACL)
                .map(AccountUserDetails::new);
    }

    /**
     * Nothing to do since there is no cache.
     */
    @Override
    public void onLogout(String username) {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return loadUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s cannot be found", username)));
    }

}
