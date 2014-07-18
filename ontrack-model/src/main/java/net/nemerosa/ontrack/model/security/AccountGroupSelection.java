package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.support.Selectable;

/**
 * Defines the selection of an {@link net.nemerosa.ontrack.model.security.AccountGroup}.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountGroupSelection implements Selectable {

    private final int id;
    private final String name;
    private final boolean selected;

    public static AccountGroupSelection of(AccountGroup group, boolean selected) {
        return new AccountGroupSelection(group.id(), group.getName(), selected);
    }
}
