package net.nemerosa.ontrack.service.security.ldap;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.repository.AccountRepository;
import net.nemerosa.ontrack.service.security.AbstractOntrackAuthenticationProvider;
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

    @Autowired
    public LDAPAuthenticationProvider(
            AccountService accountService,
            LDAPProviderFactory ldapProviderFactory,
            LDAPAuthenticationSourceProvider ldapAuthenticationSourceProvider,
            AccountRepository accountRepository) {
        super(accountService);
        this.ldapProviderFactory = ldapProviderFactory;
        this.ldapAuthenticationSourceProvider = ldapAuthenticationSourceProvider;
        this.accountRepository = accountRepository;
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
            Authentication ldapAuthentication = ldapAuthenticationProvider.authenticate(authentication);
            if (ldapAuthentication != null && ldapAuthentication.isAuthenticated()) {
                // Gets the account name
                final String name = ldapAuthentication.getName();
                // Gets any existing account
                Optional<Account> existingAccount = accountRepository.findUserByNameAndSource(username, ldapAuthenticationSourceProvider);
                if (!existingAccount.isPresent()) {
                    // TODO If not found, auto-registers the account using the LDAP details
//                    Object principal = ldapAuthentication.getPrincipal();
//                    if (principal instanceof PersonLDAPUserDetails) {
//                        final PersonLDAPUserDetails details = (PersonLDAPUserDetails) principal;
//                        // Auto-registration if email is OK
//                        if (StringUtils.isNotBlank(details.getEmail())) {
//                            // Registration
//                            account = securityUtils.asAdmin(new Callable<Account>() {
//                                @Override
//                                public Account call() throws Exception {
//                                    ID id = accountService.createAccount(new AccountCreationForm(
//                                            name,
//                                            details.getFullName(),
//                                            details.getEmail(),
//                                            SecurityRoles.USER,
//                                            "ldap",
//                                            "",
//                                            ""
//                                    ));
//                                    // Created account
//                                    return accountService.getAccount(id.getValue());
//                                }
//                            });
//                        } else {
//                            // Temporary account
//                            account = new Account(0, name, details.getFullName(), "", SecurityRoles.USER, "ldap", Locale.ENGLISH);
//                        }
//                    } else {
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
//                    }
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
        // FIXME Method net.nemerosa.ontrack.service.security.LDAPAuthenticationProvider.additionalAuthenticationChecks

    }
}
