package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.structure.ID;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ConnectedAccountTest {

    @Test
    public void none() {
        assertNull(ConnectedAccount.none(false).getAccount());
    }

    @Test(expected = IllegalStateException.class)
    public void of_null() {
        ConnectedAccount.of(false, null);
    }

    @Test(expected = IllegalStateException.class)
    public void of_not_defined() {
        ConnectedAccount.of(false, Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none()));
    }

    @Test
    public void of() {
        Account test = Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none()).withId(ID.of(2));
        assertSame(ConnectedAccount.of(false, test).getAccount(), test);
    }

}
