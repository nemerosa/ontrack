package net.nemerosa.ontrack.service.security

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountProvisioningStartupTest {

    private lateinit var ontrackConfigProperties: OntrackConfigProperties
    private lateinit var accountRepository: AccountRepository
    private lateinit var accountGroupRepository: AccountGroupRepository
    private lateinit var startup: AccountProvisioningStartup

    @BeforeEach
    fun before() {
        ontrackConfigProperties = OntrackConfigProperties()

        accountRepository = mockk(relaxed = true)

        val adminGroup = AccountGroup(
            id = ID.of(1),
            name = "Administrators",
            description = "Group of administrators",
        )

        accountGroupRepository = mockk(relaxed = true)
        every { accountGroupRepository.findAccountGroupByName("Administrators") } returns adminGroup

        startup = AccountProvisioningStartup(
            ontrackConfigProperties = ontrackConfigProperties,
            accountRepository = accountRepository,
            accountGroupRepository = accountGroupRepository,
        )
    }

    @Test
    fun `No provisioning`() {
        ontrackConfigProperties.security.authorization.provisioning = false
        startup.start()

        verify { accountRepository wasNot Called }
        verify { accountGroupRepository wasNot Called }
    }

    @Test
    fun `User non existing`() {

        every { accountRepository.findAccountByName(ontrackConfigProperties.security.authorization.admin.email) } returns null

        startup.start()

        verify { accountRepository.newAccount(any()) }
        verify { accountGroupRepository.linkAccountToGroups(any(), any()) }
    }

    @Test
    fun `User existing`() {
        val existingUser = Account(
            id = ID.of(100),
            fullName = "Existing user",
            email = ontrackConfigProperties.security.authorization.admin.email,
            role = SecurityRole.USER,
        )
        every { accountRepository.findAccountByName(ontrackConfigProperties.security.authorization.admin.email) } returns existingUser
        startup.start()

        verify(exactly = 0) { accountRepository.saveAccount(any()) }
        verify(exactly = 0) { accountRepository.newAccount(any()) }
        verify { accountGroupRepository wasNot Called }
    }

    @Test
    fun `User existing, forcing with different name`() {
        val existingUser = Account(
            id = ID.of(100),
            fullName = "Existing user",
            email = "test@yontrack.com",
            role = SecurityRole.USER,
        )
        every { accountRepository.findAccountByName(ontrackConfigProperties.security.authorization.admin.email) } returns existingUser

        ontrackConfigProperties.security.authorization.admin.force = true

        startup.start()

        verify { accountRepository.saveAccount(any()) }
        verify(exactly = 0) { accountRepository.newAccount(any()) }
        verify { accountGroupRepository.linkAccountToGroups(existingUser.id(), any()) }
    }

    @Test
    fun `User existing, forcing with same name`() {
        val existingUser = Account(
            id = ID.of(100),
            fullName = ontrackConfigProperties.security.authorization.admin.fullName,
            email = ontrackConfigProperties.security.authorization.admin.email,
            role = SecurityRole.USER,
        )
        every { accountRepository.findAccountByName(ontrackConfigProperties.security.authorization.admin.email) } returns existingUser

        ontrackConfigProperties.security.authorization.admin.force = true

        startup.start()

        verify(exactly = 0) { accountRepository.saveAccount(any()) }
        verify(exactly = 0) { accountRepository.newAccount(any()) }
        verify { accountGroupRepository.linkAccountToGroups(existingUser.id(), any()) }
    }

}