package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.Duration
import kotlin.test.*

class TokenHeaderAuthenticationProviderTest {

    private lateinit var tokensService: TokensService
    private lateinit var accountService: AccountService
    private lateinit var provider: TokenHeaderAuthenticationProvider
    private lateinit var source: AuthenticationSource

    @BeforeEach
    fun before() {
        tokensService = mockk()
        accountService = mockk()
        provider = TokenHeaderAuthenticationProvider(
            tokensService,
            accountService
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
        every { tokensService.findAccountByToken("xxx", any()) } returns null
        val result = provider.authenticate(auth)
        assertNull(result)
    }

    @Test
    fun `Token found but invalid`() {
        val auth = TokenAuthenticationToken("xxx")
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
        val auth = TokenAuthenticationToken("xxx")
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
            assertIs<TokenAuthenticationToken>(authenticated) { u ->
                assertSame(u.principal, user, "Authenticated user is set")
                assertEquals("", u.credentials, "Credentials gone")
                assertTrue(u.isAuthenticated, "Authentication OK")
            }
        }
    }

}