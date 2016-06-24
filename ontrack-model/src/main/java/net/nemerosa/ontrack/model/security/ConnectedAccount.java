package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.support.Action;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConnectedAccount {

    private final boolean authenticationRequired;
    private final Account account;
    private final List<Action> actions = new ArrayList<>();

    public static ConnectedAccount none(boolean authenticationRequired) {
        return new ConnectedAccount(authenticationRequired, null);
    }

    public static ConnectedAccount of(boolean authenticationRequired, Account account) {
        Entity.isEntityDefined(account, "Account must be defined");
        return new ConnectedAccount(authenticationRequired, account);
    }

    public boolean isLogged() {
        return account != null;
    }

    public ConnectedAccount add(Action action) {
        actions.add(action);
        return this;
    }

}
