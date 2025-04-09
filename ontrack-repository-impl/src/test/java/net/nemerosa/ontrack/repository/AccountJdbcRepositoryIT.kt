package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    fun `Find or create an account when account already exists`() {
        val name = uid("user-")
        val account = Account(
            id = ID.NONE,
            name = name,
            fullName = name,
            email = "$name@test.com",
            role = SecurityRole.USER,
        )
        val createdAccount = accountRepository.newAccount(account)

        // Tries to find it or create it
        val savedAccount = accountRepository.findOrCreateAccount(account)
        assertEquals(createdAccount.id(), savedAccount.id())
    }

    @Test
    fun `Find or create an account when account does not exists yet`() {
        val name = uid("user-")
        val email = "$name@test.com"
        val account = Account(
            id = ID.NONE,
            name = name,
            fullName = name,
            email = email,
            role = SecurityRole.USER,
        )

        // Tries to find it or create it
        val savedAccount = accountRepository.findOrCreateAccount(account)
        assertTrue(savedAccount.id.isSet, "Account created")
        assertEquals(name, savedAccount.name)
        assertEquals(name, savedAccount.fullName)
        assertEquals(email, savedAccount.email)
    }

}