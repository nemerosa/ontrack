package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.repository.AccountRepository
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder

import java.util.function.Predicate

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

class UserServiceImplTest {

    private UserService service
    private SecurityService securityService
    private AccountRepository accountRepository
    private PasswordEncoder passwordEncoder

    @Before
    void before() {
        securityService = mock(SecurityService)
        accountRepository = mock(AccountRepository)
        passwordEncoder = mock(PasswordEncoder)
        service = new UserServiceImpl(
                securityService,
                accountRepository,
                passwordEncoder
        )
    }

    @Test(expected = AccessDeniedException)
    void 'Change password: denied when not authenticated'() {
        when(securityService.getCurrentAccount()).thenReturn(null)
        service.changePassword(new PasswordChange('old', 'new'))
    }

    @Test(expected = AccessDeniedException)
    void 'Change password: denied when not allowed'() {
        def account = Account.of(
                'test',
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(false)
        )
        when(securityService.getCurrentAccount()).thenReturn(account)
        service.changePassword(new PasswordChange('old', 'new'))
    }

    @Test(expected = UserOldPasswordException)
    void 'Change password: denied when old password incorrect'() {
        def account = Account.of(
                'test',
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(true)
        ).withId(ID.of(1))
        when(securityService.getCurrentAccount()).thenReturn(account)
        when(accountRepository.checkPassword(eq(1), any(Predicate))).thenReturn(false)
        service.changePassword(new PasswordChange('old', 'new'))
    }

    @Test
    void 'Change password: ok when old password is correct'() {
        def account = Account.of(
                'test',
                "Test user",
                "test@test.com",
                SecurityRole.USER,
                AuthenticationSource.none().withAllowingPasswordChange(true)
        ).withId(ID.of(1))
        when(securityService.getCurrentAccount()).thenReturn(account)
        when(accountRepository.checkPassword(eq(1), any(Predicate))).thenReturn(true)
        when(passwordEncoder.encode('new')).thenReturn('xxx')
        def ack = service.changePassword(new PasswordChange('old', 'new'))
        assert ack.success
        verify(accountRepository, times(1)).setPassword(1, 'xxx')
    }
}