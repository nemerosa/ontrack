package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokenScope
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*
import kotlin.test.*

class TokensServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var tokensService: TokensService

    @Test
    fun `Getting a token returns none when no token is set`() {
        asUser {
            assertNull(tokensService.getCurrentToken(uid("ot_")))
        }
    }

    @Test
    fun `Getting the current token when a token is set`() {
        asUser {
            val token = tokensService.generateNewToken(TokenOptions(name = "test"))
            assertNotNull(tokensService.getCurrentToken("test"), "Could find the token") {
                assertEquals(token.value, it.value)
                assertNotNull(it.creation)
                assertNull(it.validUntil)
            }
        }
    }

    @Test
    fun `Getting the current token with a validity period being set`() {
        asUser {
            withCustomTokenValidityDuration(Duration.ofDays(1)) {
                tokensService.generateNewToken(TokenOptions(name = "test"))
            }
            assertNotNull(tokensService.getCurrentToken("test")) {
                assertTrue(it.value.isNotBlank())
                assertNotNull(it.creation)
                assertNotNull(it.validUntil) { until ->
                    assertTrue(until > it.creation, "Validity is set and in the future of the creation period")
                }
            }
        }
    }

    @Test
    fun `Revoking a token`() {
        asUser {
            tokensService.generateNewToken(TokenOptions("test"))
            assertNotNull(tokensService.getCurrentToken("test"))
            tokensService.revokeToken("test")
            assertNull(tokensService.getCurrentToken("test"))
        }
    }

    @Test
    fun `Unknown token`() {
        val t = tokensService.findAccountByToken(UUID.randomUUID().toString())
        assertNull(t, "Token not found")
    }

    @Test
    fun `Bound user`() {
        asUser {
            val accountId = securityService.currentAccount!!.id()
            tokensService.generateNewToken(TokenOptions("test"))
            val token = tokensService.getCurrentToken("test")!!.value
            val t = tokensService.findAccountByToken(token)
            assertNotNull(t, "Token found") {
                assertEquals(token, it.token.value)
                assertNotNull(it.token.creation)
                assertNull(it.token.validUntil)
                assertEquals(accountId, it.account.id(), "Same account")
            }
        }
    }

    @Test
    fun `Token not found when invalidated`() {
        asUser {
            tokensService.generateNewToken(TokenOptions("test"))
            val token = tokensService.getCurrentToken("test")!!.value
            val t = tokensService.findAccountByToken(token)
            assertNotNull(t, "Token found")
            // Invalidates the token
            tokensService.revokeToken("test")
            val tt = tokensService.findAccountByToken(token)
            assertNull(tt)
        }
    }

    @Test
    fun `Get the token of an account`() {
        asUser {
            val token = tokensService.generateNewToken(TokenOptions("test"))
            // Gets the account ID
            val accountId = securityService.currentAccount!!.id()
            asUserWith<AccountManagement> {
                val result = tokensService.getTokens(accountId).find { it.name == "test" }
                assertNotNull(result) {
                    assertEquals(token.value, it.value)
                }
            }
        }
    }

    @Test
    fun `Revoke an account`() {
        asUser {
            tokensService.generateNewToken(TokenOptions("test"))
            val accountId = securityService.currentAccount!!.id()
            asUserWith<AccountManagement> {
                tokensService.revokeAllTokens(accountId)
            }
            assertNull(tokensService.getCurrentToken("test"))
        }
    }

    @Test
    fun `Revoke all tokens`() {
        val accounts = (1..3).map { accountWithToken() }
        asAdmin {
            // Checks that they all have tokens
            accounts.forEach { account ->
                val tokens = tokensService.getTokens(account)
                assertTrue(tokens.isNotEmpty(), "Tokens are set")
            }
            // Revokes all tokens
            asUserWith<AccountManagement> {
                val count = tokensService.revokeAll()
                assertTrue(count >= 3)
            }
            // Checks that all tokens are gone
            accounts.forEach { account ->
                val tokens = tokensService.getTokens(account)
                assertTrue(tokens.isEmpty(), "Tokens are gone")
            }
        }
    }

    @Test
    fun `Checking the validity of a token with cache not enabled`() {
        withCustomTokenCache(false) {
            asUser {
                val id = securityService.currentAccount!!.id()
                val token = tokensService.generateNewToken(TokenOptions("test"))
                assertTrue(tokensService.isValid(token.value), "Token is valid")
                asAdmin { tokensService.revokeToken(id, "test") }
                assertFalse(tokensService.isValid(token.value), "Token has been revoked")
                assertFalse(tokensService.isValid(token.value), "Token has been revoked")
            }
        }
    }

    @Test
    fun `Checking the validity of a token with cache enabled`() {
        withCustomTokenCache(true) {
            asUser {
                val id = securityService.currentAccount!!.id()
                val token = tokensService.generateNewToken(TokenOptions("test"))
                assertTrue(tokensService.isValid(token.value), "Token is valid")
                asAdmin { tokensService.revokeToken(id, "test") }
                assertFalse(tokensService.isValid(token.value), "Token has been revoked")
            }
        }
    }

    @Test
    fun `Changing the validity of a token to a shorter one with unlimited defaults`() {
        asUser {
            val id = securityService.currentAccount!!.id()
            asAdmin {
                val t = tokensService.generateToken(
                    accountId = id,
                    options = TokenOptions(
                        name = "test",
                        validity = Duration.ofDays(14),
                        forceUnlimited = false,
                    )
                )
                assertNotNull(t.validUntil) {
                    assertFalse(t.isValid(Time.now() + Duration.ofDays(15)))
                }
            }
        }
    }

    @Test
    fun `A transient token has always its validity being set`() {
        withCustomTokenTransientValidityDuration(Duration.ofHours(2)) {
            asUser {
                val id = securityService.currentAccount!!.id()
                asAdmin {
                    val t = tokensService.generateToken(
                        accountId = id,
                        options = TokenOptions(
                            name = "test",
                            scope = TokenScope.NEXT_UI,
                        )
                    )
                    assertNotNull(t.validUntil) {
                        assertTrue(t.isValid(Time.now() + Duration.ofHours(1)))
                        assertFalse(t.isValid(Time.now() + Duration.ofHours(3)))
                    }
                }
            }
        }
    }

    @Test
    fun `A transient token has always its validity being overridden`() {
        withCustomTokenTransientValidityDuration(Duration.ofHours(2)) {
            asUser {
                val id = securityService.currentAccount!!.id()
                asAdmin {
                    val t = tokensService.generateToken(
                        accountId = id,
                        options = TokenOptions(
                            name = "test",
                            scope = TokenScope.NEXT_UI,
                            validity = Duration.ofDays(14),
                            forceUnlimited = false,
                        )
                    )
                    assertNotNull(t.validUntil) {
                        assertTrue(t.isValid(Time.now() + Duration.ofHours(1)))
                        assertFalse(t.isValid(Time.now() + Duration.ofHours(3)))
                    }
                }
            }
        }
    }

    @Test
    fun `Generating a token with default duration`() {
        withCustomTokenValidityDuration(Duration.ofDays(14)) {
            asUser {
                val id = securityService.currentAccount!!.id()
                asAdmin {
                    val t = tokensService.generateToken(
                        accountId = id,
                        options = TokenOptions(
                            name = "test",
                            validity = null,
                            forceUnlimited = false,
                        )
                    )
                    assertNotNull(t.validUntil) {
                        assertFalse(t.isValid(Time.now() + Duration.ofDays(15)))
                    }
                }
            }
        }
    }

    @Test
    fun `Generating a token with unlimited duration`() {
        withCustomTokenValidityDuration(Duration.ofDays(14)) {
            asUser {
                val id = securityService.currentAccount!!.id()
                asAdmin {
                    val t = tokensService.generateToken(
                        accountId = id,
                        options = TokenOptions(
                            name = "test",
                            validity = null,
                            forceUnlimited = true,
                        )
                    )
                    assertNull(t.validUntil, "Unlimited token")
                }
            }
        }
    }

    private fun accountWithToken(): Account {
        return asUser {
            tokensService.generateNewToken(TokenOptions("test"))
            val accountId = securityService.currentAccount!!.id()
            asAdmin {
                accountService.getAccount(ID.of(accountId))
            }
        }
    }

    private fun <T> withCustomTokenValidityDuration(duration: Duration, code: () -> T): T {
        val old = ontrackConfigProperties.security.tokens.validity
        return try {
            ontrackConfigProperties.security.tokens.validity = duration
            code()
        } finally {
            ontrackConfigProperties.security.tokens.validity = old
        }
    }

    private fun <T> withCustomTokenTransientValidityDuration(duration: Duration, code: () -> T): T {
        val old = ontrackConfigProperties.security.tokens.transientValidity
        return try {
            ontrackConfigProperties.security.tokens.transientValidity = duration
            code()
        } finally {
            ontrackConfigProperties.security.tokens.transientValidity = old
        }
    }

    private fun <T> withCustomTokenCache(enabled: Boolean, code: () -> T): T {
        val old = ontrackConfigProperties.security.tokens.cache.enabled
        return try {
            ontrackConfigProperties.security.tokens.cache.enabled = enabled
            code()
        } finally {
            ontrackConfigProperties.security.tokens.cache.enabled = old
        }
    }

}