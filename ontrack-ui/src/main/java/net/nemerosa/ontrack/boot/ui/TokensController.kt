package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

/**
 * Management of tokens
 */
@RestController
@RequestMapping("/rest/tokens")
class TokensController(
    private val tokensService: TokensService
) {

    /**
     * Creates a token
     */
    @PostMapping("create/{name}")
    fun createToken(@PathVariable name: String): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(
            TokenResponse(
                tokensService.generateNewToken(
                    options = TokenOptions(
                        name = name,
                    )
                )
            )
        )
    }

    /**
     * Revokes all tokens
     */
    @PostMapping("revokeAll")
    fun revokeAll(): ResponseEntity<RevokeAllResponse> {
        val count = tokensService.revokeAll()
        return ResponseEntity.ok(RevokeAllResponse(count))
    }

    /**
     * Revokes for an account
     */
    @PostMapping("account/{account}/revoke")
    fun revokeForAccount(@PathVariable account: Int): ResponseEntity<TokenResponse> {
        tokensService.revokeAllTokens(account)
        return ResponseEntity.ok(TokenResponse(null))
    }

    /**
     * Token response
     */
    data class TokenResponse(val token: Token?)

    /**
     * Revoke all tokens response
     */
    data class RevokeAllResponse(val count: Int)

    /**
     * Token creation parameters
     */
    data class TokenGenerationRequest(
        val duration: Int,
        val unit: TimeUnit
    )

}