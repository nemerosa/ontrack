package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@Qualifier("ldap")
public class LDAPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final AccountService accountService;
    private final LDAPProviderFactory ldapProviderFactory;
    private final LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider;
    private final SecurityService securityService;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public LDAPAuthenticationProvider(
            AccountService accountService,
            LDAPProviderFactory ldapProviderFactory,
            LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider,
            SecurityService securityService,
            ApplicationLogService applicationLogService) {
        this.accountService = accountService;
        this.ldapProviderFactory = ldapProviderFactory;
        this.ldapAuthenticationSourceProvider = ldapAuthenticationSourceProvider;
        this.securityService = securityService;
        this.applicationLogService = applicationLogService;
    }

    protected Optional<AuthenticatedAccount> findUser(String username, UsernamePasswordAuthenticationToken authentication) {
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
                // If not found, auto-registers the account using the LDAP details
                ExtendedLDAPUserDetails userDetails;
                Object principal = ldapAuthentication.getPrincipal();
                if (principal instanceof ExtendedLDAPUserDetails) {
                    userDetails = (ExtendedLDAPUserDetails) principal;
                } else {
                    userDetails = null;
                }
                // Gets any existing account
                Optional<Account> existingAccount = securityService.asAdmin(() ->
                                accountService.findUserByNameAndSource(username, ldapAuthenticationSourceProvider)
                );
                if (!existingAccount.isPresent()) {
                    // If not found, auto-registers the account using the LDAP details
                    if (userDetails != null) {
                        // Auto-registration if email is OK
                        if (StringUtils.isNotBlank(userDetails.getEmail())) {
                            // Registration
                            return securityService.asAdmin(() -> Optional.of(
                                            new AuthenticatedAccount(
                                                    accountService.create(
                                                            new AccountInput(
                                                                    name,
                                                                    userDetails.getFullName(),
                                                                    userDetails.getEmail(),
                                                                    "",
                                                                    Collections.emptyList()
                                                            ),
                                                            LDAPAuthenticationSourceProvider.LDAP_AUTHENTICATION_SOURCE
                                                    ),
                                                    userDetails
                                            )
                                    )
                            );
                        } else {
                            // Temporary account
                            return Optional.of(
                                    AuthenticatedAccount.of(
                                            Account.of(
                                                    name,
                                                    userDetails.getFullName(),
                                                    "",
                                                    SecurityRole.USER,
                                                    ldapAuthenticationSourceProvider.getSource()
                                            )
                                    )
                            );
                        }
                    } else {
                        // Temporary account
                        return Optional.of(
                                AuthenticatedAccount.of(
                                        Account.of(
                                                name,
                                                name,
                                                "",
                                                SecurityRole.USER,
                                                ldapAuthenticationSourceProvider.getSource()
                                        )
                                )
                        );
                    }
                } else {
                    return existingAccount.map(
                            account -> new AuthenticatedAccount(account, userDetails)
                    );
                }
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return findUser(username, authentication)
                .map(accountService::withACL)
                .map(AccountUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s cannot be found", username)));
    }
}
