package net.nemerosa.ontrack.common

/**
 * Simple expansion of string templates using `{var}` as a replaceable pattern and a map of string values.
 */
object SimpleExpand {

    /**
     * Regular expression to identify the replacements
     */
    @Suppress("RegExpRedundantEscape")
    private val regex = "\\{([A-Za-z0-9_]+)\\}".toRegex()

    /**
     * Performs an expansion.
     *
     * @param pattern String containing `{var}` replacement patterns. Each `var` must comply with the `[a-z0-9_]+` regular
     * expression.
     * @param values Mapping of `var` names to actual values
     * @param mapNullTo Value to map undefined variables to
     */
    fun expand(
        pattern: String,
        values: Map<String, String?>,
        mapNullTo: String = ""
    ): String =
        regex.replace(pattern) { m ->
            val varName = m.groupValues[1]
            values[varName] ?: mapNullTo
        }

}