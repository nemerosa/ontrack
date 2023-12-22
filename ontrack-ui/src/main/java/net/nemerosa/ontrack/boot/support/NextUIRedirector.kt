package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokenScope
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class NextUIRedirector(
    private val tokensService: TokensService,
) {

    companion object {
        const val PARAM_TOKEN = "token"
        const val PARAM_HREF = "href"
    }

    /**
     * Computes the URI to which the user must be redirected for accessing the NextUI is an
     * authenticated way.
     *
     * @param tokenCallback Next UI endpoint which needs to receive the token
     * @param href Final Next UI URL that must be displayed to the user (if null or blank, a default URL will be used
     * on Next UI side)
     * @return URI to be used for the redirection
     */
    fun redirectURI(
        tokenCallback: String,
        href: String?,
    ): String {
        // Gets any existing token and deletes it
        val existing = tokensService.getCurrentToken(tokenCallback)
        if (existing != null) {
            tokensService.revokeToken(tokenCallback)
        }
        // Generates a token for the logged user
        val generatedToken = tokensService.generateNewToken(
            TokenOptions(
                name = tokenCallback,
                scope = TokenScope.NEXT_UI,
            )
        )
        // Token value
        val tokenValue = generatedToken.value
        // Callback URL
        var url = "${tokenCallback}?${PARAM_TOKEN}=$tokenValue"
        if (!href.isNullOrBlank()) {
            url += "&${PARAM_HREF}=${URLEncoder.encode(href, Charsets.UTF_8)}"
        }
        // OK
        return url
    }

}