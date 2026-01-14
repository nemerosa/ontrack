package net.nemerosa.ontrack.service.templating

data class TemplatingExpression(
    val contextKey: String,
    val field: String?,
    val configQuery: String?,
    val filter: String?
) {

    companion object {

        private val regexToken =
            "^([a-zA-Z_]+|#)(?:\\.([a-zA-Z_.-]+))?(?:\\?([^|]+))?(?:\\|([a-zA-Z_-]+))?$".toRegex()

        fun parse(expression: String): TemplatingExpression? {
            val m = regexToken.matchEntire(expression)
            if (m != null) {
                return TemplatingExpression(
                    contextKey = m.groupValues[1],
                    field = m.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() },
                    configQuery = m.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() },
                    filter = m.groupValues.getOrNull(4)?.takeIf { it.isNotBlank() },
                )
            } else {
                return null
            }
        }

    }

}
