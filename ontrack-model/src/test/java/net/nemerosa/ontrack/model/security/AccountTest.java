package net.nemerosa.ontrack.model.security;

import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void projectEdit() {
        Account account = account(ProjectEdit.class).lock();
        assertTrue(account.isGranted(1, ProjectEdit.class));
    }

    @Test
    public void projectEdit_other_project() {
        Account account = account(ProjectEdit.class).lock();
        assertFalse(account.isGranted(2, ProjectEdit.class));
    }

    @Test
    public void projectEdit_implies_projectView() {
        Account account = account(ProjectEdit.class).lock();
        assertTrue(account.isGranted(1, ProjectView.class));
    }

    @Test
    public void projectView_does_not_imply_projectEdit() {
        Account account = account(ProjectView.class).lock();
        assertFalse(account.isGranted(1, ProjectEdit.class));
    }

    @Test
    public void projectEdit_does_not_implies_projectView_on_other_project() {
        Account account = account(ProjectEdit.class).lock();
        assertFalse(account.isGranted(2, ProjectView.class));
    }

    private Account account(Class<? extends ProjectFunction> fn) {
        return baseAccount().withProjectRole(
                new ProjectRoleAssociation(
                        1,
                        new ProjectRole(
                                "test",
                                "Test",
                                "",
                                Collections.singleton(fn)
                        )
                )
        );
    }

    private Account baseAccount() {
        return Account.of("test", "Test", "test@test.com", SecurityRole.USER);
    }

    @Test
    public void global_function_granted() {
        Account account = baseAccount().withGlobalRole(
                Optional.of(
                        new GlobalRole(
                                "test", "Test", "",
                                Collections.singleton(GlobalSettings.class),
                                Collections.emptySet()
                        )
                )
        );
        assertTrue(account.isGranted(GlobalSettings.class));
        assertFalse(account.isGranted(ProjectCreation.class));
    }

    @Test
    public void global_function_granted_for_admin() {
        Account account = Account.of("test", "Test", "test@test.com", SecurityRole.ADMINISTRATOR);
        assertTrue(account.isGranted(GlobalSettings.class));
        assertTrue(account.isGranted(ProjectCreation.class));
    }

}
