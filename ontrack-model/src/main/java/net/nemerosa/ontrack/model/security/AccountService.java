package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;

import java.util.Collection;
import java.util.List;

/**
 * Management of accounts.
 */
public interface AccountService {

    /**
     * Completes an account with the list of its authorisations.
     *
     * @param raw Account without authorisations
     * @return Account with authorisations
     */
    Account withACL(Account raw);

    /**
     * List of accounts
     */
    List<Account> getAccounts();

    /**
     * Creates a built-in account
     */
    Account create(AccountInput input);

    /**
     * Gets an account using its ID
     */
    Account getAccount(ID accountId);

    /**
     * Updating an existing account
     *
     * @param accountId ID of the account to update
     * @param input     Update data
     * @return Updated account
     */
    Account updateAccount(ID accountId, AccountInput input);

    /**
     * Deletes an account
     */
    Ack deleteAccount(ID accountId);

    /**
     * List of account groups
     */
    List<AccountGroup> getAccountGroups();

    /**
     * Creation of an account group
     */
    AccountGroup createGroup(NameDescription nameDescription);

    /**
     * Getting a group
     */
    AccountGroup getAccountGroup(ID groupId);

    /**
     * Updating a group
     */
    AccountGroup updateGroup(ID groupId, NameDescription input);

    /**
     * Deleting a group.
     */
    Ack deleteGroup(ID groupId);

    /**
     * Returns the list of <i>all</i> account groups, together with the status for their selection
     * in the given account ID. The account ID may not be {@link net.nemerosa.ontrack.model.structure.ID#isSet() set},
     * meaning that none of the groups will be selected.
     *
     * @param accountId Account ID to get the groups for, or not {@link net.nemerosa.ontrack.model.structure.ID#isSet() set}
     * @return List of groups with their selection status
     */
    List<AccountGroupSelection> getAccountGroupsForSelection(ID accountId);

    /**
     * Searches for a list of permission targets using the <code>token</code> as a part in the
     * name of the permission target.
     */
    Collection<PermissionTarget> searchPermissionTargets(String token);

    /**
     * Saves the permission for the {@link net.nemerosa.ontrack.model.security.PermissionTarget} defined
     * by the <code>type</code> and <code>id</code>.
     */
    Ack saveGlobalPermission(PermissionTargetType type, int id, PermissionInput input);
}
