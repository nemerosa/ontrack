package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.preferences.Preferences;
import net.nemerosa.ontrack.model.structure.ID;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ConnectedAccountTest {

    @Test
    public void none() {
        assertNull(ConnectedAccount.none().getAccount());
    }

    @Test(expected = IllegalStateException.class)
    public void of_null() {
        ConnectedAccount.of(null, new Preferences());
    }

    @Test(expected = IllegalStateException.class)
    public void of_not_defined() {
        ConnectedAccount.of(Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none(), false, false), new Preferences());
    }

    @Test
    public void of() {
        Account test = Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none(), false, false).withId(ID.of(2));
        assertSame(ConnectedAccount.of(test, new Preferences()).getAccount(), test);
    }

}
