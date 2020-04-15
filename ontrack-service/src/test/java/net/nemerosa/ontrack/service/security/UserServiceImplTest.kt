package net.nemerosa.ontrack.service.security

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.repository.AccountRepository
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertTrue

class UserServiceImplTest {

    private lateinit var service: UserService
    private lateinit var securityService: SecurityService
    private lateinit var accountRepository: AccountRepository

    @Before
    fun before() {
        securityService = mock()
        accountRepository = mock()
        service = UserServiceImpl(
                securityService,
                accountRepository
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
        whenever(securityService.currentAccount).thenReturn(mockOntrackAuthenticatedUser(account))
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
        whenever(securityService.currentAccount).thenReturn(mockOntrackAuthenticatedUser(account))
        whenever(accountRepository.checkPassword(eq(1), any())).thenReturn(false)
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
        whenever(securityService.currentAccount).thenReturn(mockOntrackAuthenticatedUser(account))
        whenever(accountRepository.checkPassword(eq(1), any())).thenReturn(true)
        val ack = service.changePassword(PasswordChange("old", "new"))
        assertTrue(ack.isSuccess)
        verify(accountRepository, times(1)).setPassword(1, "xxx")
    }

    private fun mockOntrackAuthenticatedUser(account: Account): OntrackAuthenticatedUser {
        val user = mock<OntrackAuthenticatedUser>()
        whenever(user.account).thenReturn(account)
        return user
    }
}