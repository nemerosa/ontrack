package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.RolesService;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final RoleRepository roleRepository;
    private final RolesService rolesService;

    @Autowired
    public AccountServiceImpl(RoleRepository roleRepository, RolesService rolesService) {
        this.roleRepository = roleRepository;
        this.rolesService = rolesService;
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
                        // TODO Groups
                        // OK
                .lock();
    }
}
