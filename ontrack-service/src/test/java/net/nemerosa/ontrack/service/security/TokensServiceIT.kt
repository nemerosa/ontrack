package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.TokensService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*
import kotlin.test.*

class TokensServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var tokensService: TokensService

    @Test
    fun `Access to tokens is only for authenticated users`() {
        asAnonymous {
            assertNull(tokensService.currentToken)
        }
    }

    @Test
    fun `Generating tokens is only for authenticated users`() {
        asAnonymous {
            assertFailsWith<TokenGenerationNoAccountException> {
                tokensService.generateNewToken()
            }
        }
    }

    @Test
    fun `Getting the current token returns none when no token is set`() {
        asUser {
            assertNull(tokensService.currentToken)
        }
    }

    @Test
    fun `Getting the current token when a token is set`() {
        asUser {
            tokensService.generateNewToken()
            assertNotNull(tokensService.currentToken) {
                assertTrue(it.value.isNotBlank())
                assertNotNull(it.creation)
                assertNull(it.validUntil)
            }
        }
    }

    @Test
    fun `Getting the current token with a validity period being set`() {
        asUser {
            withCustomTokenValidityDuration(Duration.ofDays(1)) {
                tokensService.generateNewToken()
            }
            assertNotNull(tokensService.currentToken) {
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
            tokensService.generateNewToken()
            assertNotNull(tokensService.currentToken)
            tokensService.revokeToken()
            assertNull(tokensService.currentToken)
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
            tokensService.generateNewToken()
            val token = tokensService.currentToken!!.value
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
            tokensService.generateNewToken()
            val token = tokensService.currentToken!!.value
            val t = tokensService.findAccountByToken(token)
            assertNotNull(t, "Token found")
            // Invalidates the token
            tokensService.revokeToken()
            val tt = tokensService.findAccountByToken(token)
            assertNull(tt)
        }
    }

    @Test
    fun `Get the token of an account`() {
        asUser {
            val token = tokensService.generateNewToken()
            // Gets the account ID
            val accountId = securityService.currentAccount!!.id()
            asUserWith<AccountManagement> {
                val result = tokensService.getToken(accountId)
                assertNotNull(result) {
                    assertEquals(token.value, it.value)
                }
            }
        }
    }

    @Test
    fun `Revoke an account`() {
        asUser {
            tokensService.generateNewToken()
            val accountId = securityService.currentAccount!!.id()
            asUserWith<AccountManagement> {
                tokensService.revokeToken(accountId)
            }
            assertNull(tokensService.currentToken)
        }
    }

    @Test
    fun `Revoke all tokens`() {
        val accounts = (1..3).map { accountWithToken() }
        asAdmin {
            // Checks that they all have tokens
            accounts.forEach { account ->
                val token = tokensService.getToken(account)
                assertNotNull(token, "Tokens are set")
            }
            // Revokes all tokens
            asUserWith<AccountManagement> {
                val count = tokensService.revokeAll()
                assertTrue(count >= 3)
            }
            // Checks that all tokens are gone
            accounts.forEach { account ->
                val token = tokensService.getToken(account)
                assertNull(token, "Tokens are gone")
            }
        }
    }

    private fun accountWithToken(): Account {
        return asUser {
            tokensService.generateNewToken()
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

}