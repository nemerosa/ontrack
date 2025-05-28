package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.security.Account.Companion.of
import org.apache.commons.lang3.SerializationUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AccountTest {

    @Test
    fun serializable_account() {
        val account = baseAccount()
        // Serialisation
        val bytes = SerializationUtils.serialize(account)
        // Deserialisation
        val readAccount = SerializationUtils.deserialize<Account>(bytes)
        // Check
        assertEquals(account, readAccount)
    }

    private fun baseAccount(): Account {
        return of("Test", "test@test.com", SecurityRole.USER)
    }
}
