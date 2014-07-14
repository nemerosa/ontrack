package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.RolesService;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public abstract class AbstractOntrackAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final RoleRepository roleRepository;
    private final RolesService rolesService;
    private final AccountService accountService;

    protected AbstractOntrackAuthenticationProvider(RoleRepository roleRepository, RolesService rolesService, AccountService accountService) {
        this.roleRepository = roleRepository;
        this.rolesService = rolesService;
        this.accountService = accountService;
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Optional<Account> t = findUser(username, authentication);
        return t
                .map(this::withACL)
                .map(AccountUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s cannot be found", username)));
    }

    private Account withACL(Account raw) {
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
//                .withGroups(
//                        accountGroupRepository.findAccountGroupByAccount(raw.id())
//                        .map(t -> )
//                )
                        // OK
                .lock();
    }

    protected abstract Optional<Account> findUser(String username, UsernamePasswordAuthenticationToken authentication);
}
