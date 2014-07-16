package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.AccountGroupRepository;
import net.nemerosa.ontrack.repository.AccountRepository;
import net.nemerosa.ontrack.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public Account create(AccountInput input) {
        securityService.checkGlobalFunction(AccountManagement.class);
        // Creates the account
        Account account = Account.of(
                input.getName(),
                input.getFullName(),
                input.getEmail(),
                SecurityRole.USER,
                authenticationSourceService.getAuthenticationSource("password")
        );
        // Saves it
        account = accountRepository.newAccount(account);
        // Sets the its password
        accountRepository.setPassword(account.id(), passwordEncoder.encode(input.getPassword()));
        // OK
        return account;
    }

    @Override
    public Account updateAccount(ID accountId, AccountInput input) {
        securityService.checkGlobalFunction(AccountManagement.class);
        // Gets the existing account
        Account account = getAccount(accountId);
        // Updates it
        account = account.update(input);
        // Saves it
        accountRepository.saveAccount(account);
        // Updating the password?
        if (StringUtils.isNotBlank(input.getPassword())) {
            accountRepository.setPassword(accountId.getValue(), passwordEncoder.encode(input.getPassword()));
        }
        // OK
        return account;
    }

    @Override
    public Account getAccount(ID accountId) {
        return accountRepository.getAccount(accountId, authenticationSourceService::getAuthenticationSource)
                .withGroups(accountGroupRepository.findByAccount(accountId.getValue()))
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
