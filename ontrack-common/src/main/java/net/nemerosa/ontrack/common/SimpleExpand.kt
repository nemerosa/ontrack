package net.nemerosa.ontrack.common

import java.net.URLEncoder

/**
 * Simple expansion of string templates using `{var}` as a replaceable pattern and a map of string values.
 */
object SimpleExpand {

    /**
     * Regular expression to identify the replacements
     */
    @Suppress("RegExpRedundantEscape")
    private val regex = "\\{([A-Za-z0-9_]+)(?:\\|([A-Za-z0-9_]+))?\\}".toRegex()

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
            val filterName = if (m.groupValues.size > 2) {
                m.groupValues[2]
            } else {
                null
            }
            val value = values[varName]
            if (value != null) {
                if (filterName.isNullOrBlank()) {
                    value
                } else {
                    filter(value, filterName)
                }
            } else {
                mapNullTo
            }
        }

    /**
     * Transforms a string using a filter
     *
     * @param value String to transform
     * @param filter Name of the transformation
     * @return Transformed string
     */
    fun filter(
        value: String,
        filter: String,
    ): String = when(filter) {
        "urlencode" -> URLEncoder.encode(value, Charsets.UTF_8)
        "uppercase" -> value.uppercase()
        "lowercase" -> value.lowercase()
        else -> error("Unknown filter: $filter")
    }

}