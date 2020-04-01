package net.nemerosa.ontrack.model.security;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

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

    @Test
    public void serializable_account() {
        Account account = account(ProjectView.class);
        // Serialisation
        byte[] bytes = SerializationUtils.serialize(account);
        // Deserialisation
        Account readAccount = SerializationUtils.deserialize(bytes);
        // Check
        assertEquals(account, readAccount);
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
        return Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none());
    }

    @Test
    public void global_function_granted() {
        Account account = baseAccount().withGlobalRole(
                new GlobalRole(
                        "test", "Test", "",
                        Collections.singleton(GlobalSettings.class),
                        Collections.emptySet()
                )
        );
        assertTrue(account.isGranted(GlobalSettings.class));
        assertFalse(account.isGranted(ProjectCreation.class));
    }

    @Test
    public void global_function_granted_for_admin() {
        Account account = Account.of("test", "Test", "test@test.com", SecurityRole.ADMINISTRATOR, AuthenticationSource.none());
        assertTrue(account.isGranted(GlobalSettings.class));
        assertTrue(account.isGranted(ProjectCreation.class));
    }

}
