package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.TokensService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
                assertNotNull(it.validUntil) { validUntil ->
                    assertTrue(validUntil > it.creation)
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

}