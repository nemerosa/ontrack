package net.nemerosa.ontrack.extension.guest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import org.junit.Before
import org.junit.Test

typealias Supplier<T> = () -> T

class GuestAccountInitializationTest {

    private lateinit var accountService: AccountService
    private lateinit var securityService: SecurityService

    @Before
    fun mocks() {
        accountService = mockk(relaxed = true)
        securityService = mockk()
        every { securityService.asAdmin(any<Supplier<*>>()) } answers {
            val supplier = arg<Supplier<*>>(0)
            supplier()
        }
    }

    @Test
    fun `No creation by default`() {
        ifAccountDoesNotExist()

        service {}.start()

        verify(exactly = 0) {
            accountService.create(any())
        }
    }

    @Test
    fun `Cleanup if not enabled and account already exists`() {
        ifAccountAlreadyExists()

        service {}.start()

        // No creation
        verify(exactly = 0) {
            accountService.create(any())
        }
        // But deletion
        verify(exactly = 1) {
            accountService.deleteAccount(ID.of(2))
        }
    }

    @Test
    fun `No update of the account if it exists already`() {
        ifAccountAlreadyExists()

        service {
            enabled = true
        }.start()

        verify(exactly = 0) {
            accountService.create(any())
        }
    }

    @Test
    fun `Creation of the account if it does not exist yet`() {
        ifAccountDoesNotExist()

        service {
            enabled = true
        }.start()

        verify(exactly = 1) {
            accountService.create(any())
        }
    }

    private fun service(
        properties: GuestExtensionProperties.() -> Unit,
    ): GuestAccountInitialization {
        val guestExtensionProperties = GuestExtensionProperties().apply(properties)
        return GuestAccountInitialization(
            guestExtensionProperties = guestExtensionProperties,
            accountService = accountService,
            securityService = securityService,
        )
    }

    private fun ifAccountAlreadyExists() {
        every { accountService.findAccountByName("guest") } returns Account(
            ID.of(2),
            "guest",
            "Guest",
            "n/a",
            BuiltinAuthenticationSourceProvider.SOURCE,
            SecurityRole.USER,
        )
    }

    private fun ifAccountDoesNotExist() {
        every { accountService.findAccountByName("guest") } returns null
    }

}