package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

/**
 * Administration management.
 */
@DSL("Administration management")
class Admin {

    private final Ontrack ontrack

    Admin(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    /**
     * Gets the list of accounts
     */
    @DSLMethod("Returns the list of all accounts.")
    List<Account> getAccounts() {
        ontrack.get('accounts').resources.collect {
            new Account(ontrack, it)
        }
    }

    /**
     * Creating or updating an account
     */
    @DSLMethod(value = "Creates or updates an account.", count = 5)
    Account account(String name, String fullName, String email, String password = '', List<String> groupNames = []) {
        // Gets the accounts
        def accounts = ontrack.get('accounts')
        // Gets the groups by name
        def groups = groupNames.collect { findGroupByName(it) }
        // Looks for an existing account
        def account = accounts.resources.find { it.name == name }
        if (account != null) {
            // Update
            new Account(
                    ontrack,
                    ontrack.put(
                            account._update as String,
                            [
                                    name    : name,
                                    fullName: fullName,
                                    email   : email,
                                    password: password,
                                    groups  : groups.collect { it.id },
                            ]
                    )
            )
        } else {
            // Creation
            new Account(
                    ontrack,
                    ontrack.post(
                            accounts._create as String,
                            [
                                    name    : name,
                                    fullName: fullName,
                                    email   : email,
                                    password: password,
                                    groups  : groups.collect { it.id },
                            ]
                    )
            )
        }
    }

    /**
     * Gets the list of groups
     */
    @DSLMethod("Returns the list of all groups.")
    List<AccountGroup> getGroups() {
        ontrack.get('accounts/groups').resources.collect {
            new AccountGroup(ontrack, it)
        }
    }

    /**
     * Creating or updating a group
     */
    @DSLMethod("Creates or updates an account group.")
    AccountGroup accountGroup(String name, String description) {
        // Gets the groups
        def groups = ontrack.get('accounts/groups')
        // Looks for an existing group
        def group = groups.resources.find { it.name == name }
        if (group != null) {
            // Update
            new AccountGroup(
                    ontrack,
                    ontrack.put(
                            group._update as String,
                            [
                                    name       : name,
                                    description: description
                            ]
                    )
            )
        } else {
            // Creation
            new AccountGroup(
                    ontrack,
                    ontrack.post(
                            groups._create as String,
                            [
                                    name       : name,
                                    description: description
                            ]
                    )
            )
        }
    }

    /**
     * Gets the list of LDAP mappings
     */
    @DSLMethod("Gets the list of LDAP mappings.")
    List<GroupMapping> getLdapMappings() {
        ontrack.get('extension/ldap/ldap-mapping').resources.collect { node ->
            new GroupMapping(ontrack, node)
        }
    }

    /**
     * Creates or updates a LDAP mapping
     * @param name LDAP group name
     * @param groupName Group to map to
     * @return Mapping
     */
    @DSLMethod("Creates or updates a LDAP mapping.")
    GroupMapping ldapMapping(String name, String groupName) {
        def mappings = ontrack.get('extension/ldap/ldap-mapping')
        // Group ID from the name
        AccountGroup group = findGroupByName(groupName)
        // Looks for an existing mapping
        def mapping = mappings.resources.find { it.name == name }
        if (mapping != null) {
            // Update
            new GroupMapping(
                    ontrack,
                    ontrack.put(
                            mapping._update as String,
                            [
                                    name : name,
                                    group: group.id
                            ]
                    )
            )
        } else {
            // Creation
            new GroupMapping(
                    ontrack,
                    ontrack.post(
                            mappings._create as String,
                            [
                                    name : name,
                                    group: group.id
                            ]
                    )
            )
        }
    }

    protected AccountGroup findGroupByName(String groupName) {
        def group = getGroups().find { it.name == groupName }
        if (group == null) {
            throw new AccountGroupNameNotFoundException(groupName)
        }
        group
    }

    protected Account findAccountByName(String name) {
        def account = getAccounts().find { it.name == name }
        if (account == null) {
            throw new AccountGroupNameNotFoundException(name)
        }
        account
    }

    /**
     * Sets a global role on an account
     */
    @DSLMethod("Sets a global role on an account. See <<dsl-usecases-security-account-permissions>>.")
    public void setAccountGlobalPermission(String accountName, String globalRole) {
        def account = findAccountByName(accountName)
        ontrack.put(
                "/accounts/permissions/globals/ACCOUNT/${account.id}",
                [
                        role: globalRole
                ]
        )
    }

    /**
     * Gets the list of global roles an account has
     * @param accountName Name of the account to get the permissions for
     * @return List of roles
     */
    @DSLMethod("Gets the list of global roles an account has. See <<dsl-usecases-security-account-permissions>>.")
    List<Role> getAccountGlobalPermissions(String accountName) {
        def account = findAccountByName(accountName)
        ontrack.get("/accounts/permissions/globals").resources
                .findAll { it.target.type == 'ACCOUNT' && it.target.id == account.id }
                .collect {
            new Role(
                    ontrack,
                    it.role
            )
        }
    }

    /**
     * Sets a project role on an account
     */
    @DSLMethod("Sets a project role on an account. See <<dsl-usecases-security-account-permissions>>.")
    public void setAccountProjectPermission(String projectName, String accountName, String projectRole) {
        def project = ontrack.project(projectName)
        def account = findAccountByName(accountName)
        ontrack.put(
                "/accounts/permissions/projects/${project.id}/ACCOUNT/${account.id}",
                [
                        role: projectRole
                ]
        )
    }

    /**
     * Gets the list of roles an account has on a project
     * @param accountName Name of the account to get the permissions for
     * @return List of roles
     */
    @DSLMethod("Gets the list of roles an account has on a project. See <<dsl-usecases-security-account-permissions>>.")
    List<Role> getAccountProjectPermissions(String projectName, String accountName) {
        def project = ontrack.project(projectName)
        def account = findAccountByName(accountName)
        ontrack.get("/accounts/permissions/projects/${project.id}").resources
                .findAll { it.target.type == 'ACCOUNT' && it.target.id == account.id }
                .collect {
            new Role(
                    ontrack,
                    it.role
            )
        }
    }

    /**
     * Sets a global role on an account group
     */
    @DSLMethod("Sets a global role on an account group. See <<dsl-usecases-security-account-group-permissions>>.")
    public void setAccountGroupGlobalPermission(String groupName, String globalRole) {
        def group = findGroupByName(groupName)
        ontrack.put(
                "/accounts/permissions/globals/GROUP/${group.id}",
                [
                        role: globalRole
                ]
        )
    }

    /**
     * Gets the list of global roles an account group has
     * @param groupName Name of the account group to get the permissions for
     * @return List of roles
     */
    @DSLMethod("Gets the list of global roles an account group has. See <<dsl-usecases-security-account-group-permissions>>.")
    List<Role> getAccountGroupGlobalPermissions(String groupName) {
        def group = findGroupByName(groupName)
        ontrack.get("/accounts/permissions/globals").resources
                .findAll { it.target.type == 'GROUP' && it.target.id == group.id }
                .collect {
            new Role(
                    ontrack,
                    it.role
            )
        }
    }

    /**
     * Sets a project role on an account group
     */
    @DSLMethod("Sets a project role on an account group. See <<dsl-usecases-security-account-group-permissions>>.")
    public void setAccountGroupProjectPermission(String projectName, String groupName, String projectRole) {
        def project = ontrack.project(projectName)
        def group = findGroupByName(groupName)
        ontrack.put(
                "/accounts/permissions/projects/${project.id}/GROUP/${group.id}",
                [
                        role: projectRole
                ]
        )
    }

    /**
     * Gets the list of roles an account group has on a project
     * @param groupName Name of the account group to get the permissions for
     * @return List of roles
     */
    @DSLMethod("Gets the list of roles an account group has on a project. See <<dsl-usecases-security-account-group-permissions>>.")
    List<Role> getAccountGroupProjectPermissions(String projectName, String groupName) {
        def project = ontrack.project(projectName)
        def group = findGroupByName(groupName)
        ontrack.get("/accounts/permissions/projects/${project.id}").resources
                .findAll { it.target.type == 'GROUP' && it.target.id == group.id }
                .collect {
            new Role(
                    ontrack,
                    it.role
            )
        }
    }
}
