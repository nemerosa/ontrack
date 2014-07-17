package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountHolder;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class RunAsAdminAuthentication extends AbstractAuthenticationToken {

    private static final Account RUNAS_ACCOUNT = Account.of(
            "runas_admin",
            "Run-as-administrator",
            "",
            SecurityRole.ADMINISTRATOR,
            AuthenticationSource.of("runas", "Run-as authentication")
    ).withId(ID.of(1));

    private final Account account;

    public RunAsAdminAuthentication(Account account) {
        super(AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.name()));
        if (account == null) {
            this.account = RUNAS_ACCOUNT;
        } else {
            this.account = Account.of(
                    account.getName(),
                    account.getFullName(),
                    account.getEmail(),
                    SecurityRole.ADMINISTRATOR,
                    account.getAuthenticationSource()
            ).withId(account.getId());
        }
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
