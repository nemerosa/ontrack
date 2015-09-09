package net.nemerosa.ontrack.model.security;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Contributes a list of groups to an account.
 */
public interface AccountGroupContributor {

    /**
     * Collects the list of groups for this account
     *
     * @param account Account to collect groups for
     * @return List of groups (can be empty but not null)
     */
    Collection<AccountGroup> collectGroups(@NotNull Account account);

}
