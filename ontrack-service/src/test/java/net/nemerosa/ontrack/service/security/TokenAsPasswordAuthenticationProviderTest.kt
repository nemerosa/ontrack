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

class TokenAsPasswordAuthenticationProviderTest {

    private lateinit var tokensService: TokensService
    private lateinit var accountService: AccountService
    private val ontrackConfigProperties = OntrackConfigProperties()
    private lateinit var provider: TokenAsPasswordAuthenticationProvider

    @Before
    fun before() {
        tokensService = mock()
        accountService = mock()
        provider = TokenAsPasswordAuthenticationProvider(
                tokensService,
                accountService,
                ontrackConfigProperties
        )
    }

    @Test
    fun `Requires user name password`() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken::class.java))
    }

    @Test
    fun `Authenticating something else than a user name password`() {
        val authentication = provider.authenticate(TokenAuthenticationToken("x"))
        assertNull(authentication)
    }

    @Test
    fun `Password as a token is not allowed`() {
        val old = ontrackConfigProperties.security.tokens.password
        try {
            ontrackConfigProperties.security.tokens.password = false
            val auth = UsernamePasswordAuthenticationToken("user", "xxx")
            val result = provider.authenticate(auth)
            assertNull(result)
        } finally {
            ontrackConfigProperties.security.tokens.password = old
        }
    }

    @Test
    fun `Token not found`() {
        val auth = UsernamePasswordAuthenticationToken("user", "xxx")
        val result = provider.authenticate(auth)
        assertNull(result)
    }

    @Test
    fun `Token not found but name mismatch`() {
        val auth = UsernamePasswordAuthenticationToken("user", "xxx")
        val tokenAccount = TokenAccount(
                account = Account(
                        ID.of(1),
                        "other-user",
                        "Other user",
                        "other-user@test.com",
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
        assertFailsWith<TokenNameMismatchException> {
            provider.authenticate(auth)
        }
    }

    @Test
    fun `Token found`() {
        val auth = UsernamePasswordAuthenticationToken("user", "xxx")
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
            assertIs<UsernamePasswordAuthenticationToken>(authenticated) { u ->
                assertSame(u.principal, user, "Authenticated user is set")
                assertEquals("", u.credentials, "Credentials gone")
                assertTrue(u.isAuthenticated, "Authentication OK")
            }
        }
    }

}