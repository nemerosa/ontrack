package net.nemerosa.ontrack.service.security.ldap;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.repository.AccountRepository;
import net.nemerosa.ontrack.service.security.AbstractOntrackAuthenticationProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("ldap")
public class LDAPAuthenticationProvider extends AbstractOntrackAuthenticationProvider {

    private final LDAPProviderFactory ldapProviderFactory;
    private final LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public LDAPAuthenticationProvider(
            AccountService accountService,
            LDAPProviderFactory ldapProviderFactory,
            LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider,
            AccountRepository accountRepository,
            SecurityService securityService,
            ApplicationLogService applicationLogService) {
        super(accountService);
        this.ldapProviderFactory = ldapProviderFactory;
        this.ldapAuthenticationSourceProvider = ldapAuthenticationSourceProvider;
        this.accountRepository = accountRepository;
        this.securityService = securityService;
        this.applicationLogService = applicationLogService;
    }

    @Override
    protected Optional<Account> findUser(String username, UsernamePasswordAuthenticationToken authentication) {
        // Gets the (cached) provider
        LdapAuthenticationProvider ldapAuthenticationProvider = ldapProviderFactory.getProvider();
        // If not enabled, cannot authenticate!
        if (ldapAuthenticationProvider == null) {
            return Optional.empty();
        }
        // LDAP connection
        else {
            Authentication ldapAuthentication;
            try {
                ldapAuthentication = ldapAuthenticationProvider.authenticate(authentication);
            } catch (Exception ex) {
                // Cannot use the LDAP, logs the error
                applicationLogService.error(
                        ex,
                        LDAPAuthenticationProvider.class,
                        "ldap",
                        "",
                        "Cannot authenticate using the LDAP"
                );
                // Rejects the authentication
                return Optional.empty();
            }
            if (ldapAuthentication != null && ldapAuthentication.isAuthenticated()) {
                // Gets the account name
                final String name = ldapAuthentication.getName();
                // Gets any existing account
                Optional<Account> existingAccount = accountRepository.findUserByNameAndSource(username, ldapAuthenticationSourceProvider);
                if (!existingAccount.isPresent()) {
                    // If not found, auto-registers the account using the LDAP details
                    Object principal = ldapAuthentication.getPrincipal();
                    if (principal instanceof ExtendedLDAPUserDetails) {
                        ExtendedLDAPUserDetails details = (ExtendedLDAPUserDetails) principal;
                        // Auto-registration if email is OK
                        if (StringUtils.isNotBlank(details.getEmail())) {
                            // Registration
                            return securityService.asAdmin(() -> Optional.of(
                                            accountRepository.newAccount(
                                                    Account.of(
                                                            name,
                                                            details.getFullName(),
                                                            details.getEmail(),
                                                            SecurityRole.USER,
                                                            ldapAuthenticationSourceProvider.getSource()
                                                    )
                                            )
                                    )
                            );
                        } else {
                            // Temporary account
                            return Optional.of(
                                    Account.of(
                                            name,
                                            details.getFullName(),
                                            "",
                                            SecurityRole.USER,
                                            ldapAuthenticationSourceProvider.getSource()
                                    )
                            );
                        }
                    } else {
                        // Temporary account
                        return Optional.of(
                                Account.of(
                                        name,
                                        name,
                                        "",
                                        SecurityRole.USER,
                                        ldapAuthenticationSourceProvider.getSource()
                                )
                        );
                    }
                } else {
                    return existingAccount;
                }
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }
}
