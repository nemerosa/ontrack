package net.nemerosa.ontrack.model.security;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void serializable_account() {
        Account account = baseAccount();
        // Serialisation
        byte[] bytes = SerializationUtils.serialize(account);
        // Deserialisation
        Account readAccount = SerializationUtils.deserialize(bytes);
        // Check
        assertEquals(account, readAccount);
    }

    private Account baseAccount() {
        return Account.of("test", "Test", "test@test.com", SecurityRole.USER, AuthenticationSource.none(), false, false);
    }

}
