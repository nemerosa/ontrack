package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Management of accounts.
 */
public interface AccountService {

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
     * @param input                Account data
     * @param authenticationSource Authentication source
     */
    Account create(AccountInput input, AuthenticationSource authenticationSource);

    /**
     * Gets an account using its ID
     */
    Account getAccount(ID accountId);

    /**
     * Gets the groups for an account
     */
    List<AccountGroup> getGroupsForAccount(ID accountId);

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
    AccountGroup createGroup(AccountGroupInput input);

    /**
     * Getting a group
     */
    AccountGroup getAccountGroup(ID groupId);

    /**
     * Updating a group
     */
    AccountGroup updateGroup(ID groupId, AccountGroupInput input);

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
     *
     * @param type  Group or account level permission
     * @param id    ID of the group or account
     * @param input Permissions to set
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

    /**
     * Gets the list of project permissions for a given account group
     *
     * @param group Account group to get the permissions for
     * @return List of project roles
     */
    Collection<ProjectRoleAssociation> getProjectPermissionsForAccountGroup(AccountGroup group);

    /**
     * List of groups having the given global role
     *
     * @param globalRole Global role to search for
     * @return List of matching account groups
     */
    Collection<AccountGroup> findAccountGroupsByGlobalRole(GlobalRole globalRole);

    /**
     * List of accounts having the given global role
     *
     * @param globalRole Global role to search for
     * @return List of matching accounts
     */
    Collection<Account> findAccountsByGlobalRole(GlobalRole globalRole);

    /**
     * List of groups having the given project role
     *
     * @param project     Project
     * @param projectRole Role to search for
     * @return List of matching account groups
     */
    Collection<AccountGroup> findAccountGroupsByProjectRole(Project project, ProjectRole projectRole);

    /**
     * List of accounts having the given project role
     *
     * @param project     Project
     * @param projectRole Role to search for
     * @return List of matching accounts
     */
    Collection<Account> findAccountsByProjectRole(Project project, ProjectRole projectRole);

    /**
     * Finds an account using its name.
     *
     * @param username Name to look for
     * @return Account or null if not found
     */
    @Nullable
    Account findAccountByName(@NotNull String username);

    /**
     * Finds an account group using its name.
     *
     * @param name Name to look for
     * @return Account group or null if not found
     */
    @Nullable
    AccountGroup findAccountGroupByName(@NotNull String name);

    /**
     * Checks if an account ID exists
     */
    boolean doesAccountIdExist(@NotNull ID id);

    /**
     * Deletes all accounts having the given source.
     *
     * @param source Source to delete accounts from
     */
    void deleteAccountBySource(@NotNull AuthenticationSource source);

    /**
     * Disabled / enables an account
     */
    void setAccountDisabled(@NotNull ID id, boolean disabled);

    /**
     * Locks / unlocks an account
     */
    void setAccountLocked(@NotNull ID id, boolean locked);
}
