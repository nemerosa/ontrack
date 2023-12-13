package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.Duration
import kotlin.test.*

class TokenAsPasswordAuthenticationProviderTest {

    private lateinit var source: AuthenticationSource
    private lateinit var tokensService: TokensService
    private lateinit var accountService: AccountService
    private val ontrackConfigProperties = OntrackConfigProperties()
    private lateinit var provider: TokenAsPasswordAuthenticationProvider

    @BeforeEach
    fun before() {
        tokensService = mockk()
        accountService = mockk()
        provider = TokenAsPasswordAuthenticationProvider(
            tokensService,
            accountService,
            ontrackConfigProperties
        )
        source = AuthenticationSource(
            provider = BuiltinAuthenticationSourceProvider.ID,
            key = "",
            name = "Built-in",
            isEnabled = true,
            isAllowingPasswordChange = true
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
    fun `Token found but name mismatch`() {
        val auth = UsernamePasswordAuthenticationToken("user", "xxx")
        val tokenAccount = TokenAccount(
            account = Account(
                ID.of(1),
                "other-user",
                "Other user",
                "other-user@test.com",
                source,
                SecurityRole.USER,
                disabled = false,
                locked = false,
            ),
            token = Token(
                name = "default",
                value = "xxx",
                creation = Time.now(),
                scope = TokenScope.USER,
                validUntil = null,
                lastUsed = null,
            )
        )
        every { tokensService.findAccountByToken("xxx", any()) } returns tokenAccount
        assertFailsWith<TokenNameMismatchException> {
            provider.authenticate(auth)
        }
    }

    @Test
    fun `Token found but invalid`() {
        val auth = UsernamePasswordAuthenticationToken("user", "xxx")
        val tokenAccount = TokenAccount(
            account = Account(
                ID.of(1),
                "user",
                "User",
                "user@test.com",
                source,
                SecurityRole.USER,
                disabled = false,
                locked = false,
            ),
            token = Token(
                name = "default",
                value = "xxx",
                creation = Time.now() - Duration.ofHours(24),
                scope = TokenScope.USER,
                validUntil = Time.now() - Duration.ofHours(12), // Stopped being valid 12 hours ago
                lastUsed = null,
            )
        )
        every { tokensService.findAccountByToken("xxx", any()) } returns tokenAccount
        assertFailsWith<CredentialsExpiredException> {
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
                source,
                SecurityRole.USER,
                disabled = false,
                locked = false,
            ),
            token = Token(
                name = "default",
                value = "xxx",
                creation = Time.now(),
                scope = TokenScope.USER,
                validUntil = null,
                lastUsed = null,
            )
        )
        every { tokensService.findAccountByToken("xxx", any()) } returns tokenAccount
        val user = mockk<OntrackAuthenticatedUser>()
        every { accountService.withACL(any()) } returns user
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