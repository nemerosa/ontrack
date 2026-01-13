package net.nemerosa.ontrack.model.templating

@Deprecated("Use parseTemplatingSourceConfig")
fun parseTemplatingConfig(expression: String): Map<String, String> {
    if (expression.isBlank()) {
        return emptyMap()
    } else {
        val config = mutableMapOf<String, String>()
        val tokens = expression.split("&")
        tokens.forEach { token ->
            val values = token.split("=")
            if (values.size != 2) {
                throw TemplatingConfigFormatException(expression)
            } else {
                val (key, value) = values
                if (key.isNotBlank() && value.isNotBlank()) {
                    config[key.trim()] = value.trim()
                } else {
                    throw TemplatingConfigFormatException(expression)
                }
            }
        }
        return config
    }
}

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
            val values = token.split("=")
            if (values.size != 2) {
                throw TemplatingConfigFormatException(expression)
            } else {
                val (key, value) = values
                if (key.isNotBlank() && value.isNotBlank()) {
                    params.getOrPut(key.trim()) { mutableListOf() }.add(value.trim())
                } else {
                    throw TemplatingConfigFormatException(expression)
                }
            }
        }
        return TemplatingSourceConfig(params)
    }
}
