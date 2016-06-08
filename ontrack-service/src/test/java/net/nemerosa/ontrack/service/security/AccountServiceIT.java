package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.Assert.*;

public class AccountServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private AccountService accountService;

    @Autowired
    private StructureService structureService;

    private int project;

    @Before
    public void before() throws Exception {
        project = asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(
                        nameDescription()
                )
        )).id();
    }

    @Test
    public void account_with_no_role() throws Exception {
        Account account = account();
        account = accountService.withACL(AuthenticatedAccount.of(account));
        assertNotNull(account);
        assertFalse("As a normal user, must not have any global grant", account.isGranted(AccountManagement.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, BranchCreate.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, ValidationRunCreate.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, ValidationStampEdit.class));
    }

    @Test
    public void account_with_global_role_controller() throws Exception {
        Account account = account();
        int id = account.id();
        asUser().with(AccountManagement.class).call(() -> accountService.saveGlobalPermission(
                PermissionTargetType.ACCOUNT,
                id,
                new PermissionInput("CONTROLLER")
        ));
        account = accountService.withACL(AuthenticatedAccount.of(account));
        assertNotNull(account);
        assertFalse("As a normal user, must not have any global grant", account.isGranted(AccountManagement.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, BranchCreate.class));
        assertTrue("As a controller, must have the right to create validation runs on any project", account.isGranted(project, ValidationRunCreate.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, ValidationStampEdit.class));
    }

    @Test
    public void account_with_global_role_controller_on_group() throws Exception {
        // Account
        Account origAccount = account();

        // Group
        AccountGroup group = asUser().with(AccountManagement.class).call(() -> accountService.createGroup(nameDescription()));

        // Updates the account with the group
        Account accountWithGroup = asUser().with(AccountManagement.class).call(() -> accountService.updateAccount(
                origAccount.getId(),
                new AccountInput(
                        origAccount.getName(),
                        origAccount.getFullName(),
                        origAccount.getEmail(),
                        "",
                        Collections.singletonList(group.id())
                )
        ));

        // Checks the group
        assertNotNull(accountWithGroup.getAccountGroups());
        assertEquals(1, accountWithGroup.getAccountGroups().size());
        assertEquals(group, accountWithGroup.getAccountGroups().get(0));

        // Changes the group global role
        asUser().with(AccountManagement.class).call(() -> accountService.saveGlobalPermission(
                PermissionTargetType.GROUP,
                group.id(),
                new PermissionInput("CONTROLLER")
        ));

        // Gets the ACL of the account
        Account account = asUser().with(AccountManagement.class).call(() -> accountService.withACL(
                AuthenticatedAccount.of(accountService.getAccount(accountWithGroup.getId()))
        ));

        // Checks
        assertNotNull(account);
        assertFalse("As a normal user, must not have any global grant", account.isGranted(AccountManagement.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, BranchCreate.class));
        assertTrue("As a controller, must have the right to create validation runs on any project", account.isGranted(project, ValidationRunCreate.class));
        assertFalse("As a normal user, must not have any project grant", account.isGranted(project, ValidationStampEdit.class));
    }

    /**
     * Regression test for #427
     */
    @Test
    public void admin_can_delete_promotion_run() throws Exception {
        // Creates an account
        ID id = account().getId();
        // Assigns the Administrator role to this account
        asUser().with(AccountManagement.class).call(() -> accountService.saveGlobalPermission(
                PermissionTargetType.ACCOUNT,
                id.get(),
                new PermissionInput("ADMINISTRATOR")
        ));
        // Gets the ACL of the account
        Account account = asUser().with(AccountManagement.class).call(() -> accountService.withACL(
                AuthenticatedAccount.of(accountService.getAccount(id))
        ));
        // Creates any project
        Project project = doCreateProject();
        // Checks the account can delete a promotion run
        assertTrue("An administrator must be granted the promotion run deletion", account.isGranted(project.id(), PromotionRunDelete.class));
    }

    private Account account() throws Exception {
        return asUser().with(AccountManagement.class).call(() -> accountService.create(new AccountInput(
                TestUtils.uid("A"),
                "Test account",
                "test@test.com",
                "secret",
                Collections.emptyList()
        )));
    }

}