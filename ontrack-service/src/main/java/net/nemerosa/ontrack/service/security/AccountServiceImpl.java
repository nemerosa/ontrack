package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.repository.AccountGroupRepository;
import net.nemerosa.ontrack.repository.AccountRepository;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Autowired
    public AccountServiceImpl(
            RoleRepository roleRepository,
            RolesService rolesService,
            AccountRepository accountRepository,
            AccountGroupRepository accountGroupRepository,
            SecurityService securityService,
            AuthenticationSourceService authenticationSourceService) {
        this.roleRepository = roleRepository;
        this.rolesService = rolesService;
        this.accountRepository = accountRepository;
        this.accountGroupRepository = accountGroupRepository;
        this.securityService = securityService;
        this.authenticationSourceService = authenticationSourceService;
    }

    @Override
    public Account withACL(Account raw) {
        return raw
                // Global role
                .withGlobalRole(
                        roleRepository.findGlobalRoleByAccount(raw.id()).flatMap(rolesService::getGlobalRole)
                )
                        // Project roles
                .withProjectRoles(
                        roleRepository.findProjectRoleAssociationsByAccount(raw.id(), rolesService::getProjectRoleAssociation)
                )
                        // Groups
                .withGroups(
                        accountGroupRepository.findByAccount(raw.id()).stream()
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

    protected AccountGroup groupWithACL(AccountGroup group) {
        return group
                // TODO Global role
                // TODO Project roles
                // OK
                .lock();
    }
}
