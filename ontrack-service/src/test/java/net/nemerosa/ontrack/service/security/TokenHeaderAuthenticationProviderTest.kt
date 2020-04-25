package net.nemerosa.ontrack.service.security

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokenAccount
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import kotlin.test.*

class TokenHeaderAuthenticationProviderTest {

    private lateinit var tokensService: TokensService
    private lateinit var accountService: AccountService
    private lateinit var provider: TokenHeaderAuthenticationProvider

    @Before
    fun before() {
        tokensService = mock()
        accountService = mock()
        provider = TokenHeaderAuthenticationProvider(
                tokensService,
                accountService
        )
    }

    @Test
    fun `Requires token`() {
        assertTrue(provider.supports(TokenAuthenticationToken::class.java))
    }

    @Test
    fun `Authenticating something else than a token`() {
        val authentication = provider.authenticate(UsernamePasswordAuthenticationToken("user", "xxx"))
        assertNull(authentication)
    }

    @Test
    fun `Token not found`() {
        val auth = TokenAuthenticationToken("xxx")
        val result = provider.authenticate(auth)
        assertNull(result)
    }

    @Test
    fun `Token found`() {
        val auth = TokenAuthenticationToken("xxx")
        val tokenAccount = TokenAccount(
                account = Account(
                        ID.of(1),
                        "user",
                        "User",
                        "user@test.com",
                        BuiltinAuthenticationSourceProvider.SOURCE,
                        SecurityRole.USER
                ),
                token = Token(
                        "xxx",
                        Time.now(),
                        null
                )
        )
        whenever(tokensService.findAccountByToken("xxx")).thenReturn(tokenAccount)
        val user = mock<OntrackAuthenticatedUser>()
        whenever(accountService.withACL(any())).thenReturn(user)
        val result = provider.authenticate(auth)
        assertNotNull(result) { authenticated ->
            assertIs<TokenAuthenticationToken>(authenticated) { u ->
                assertSame(u.principal, user, "Authenticated user is set")
                assertEquals("", u.credentials, "Credentials gone")
                assertTrue(u.isAuthenticated, "Authentication OK")
            }
        }
    }

}