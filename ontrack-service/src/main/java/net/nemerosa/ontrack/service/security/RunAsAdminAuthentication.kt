package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountHolder;
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser;
import net.nemerosa.ontrack.model.security.SecurityRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class RunAsAdminAuthentication extends AbstractAuthenticationToken {

    private final Account account;

    public RunAsAdminAuthentication(OntrackAuthenticatedUser authenticatedUser) {
        super(AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.name()));
        Account account = authenticatedUser.getAccount();
        this.account = Account.of(
                account.getName(),
                account.getFullName(),
                account.getEmail(),
                SecurityRole.ADMINISTRATOR,
                account.getAuthenticationSource()
        ).withId(account.getId());
    }

    @Override
    public Account getDetails() {
        return account;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return (AccountHolder) () -> account;
    }
}
