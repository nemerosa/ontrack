package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Management of tokens
 */
@RestController
@RequestMapping("/rest/tokens")
class TokensController(
        private val tokensService: TokensService
) {

    /**
     * Gets the current token
     */
    @get:GetMapping("current")
    val currentToken: ResponseEntity<TokenResponse>
        get() =
            tokensService.currentToken.let { ResponseEntity.ok(TokenResponse(it)) }

    /**
     * Generates a new token for the current user
     */
    @PostMapping("new")
    fun generateNewToken(): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(TokenResponse(tokensService.generateNewToken()))
    }

    /**
     * Revokes the current token
     */
    @PostMapping("revoke")
    fun revokeToken(): ResponseEntity<TokenResponse> {
        tokensService.revokeToken()
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
    @PostMapping("revoke/{account}")
    fun revokeForAccount(@PathVariable account: Int): ResponseEntity<TokenResponse> {
        tokensService.revokeToken(account)
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

}