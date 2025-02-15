package net.nemerosa.ontrack.common

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

/**
 * Default tags to accept
 */
val defaultTags = setOf(
    "b",
    "em",
    "i",
    "strong",
    "u",
    "a",
    "code",
    "ul",
    "li",
)

/**
 * Sanitizing the HTML, keeping only text nodes and basic styling (see [Safelist.simpleText])
 */
fun String.safeHtml(
    allowedTags: Set<String> = defaultTags,
): String {
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
        .addTags(*allowedTags.toTypedArray())
        .addAttributes("a", "href")
        .addProtocols("a", "href", "http", "https", "mock")

    return Jsoup.clean(preprocessed, safeList)
}
