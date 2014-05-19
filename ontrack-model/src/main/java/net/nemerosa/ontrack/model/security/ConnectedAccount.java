package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.Entity;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectedAccount {

    private final Account account;

    public static ConnectedAccount none() {
        return new ConnectedAccount(null);
    }

    public static ConnectedAccount of(Account account) {
        Entity.isEntityDefined(account, "Account must be defined");
        return new ConnectedAccount(account);
    }

}
