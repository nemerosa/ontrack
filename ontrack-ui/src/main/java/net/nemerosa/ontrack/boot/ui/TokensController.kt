package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
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
     * Gets the current token
     */
    @get:GetMapping("current")
    @Deprecated("Use named token. Will be removed in V5.")
    val currentToken: ResponseEntity<TokenResponse>
        get() =
            tokensService.getCurrentToken(TokensService.DEFAULT_NAME).let { ResponseEntity.ok(TokenResponse(it)) }

    /**
     * Generates a new token for the current user
     */
    @PostMapping("new")
    @Deprecated("Use named token. Will be removed in V5.")
    fun generateNewToken(): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(
            TokenResponse(
                tokensService.generateNewToken(
                    options = TokenOptions(
                        name = TokensService.DEFAULT_NAME,
                    )
                )
            )
        )
    }

    /**
     *
     */

    /**
     * Revokes the current token
     */
    @PostMapping("revoke")
    @Deprecated("Use named token. Will be removed in V5.")
    fun revokeToken(): ResponseEntity<TokenResponse> {
        tokensService.revokeToken(TokensService.DEFAULT_NAME)
        return ResponseEntity.ok(TokenResponse(null))
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
     * Generates or prolongates a token for an account
     */
    @PostMapping("account/{account}/generate")
    @Deprecated("Use named token. Will be removed in V5.")
    fun generateForAccount(
        @PathVariable account: Int,
        @RequestBody request: TokenGenerationRequest
    ): ResponseEntity<TokenResponse> {
        val actualDuration = if (request.duration <= 0) {
            null
        } else {
            Duration.of(request.duration.toLong(), request.unit.toChronoUnit())
        }
        val token = tokensService.generateToken(account, actualDuration, actualDuration == null)
        return ResponseEntity.ok(TokenResponse(token))
    }

    /**
     * Gets the token for an account
     */
    @GetMapping("account/{account}")
    @Deprecated("Use named token. Will be removed in V5.")
    fun getTokenForAccount(@PathVariable account: Int): ResponseEntity<TokenResponse> {
        val token = tokensService.getToken(account)?.obfuscate()
        return ResponseEntity.ok(TokenResponse(token))
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