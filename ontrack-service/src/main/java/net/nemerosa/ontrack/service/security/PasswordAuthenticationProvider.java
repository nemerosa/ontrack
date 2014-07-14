package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("password")
public class PasswordAuthenticationProvider extends AbstractOntrackAuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordAuthenticationProvider(AccountService accountService, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        super(accountService);
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
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
    protected Optional<Account> findUser(String username, UsernamePasswordAuthenticationToken authentication) {
        return accountRepository.findUserByNameAndMode(username, "password");
    }
}
