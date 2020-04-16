package net.nemerosa.ontrack.service.security

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
        securityService = mock()
        accountRepository = mock()
        passwordEncoder = mock()
        user = mock()
        service = UserServiceImpl(
                securityService,
                accountRepository,
                passwordEncoder
        )
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when not authenticated`() {
        whenever(securityService.currentAccount).thenReturn(null)
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when not allowed`() {
        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(false)
        )
        whenever(user.account).thenReturn(account)
        whenever(securityService.currentAccount).thenReturn(user)
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = AccessDeniedException::class)
    fun `Change password denied when account not found`() {
        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(true)
        ).withId(ID.of(1))
        whenever(user.account).thenReturn(account)
        whenever(securityService.currentAccount).thenReturn(user)
        whenever(accountRepository.findBuiltinAccount("test")).thenReturn(null)
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test(expected = UserOldPasswordException::class)
    fun `Change password denied when old password incorrect`() {
        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(true)
        ).withId(ID.of(1))
        val builtinAccount = BuiltinAccount(account, "old-encoded")
        whenever(user.account).thenReturn(account)
        whenever(securityService.currentAccount).thenReturn(user)
        whenever(accountRepository.findBuiltinAccount("test")).thenReturn(builtinAccount)
        whenever(passwordEncoder.matches("old", "old-encoded")).thenReturn(false)
        service.changePassword(PasswordChange("old", "new"))
    }

    @Test
    fun `Change password ok when old password is correct`() {
        val account = Account.of(
                "test",
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(true)
        ).withId(ID.of(1))
        val builtinAccount = BuiltinAccount(account, "old-encoded")
        whenever(user.account).thenReturn(account)
        whenever(securityService.currentAccount).thenReturn(user)
        whenever(accountRepository.findBuiltinAccount("test")).thenReturn(builtinAccount)
        whenever(passwordEncoder.matches("old", "old-encoded")).thenReturn(true)
        whenever(passwordEncoder.encode("new")).thenReturn("new-encoded")
        val ack = service.changePassword(PasswordChange("old", "new"))
        assertTrue(ack.isSuccess)
        verify(accountRepository, times(1)).setPassword(1, "new-encoded")
    }

}