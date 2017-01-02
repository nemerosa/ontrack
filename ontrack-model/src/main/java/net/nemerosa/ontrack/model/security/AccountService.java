package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    Account withACL(AuthenticatedAccount raw);

    /**
     * List of accounts
     */
    List<Account> getAccounts();

    /**
     * Creates a built-in account
     */
    Account create(AccountInput input);

    /**
     * Creates an account and allows for further customisation
     *
     * @param input                    Account data
     * @param authenticationSourceMode Authentication mode
     * @see AuthenticationSourceService#getAuthenticationSource(String)
     */
    Account create(AccountInput input, String authenticationSourceMode);

    /**
     * Looks for an account using its user name and his authentication source.
     *
     * @param username       User name
     * @param sourceProvider Provider of the source
     * @return Account
     */
    Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider);

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

    /**
     * Gets the list of global permissions.
     */
    Collection<GlobalPermission> getGlobalPermissions();

    /**
     * Deletes a permission
     */
    Ack deleteGlobalPermission(PermissionTargetType type, int id);

    /**
     * Gets the list of permissions for a project.
     *
     * @param projectId ID of the project
     */
    Collection<ProjectPermission> getProjectPermissions(ID projectId);

    /**
     * Saves the project permission for the {@link net.nemerosa.ontrack.model.security.PermissionTarget} defined
     * by the <code>type</code> and <code>id</code>.
     */
    Ack saveProjectPermission(ID projectId, PermissionTargetType type, int id, PermissionInput input);

    /**
     * Deletes a project permission.
     */
    Ack deleteProjectPermission(ID projectId, PermissionTargetType type, int id);

    /**
     * Gets the list of project permissions for a given account
     *
     * @param account Account to get the permissions for
     * @return List of project roles
     */
    Collection<ProjectRoleAssociation> getProjectPermissionsForAccount(Account account);

    /**
     * Gets the optional global role for an account
     *
     * @param account Account
     * @return Optional role
     */
    Optional<GlobalRole> getGlobalRoleForAccount(Account account);

    /**
     * Gets the list of accounts associated with this account group.
     *
     * @param accountGroup Account group
     * @return List of accounts
     */
    List<Account> getAccountsForGroup(AccountGroup accountGroup);

    /**
     * Gets the optional global role for an account group.
     *
     * @param group Account group
     * @return Optional role
     */
    Optional<GlobalRole> getGlobalRoleForAccountGroup(AccountGroup group);
}
