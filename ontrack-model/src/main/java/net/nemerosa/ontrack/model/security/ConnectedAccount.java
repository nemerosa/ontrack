package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.Entity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectedAccount {

    private final Account account;
    private final List<Action> actions = new ArrayList<>();

    public static ConnectedAccount none() {
        return new ConnectedAccount(null);
    }

    public static ConnectedAccount of(Account account) {
        Entity.isEntityDefined(account, "Account must be defined");
        return new ConnectedAccount(account);
    }

    public ConnectedAccount add(Action action) {
        actions.add(action);
        return this;
    }

}
