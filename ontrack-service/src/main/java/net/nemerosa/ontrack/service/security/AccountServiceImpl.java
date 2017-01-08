package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountDefaultAdminCannotDeleteException;
import net.nemerosa.ontrack.model.exceptions.AccountDefaultAdminCannotUpdateNameException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.repository.AccountGroupRepository;
import net.nemerosa.ontrack.repository.AccountRepository;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final RoleRepository roleRepository;
    private final RolesService rolesService;
    private final AccountRepository accountRepository;
    private final AccountGroupRepository accountGroupRepository;
    private final SecurityService securityService;
    private final AuthenticationSourceService authenticationSourceService;
    private final PasswordEncoder passwordEncoder;
    private Collection<AccountGroupContributor> accountGroupContributors = Collections.emptyList();

    @Autowired
    public AccountServiceImpl(
            RoleRepository roleRepository,
            RolesService rolesService,
            AccountRepository accountRepository,
            AccountGroupRepository accountGroupRepository,
            SecurityService securityService,
            AuthenticationSourceService authenticationSourceService,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.rolesService = rolesService;
        this.accountRepository = accountRepository;
        this.accountGroupRepository = accountGroupRepository;
        this.securityService = securityService;
        this.authenticationSourceService = authenticationSourceService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired(required = false)
    public void setAccountGroupContributors(Collection<AccountGroupContributor> accountGroupContributors) {
        this.accountGroupContributors = accountGroupContributors;
    }

    @Override
    public Account withACL(AuthenticatedAccount raw) {
        return raw.getAccount()
                // Global role
                .withGlobalRole(
                        roleRepository.findGlobalRoleByAccount(raw.getAccount().id()).flatMap(rolesService::getGlobalRole)
                )
                // Project roles
                .withProjectRoles(
                        roleRepository.findProjectRoleAssociationsByAccount(raw.getAccount().id(), rolesService::getProjectRoleAssociation)
                )
                // Groups from the repository
                .withGroups(
                        accountGroupRepository.findByAccount(raw.getAccount().id()).stream()
                                .map(this::groupWithACL)
                                .collect(Collectors.toList())
                )
                // Group contributions
                .withGroups(
                        accountGroupContributors.stream()
                                .flatMap(accountGroupContributor -> accountGroupContributor.collectGroups(raw).stream())
                                .map(this::groupWithACL)
                                .collect(Collectors.toList())
                )
                // OK
                .lock();
    }

    @Override
    public List<Account> getAccounts() {
        securityService.checkGlobalFunction(AccountManagement.class);
        return accountRepository.findAll(authenticationSourceService::getAuthenticationSource)
                .stream()
                .map(account -> account.withGroups(accountGroupRepository.findByAccount(account.id())))
                .collect(Collectors.toList());
    }

    @Override
    public Account create(AccountInput input) {
        Account account = create(
                input,
                "password"
        );
        accountRepository.setPassword(account.id(), passwordEncoder.encode(input.getPassword()));
        return account;
    }

    @Override
    public Account create(AccountInput input, String authenticationSourceMode) {
        securityService.checkGlobalFunction(AccountManagement.class);
        // Creates the account
        Account account = Account.of(
                input.getName(),
                input.getFullName(),
                input.getEmail(),
                SecurityRole.USER,
                authenticationSourceService.getAuthenticationSource(authenticationSourceMode)
        );
        // Saves it
        account = accountRepository.newAccount(account);
        // Account groups
        accountGroupRepository.linkAccountToGroups(account.id(), input.getGroups());
        // OK
        return account;
    }

    @Override
    public Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider) {
        securityService.checkGlobalFunction(AccountManagement.class);
        return accountRepository.findUserByNameAndSource(username, sourceProvider);
    }

    @Override
    public Account updateAccount(ID accountId, AccountInput input) {
        securityService.checkGlobalFunction(AccountManagement.class);
        // Gets the existing account
        Account account = getAccount(accountId);
        // Checks if default admin
        if (account.isDefaultAdmin() && !StringUtils.equals(account.getName(), input.getName())) {
            throw new AccountDefaultAdminCannotUpdateNameException();
        }
        // Updates it
        account = account.update(input);
        // Saves it
        accountRepository.saveAccount(account);
        // Updating the password?
        if (StringUtils.isNotBlank(input.getPassword())) {
            accountRepository.setPassword(accountId.getValue(), passwordEncoder.encode(input.getPassword()));
        }
        // Account groups
        accountGroupRepository.linkAccountToGroups(account.id(), input.getGroups());
        // OK
        return getAccount(accountId);
    }

    @Override
    public Ack deleteAccount(ID accountId) {
        // Security check
        securityService.checkGlobalFunction(AccountManagement.class);
        // Check the `admin` account
        if (getAccount(accountId).isDefaultAdmin()) {
            throw new AccountDefaultAdminCannotDeleteException();
        }
        // Deletion
        return accountRepository.deleteAccount(accountId);
    }

    @Override
    public List<AccountGroup> getAccountGroups() {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return accountGroupRepository.findAll();
    }

    @Override
    public AccountGroup createGroup(NameDescription nameDescription) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        // Creates the account group
        AccountGroup group = AccountGroup.of(nameDescription.getName(), nameDescription.getDescription());
        // Saves it
        return accountGroupRepository.newAccountGroup(group);
    }

    @Override
    public AccountGroup getAccountGroup(ID groupId) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return accountGroupRepository.getById(groupId);
    }

    @Override
    public AccountGroup updateGroup(ID groupId, NameDescription input) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        AccountGroup group = getAccountGroup(groupId).update(input);
        accountGroupRepository.update(group);
        return group;
    }

    @Override
    public Ack deleteGroup(ID groupId) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return accountGroupRepository.delete(groupId);
    }

    @Override
    public List<AccountGroupSelection> getAccountGroupsForSelection(ID accountId) {
        // Account groups or none
        Set<Integer> accountGroupIds = accountId.ifSet(accountGroupRepository::findByAccount)
                .orElse(Collections.emptyList())
                .stream()
                .map(Entity::id)
                .collect(Collectors.toSet());
        // Collection of groups with the selection
        return getAccountGroups().stream()
                .map(group -> AccountGroupSelection.of(group, accountGroupIds.contains(group.id())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<PermissionTarget> searchPermissionTargets(String token) {
        securityService.checkGlobalFunction(AccountManagement.class);
        List<PermissionTarget> targets = new ArrayList<>();
        // Users first
        targets.addAll(
                accountRepository.findByNameToken(token, authenticationSourceService::getAuthenticationSource)
                        .stream()
                        .map(Account::asPermissionTarget)
                        .collect(Collectors.toList())
        );
        // ... then groups
        targets.addAll(
                accountGroupRepository.findByNameToken(token)
                        .stream()
                        .map(AccountGroup::asPermissionTarget)
                        .collect(Collectors.toList())
        );
        // OK
        return targets;
    }

    @Override
    public Ack saveGlobalPermission(PermissionTargetType type, int id, PermissionInput input) {
        switch (type) {
            case ACCOUNT:
                securityService.checkGlobalFunction(AccountManagement.class);
                return roleRepository.saveGlobalRoleForAccount(id, input.getRole());
            case GROUP:
                securityService.checkGlobalFunction(AccountGroupManagement.class);
                return roleRepository.saveGlobalRoleForGroup(id, input.getRole());
            default:
                return Ack.NOK;
        }
    }

    @Override
    public Collection<GlobalPermission> getGlobalPermissions() {
        Collection<GlobalPermission> permissions = new ArrayList<>();
        // Users first
        permissions.addAll(
                accountRepository.findAll(authenticationSourceService::getAuthenticationSource)
                        .stream()
                        .map(this::getGlobalPermission)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
        // ... then groups
        permissions.addAll(
                accountGroupRepository.findAll()
                        .stream()
                        .map(this::getGroupGlobalPermission)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
        // OK
        return permissions;
    }

    @Override
    public Ack deleteGlobalPermission(PermissionTargetType type, int id) {
        switch (type) {
            case ACCOUNT:
                securityService.checkGlobalFunction(AccountManagement.class);
                return roleRepository.deleteGlobalRoleForAccount(id);
            case GROUP:
                securityService.checkGlobalFunction(AccountGroupManagement.class);
                return roleRepository.deleteGlobalRoleForGroup(id);
            default:
                return Ack.NOK;
        }
    }

    @Override
    public Collection<ProjectPermission> getProjectPermissions(ID projectId) {
        securityService.checkProjectFunction(projectId.getValue(), ProjectAuthorisationMgt.class);
        Collection<ProjectPermission> permissions = new ArrayList<>();
        // Users first
        permissions.addAll(
                accountRepository.findAll(authenticationSourceService::getAuthenticationSource)
                        .stream()
                        .map(account -> getProjectPermission(projectId, account))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
        // ... then groups
        permissions.addAll(
                accountGroupRepository.findAll()
                        .stream()
                        .map(accountGroup -> getGroupProjectPermission(projectId, accountGroup))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
        // OK
        return permissions;
    }

    @Override
    public Ack saveProjectPermission(ID projectId, PermissionTargetType type, int id, PermissionInput input) {
        securityService.checkProjectFunction(projectId.getValue(), ProjectAuthorisationMgt.class);
        switch (type) {
            case ACCOUNT:
                return roleRepository.saveProjectRoleForAccount(projectId.getValue(), id, input.getRole());
            case GROUP:
                return roleRepository.saveProjectRoleForGroup(projectId.getValue(), id, input.getRole());
            default:
                return Ack.NOK;
        }
    }

    @Override
    public Ack deleteProjectPermission(ID projectId, PermissionTargetType type, int id) {
        securityService.checkProjectFunction(projectId.getValue(), ProjectAuthorisationMgt.class);
        switch (type) {
            case ACCOUNT:
                return roleRepository.deleteProjectRoleForAccount(projectId.getValue(), id);
            case GROUP:
                return roleRepository.deleteProjectRoleForGroup(projectId.getValue(), id);
            default:
                return Ack.NOK;
        }
    }

    @Override
    public Collection<ProjectRoleAssociation> getProjectPermissionsForAccount(Account account) {
        return roleRepository.findProjectRoleAssociationsByAccount(
                account.id(),
                rolesService::getProjectRoleAssociation
        )
                .stream()
                // Filter by authorisation
                .filter(projectRoleAssociation -> securityService.isProjectFunctionGranted(
                        projectRoleAssociation.getProjectId(),
                        ProjectAuthorisationMgt.class
                ))
                // OK
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GlobalRole> getGlobalRoleForAccount(Account account) {
        return roleRepository.findGlobalRoleByAccount(account.id())
                .flatMap(rolesService::getGlobalRole);
    }

    @Override
    public List<Account> getAccountsForGroup(AccountGroup accountGroup) {
        return accountRepository.getAccountsForGroup(accountGroup, authenticationSourceService::getAuthenticationSource);
    }

    @Override
    public Optional<GlobalRole> getGlobalRoleForAccountGroup(AccountGroup group) {
        return roleRepository.findGlobalRoleByGroup(group.id())
                .flatMap(rolesService::getGlobalRole);
    }

    @Override
    public Collection<ProjectRoleAssociation> getProjectPermissionsForAccountGroup(AccountGroup group) {
        return roleRepository.findProjectRoleAssociationsByGroup(
                group.id(),
                rolesService::getProjectRoleAssociation
        )
                .stream()
                // Filter by authorisation
                .filter(projectRoleAssociation -> securityService.isProjectFunctionGranted(
                        projectRoleAssociation.getProjectId(),
                        ProjectAuthorisationMgt.class
                ))
                // OK
                .collect(Collectors.toList());
    }

    @Override
    public Collection<AccountGroup> findAccountGroupsByGlobalRole(GlobalRole globalRole) {
        return roleRepository.findAccountGroupsByGlobalRole(globalRole, this::getAccountGroup);
    }

    @Override
    public Collection<Account> findAccountsByGlobalRole(GlobalRole globalRole) {
        return roleRepository.findAccountsByGlobalRole(globalRole, this::getAccount);
    }

    @Override
    public Collection<AccountGroup> findAccountGroupsByProjectRole(Project project, ProjectRole projectRole) {
        return roleRepository.findAccountGroupsByProjectRole(project, projectRole, this::getAccountGroup);
    }

    @Override
    public Collection<Account> findAccountsByProjectRole(Project project, ProjectRole projectRole) {
        return roleRepository.findAccountsByProjectRole(project, projectRole, this::getAccount);
    }

    private Optional<ProjectPermission> getGroupProjectPermission(ID projectId, AccountGroup accountGroup) {
        Optional<ProjectRoleAssociation> roleAssociationOptional = roleRepository.findProjectRoleAssociationsByGroup(
                accountGroup.id(),
                projectId.getValue(),
                rolesService::getProjectRoleAssociation
        );
        if (roleAssociationOptional.isPresent()) {
            return Optional.of(
                    new ProjectPermission(
                            projectId,
                            accountGroup.asPermissionTarget(),
                            roleAssociationOptional.get().getProjectRole()
                    )
            );
        } else {
            return Optional.empty();
        }
    }

    private Optional<ProjectPermission> getProjectPermission(ID projectId, Account account) {
        Optional<ProjectRoleAssociation> roleAssociationOptional = roleRepository.findProjectRoleAssociationsByAccount(
                account.id(),
                projectId.getValue(),
                rolesService::getProjectRoleAssociation
        );
        if (roleAssociationOptional.isPresent()) {
            return Optional.of(
                    new ProjectPermission(
                            projectId,
                            account.asPermissionTarget(),
                            roleAssociationOptional.get().getProjectRole()
                    )
            );
        } else {
            return Optional.empty();
        }
    }

    private Optional<GlobalPermission> getGroupGlobalPermission(AccountGroup group) {
        Optional<String> roleId = roleRepository.findGlobalRoleByGroup(group.id());
        if (roleId.isPresent()) {
            Optional<GlobalRole> globalRole = rolesService.getGlobalRole(roleId.get());
            if (globalRole.isPresent()) {
                return Optional.of(
                        new GlobalPermission(
                                group.asPermissionTarget(),
                                globalRole.get()
                        )
                );
            }
        }
        return Optional.empty();
    }

    private Optional<GlobalPermission> getGlobalPermission(Account account) {
        Optional<String> roleId = roleRepository.findGlobalRoleByAccount(account.id());
        if (roleId.isPresent()) {
            Optional<GlobalRole> globalRole = rolesService.getGlobalRole(roleId.get());
            if (globalRole.isPresent()) {
                return Optional.of(
                        new GlobalPermission(
                                account.asPermissionTarget(),
                                globalRole.get()
                        )
                );
            }
        }
        return Optional.empty();
    }

    @Override
    public Account getAccount(ID accountId) {
        securityService.checkGlobalFunction(AccountManagement.class);
        return accountRepository.getAccount(accountId, authenticationSourceService::getAuthenticationSource)
                .withGroups(accountGroupRepository.findByAccount(accountId.getValue()));
    }

    protected AccountGroup groupWithACL(AccountGroup group) {
        return group
                // Global role
                .withGlobalRole(
                        roleRepository.findGlobalRoleByGroup(group.id()).flatMap(rolesService::getGlobalRole)
                )
                // Project roles
                .withProjectRoles(
                        roleRepository.findProjectRoleAssociationsByGroup(group.id(), rolesService::getProjectRoleAssociation)
                )
                // OK
                .lock();
    }
}
