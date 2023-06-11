package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.preferences.Preferences;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.support.Action;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConnectedAccount {

    private final Account account;
    private final Preferences preferences;
    private final List<Action> actions = new ArrayList<>();

    public static ConnectedAccount none() {
        return new ConnectedAccount(null, new Preferences());
    }

    public static ConnectedAccount of(Account account, Preferences preferences) {
        Entity.isEntityDefined(account, "Account must be defined");
        return new ConnectedAccount(account, preferences);
    }

    public boolean isLogged() {
        return account != null;
    }

    public ConnectedAccount add(Action action) {
        actions.add(action);
        return this;
    }

}
