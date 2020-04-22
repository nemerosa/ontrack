package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Management of tokens
 */
@RestController
@RequestMapping("/rest/tokens")
class TokensController(
        private val tokensService: TokensService
) {

    /**
     * Generates a new token for the current user
     */
    @PostMapping("new")
    fun generateNewToken(): ResponseEntity<String> {
        return ResponseEntity.ok(tokensService.generateNewToken())
    }

}