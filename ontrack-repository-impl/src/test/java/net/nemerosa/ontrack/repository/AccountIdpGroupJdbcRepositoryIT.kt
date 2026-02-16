package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AccountIdpGroupJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var accountIdpGroupRepository: AccountIdpGroupRepository

    @Test
    fun `Maximum length for group names`() {
        val name = uid("user-")
        val account = Account(
            id = ID.NONE,
            fullName = name,
            email = "$name@test.com",
            role = SecurityRole.USER,
        )
        val createdAccount = accountRepository.newAccount(account)
        accountIdpGroupRepository.syncGroups(
            accountId = createdAccount.id(),
            idpGroups = listOf("a".repeat(256))
        )
        val groups = accountIdpGroupRepository.getAccountIdpGroups(createdAccount.id())
        assertEquals("a".repeat(255), groups.single())
    }

}