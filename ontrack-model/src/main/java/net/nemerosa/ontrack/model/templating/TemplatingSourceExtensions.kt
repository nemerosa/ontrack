package net.nemerosa.ontrack.model.templating

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
