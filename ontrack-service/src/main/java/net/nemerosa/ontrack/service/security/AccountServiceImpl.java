package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.RolesService;
import net.nemerosa.ontrack.repository.AccountGroupRepository;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final RoleRepository roleRepository;
    private final RolesService rolesService;
    private final AccountGroupRepository accountGroupRepository;

    @Autowired
    public AccountServiceImpl(RoleRepository roleRepository, RolesService rolesService, AccountGroupRepository accountGroupRepository) {
        this.roleRepository = roleRepository;
        this.rolesService = rolesService;
        this.accountGroupRepository = accountGroupRepository;
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

    protected AccountGroup groupWithACL(AccountGroup group) {
        return group
                // TODO Global role
                // TODO Project roles
                // OK
                .lock();
    }
}
