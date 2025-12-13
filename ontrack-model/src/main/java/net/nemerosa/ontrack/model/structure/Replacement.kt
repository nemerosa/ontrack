package net.nemerosa.ontrack.model.structure

import java.util.regex.Pattern

data class Replacement(
    private val regex: String,
    private val replacement: String,
) {

    fun replace(value: String): String {
        if (value.isBlank()) {
            return value
        } else if (regex.isNotBlank() && replacement.isNotBlank()) {
            val m = Pattern.compile(regex).matcher(value)
            val s = StringBuffer()
            while (m.find()) {
                m.appendReplacement(s, replacement)
            }
            m.appendTail(s)
            return s.toString()
        } else {
            return value
        }
    }

    companion object {
        fun replacementFn(replacements: List<Replacement>): (String) -> String =
            { value ->
                var transformedValue = value
                for (replacement in replacements) {
                    transformedValue = replacement.replace(transformedValue)
                }
                transformedValue
            }
    }
}
