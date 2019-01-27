package net.nemerosa.ontrack.model.labels

/**
 * Input form which allows the creation of label from a token
 * having either one of the following formats:
 *
 * * `name`
 * * `category:name`
 */
class LabelTokenForm(
        val token: String
) {
    fun parse(): LabelForm {
        val index = token.indexOf(":")
        val category: String?
        val name: String
        if (index >= 0) {
            category = token.substringBefore(":").trim()
            name = token.substringAfter(":").trim()
        } else {
            category = null
            name = token.trim()
        }
        // OK
        return LabelForm(
                category = category,
                name = name,
                description = "", // default
                color = "#000000" // default
        )
    }
}
