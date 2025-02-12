package net.nemerosa.ontrack.common

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

/**
 * Sanitizing the HTML, keeping only text nodes and basic styling (see [Safelist.simpleText])
 */
val String.safeHtml: String?
    get() {
        val allowedTags = setOf("b", "em", "i", "strong", "u", "a", "code") // Allowed tags
        val tagRegex = Regex("</?([a-zA-Z0-9]+)[^>]*>") // Regex to match HTML tags

        // Step 1: Escape unknown tags
        val preprocessed = tagRegex.replace(this) { matchResult ->
            val tagName = matchResult.groupValues[1].lowercase()
            if (tagName in allowedTags) {
                matchResult.value // Keep allowed tags
            } else {
                matchResult.value.replace("<", "&lt;").replace(">", "&gt;") // Escape unknown tags
            }
        }

        // Step 2: Sanitize using JSoup (this won't remove already escaped tags)
        val safeList = Safelist.simpleText()
            .addTags("a", "b", "i", "u", "strong", "em")
            .addAttributes("a", "href")
            .addProtocols("a", "href", "http", "https")

        return Jsoup.clean(preprocessed, safeList)
    }
