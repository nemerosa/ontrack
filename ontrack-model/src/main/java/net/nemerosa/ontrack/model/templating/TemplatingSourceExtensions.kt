package net.nemerosa.ontrack.model.templating

/**
 * Full parsing of a templating configuration expression.
 *
 * @param expression The expression to parse (e.g. `name=value&sections=one&sections=two`)
 * @return The parsed configuration
 */
fun parseTemplatingSourceConfig(expression: String?): TemplatingSourceConfig {
    if (expression.isNullOrBlank()) {
        return TemplatingSourceConfig(emptyMap())
    } else {
        val params = mutableMapOf<String, MutableList<String>>()
        val tokens = expression.split("&")
        tokens.forEach { token ->
            if (!token.contains("=")) throw TemplatingConfigFormatException(expression)
            val key = token.substringBefore("=")
            val value = token.substringAfter("=")
            if (key.isNotBlank() && value.isNotBlank()) {
                params.getOrPut(key.trim()) { mutableListOf() }.add(value.trim())
            } else {
                throw TemplatingConfigFormatException(expression)
            }
        }
        return TemplatingSourceConfig(params)
    }
}
