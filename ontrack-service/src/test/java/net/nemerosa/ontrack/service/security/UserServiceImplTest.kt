package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.repository.BuiltinAccount
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertTrue

class UserServiceImplTest {

    private lateinit var service: UserService
    private lateinit var securityService: SecurityService
    private lateinit var accountRepository: AccountRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var user: OntrackAuthenticatedUser

    @Before
    fun before() {
        securityService = mockk()
        accountRepository = mockk()
        passwordEncoder = mockk()
        user = mockk()
        service = UserServiceImpl(
            securityService,
            accountRepository,
            passwordEncoder
        )
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when not authenticated`() {
        every { securityService.currentAccount } returns null
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when not allowed`() {
        val account = Account.of(
            "test",
            "Test user",
            "test@test.com",
            SecurityRole.USER,
            authenticationSourceWithPasswordChangeAllowed(false),
            disabled = false,
            locked = false,
        )
        every { user.account } returns account
        every { securityService.currentAccount } returns user
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when account not found`() {
        val account = Account.of(
            "test",
            "Test user",
            "test@test.com",
            SecurityRole.USER,
            authenticationSourceWithPasswordChangeAllowed(true),
            disabled = false,
            locked = false,
        ).withId(ID.of(1))
        every { user.account } returns account
        every { securityService.currentAccount } returns user
        every { accountRepository.findBuiltinAccount("test") } returns null
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = UserOldPasswordException::class)
    fun `Change password denied when old password incorrect`() {
        val account = Account.of(
            "test",
            "Test user",
            "test@test.com",
            SecurityRole.USER,
            authenticationSourceWithPasswordChangeAllowed(true),
            disabled = false,
            locked = false,
        ).withId(ID.of(1))
        val builtinAccount = BuiltinAccount(account, "old-encoded")
        every { user.account } returns account
        every { securityService.currentAccount } returns user
        every { accountRepository.findBuiltinAccount("test") } returns builtinAccount
        every { passwordEncoder.matches("old", "old-encoded") } returns false
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test
    fun `Change password ok when old password is correct`() {
        val account = Account.of(
            "test",
            "Test user",
            "test@test.com",
            SecurityRole.USER,
            authenticationSourceWithPasswordChangeAllowed(true),
            disabled = false,
            locked = false,
        ).withId(ID.of(1))
        val builtinAccount = BuiltinAccount(account, "old-encoded")
        every { user.account } returns account
        every { securityService.currentAccount } returns user
        every { accountRepository.findBuiltinAccount("test") } returns builtinAccount
        every { passwordEncoder.matches("old", "old-encoded") } returns true
        every { passwordEncoder.encode("new") } returns "new-encoded"
        val ack = service.changePassword(PasswordChange("old", "new"))
        assertTrue(ack.success)
        verify(exactly = 1) { accountRepository.setPassword(1, "new-encoded") }
    }

    private fun authenticationSourceWithPasswordChangeAllowed(allowingPasswordChange: Boolean) =
        AuthenticationSource.none().run {
            AuthenticationSource(
                provider = provider,
                key = key,
                name = name,
                isAllowingPasswordChange = allowingPasswordChange
            )
        }

}