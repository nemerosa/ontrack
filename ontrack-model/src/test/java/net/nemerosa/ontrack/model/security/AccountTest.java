package net.nemerosa.ontrack.model.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void projectEdit() {
        Account account = account().with(1, ProjectEdit.class).lock();
        assertTrue(account.isGranted(1, ProjectEdit.class));
    }

    @Test
    public void projectEdit_other_project() {
        Account account = account().with(1, ProjectEdit.class).lock();
        assertFalse(account.isGranted(2, ProjectEdit.class));
    }

    @Test
    public void projectEdit_implies_projectView() {
        Account account = account().with(1, ProjectEdit.class).lock();
        assertTrue(account.isGranted(1, ProjectView.class));
    }

    @Test
    public void projectView_does_not_imply_projectEdit() {
        Account account = account().with(1, ProjectView.class).lock();
        assertFalse(account.isGranted(1, ProjectEdit.class));
    }

    @Test
    public void projectEdit_does_not_implies_projectView_on_other_project() {
        Account account = account().with(1, ProjectEdit.class).lock();
        assertFalse(account.isGranted(2, ProjectView.class));
    }

    private Account account() {
        return Account.of("test", "Test", "test@test.com", SecurityRole.USER);
    }

}
