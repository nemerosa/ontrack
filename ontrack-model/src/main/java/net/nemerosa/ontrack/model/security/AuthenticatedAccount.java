package net.nemerosa.ontrack.model.security;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class AuthenticatedAccount {

    private final Account account;
    private final UserDetails userDetails;

    public static AuthenticatedAccount of(Account account) {
        return new AuthenticatedAccount(account, new AccountUserDetails(account));
    }

}
