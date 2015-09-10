package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupContributor;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import net.nemerosa.ontrack.model.security.AuthenticatedAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LDAPAccountGroupContributor implements AccountGroupContributor {

    private final AccountGroupMappingService accountGroupMappingService;

    @Autowired
    public LDAPAccountGroupContributor(AccountGroupMappingService accountGroupMappingService) {
        this.accountGroupMappingService = accountGroupMappingService;
    }

    @Override
    public Collection<AccountGroup> collectGroups(@NotNull AuthenticatedAccount authenticatedAccount) {
        // Gets the list of LDAP groups from the account
        Collection<String> ldapGroups = getLdapGroups(authenticatedAccount);
        // Maps them to the account groups
        return ldapGroups.stream()
                .flatMap(ldapGroup -> accountGroupMappingService.getGroups("ldap", ldapGroup).stream())
                .collect(Collectors.toList());
    }

    public static Collection<String> getLdapGroups(@NotNull AuthenticatedAccount authenticatedAccount) {
        UserDetails userDetails = authenticatedAccount.getUserDetails();
        if (userDetails instanceof ExtendedLDAPUserDetails) {
            return ((ExtendedLDAPUserDetails) userDetails).getGroups();
        } else {
            return Collections.emptyList();
        }
    }
}
