package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.security.UserService;
import net.nemerosa.ontrack.model.support.PasswordChange;
import net.nemerosa.ontrack.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final SecurityService securityService;
    private final AccountRepository accountRepository;

    // TODO #756 Cleanup
    private final PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    @Autowired
    public UserServiceImpl(SecurityService securityService, AccountRepository accountRepository) {
        this.securityService = securityService;
        this.accountRepository = accountRepository;
    }

    @Override
    public Ack changePassword(PasswordChange input) {
        // Checks the account
        OntrackAuthenticatedUser user = securityService.getCurrentAccount();
        if (user == null) {
            throw new AccessDeniedException("Must be logged to change password.");
        } else if (!user.getAccount().getAuthenticationSource().isAllowingPasswordChange()) {
            throw new AccessDeniedException("Password change is not allowed from ontrack.");
        } else if (!accountRepository.checkPassword(
                user.id(),
                encodedPassword -> passwordEncoder.matches(input.getOldPassword(), encodedPassword)
        )) {
            throw new UserOldPasswordException();
        } else {
            accountRepository.setPassword(
                    user.id(),
                    passwordEncoder.encode(input.getNewPassword())
            );
            return Ack.OK;
        }
    }
}
