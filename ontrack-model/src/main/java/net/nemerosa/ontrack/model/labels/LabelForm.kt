package net.nemerosa.ontrack.model.labels

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import net.nemerosa.ontrack.common.RGB_COLOR_REGEX

data class LabelForm(
        @get:Pattern(regexp = LABEL_REGEX, message = "The category must comply with format $LABEL_REGEX")
        val category: String?,
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = LABEL_REGEX, message = "The name must comply with format $LABEL_REGEX")
        val name: String,
        val description: String?,
        @get:NotNull(message = "The color is required.")
        @get:Pattern(regexp = "#[a-fA-F0-9]{6}", message = "The color must comply with format #[a-fA-F0-9]{6}")
        val color: String
) {
    /**
     * Validation of the form
     */
    fun validate() {
        validate(category, LABEL_REGEX, "category")
        validate(name, LABEL_REGEX, "name")
        validate(color, RGB_COLOR_REGEX, "color")
    }

    private fun validate(value: String?, regex: String, field: String) {
        if (value != null && !regex.toRegex().matches(value)) {
            throw LabelFormatException(
                    "Field `$field` must comply with format: $regex"
            )
        }
    }
}

/**
 * Regular expression to validate a category or a name.
 */
const val LABEL_REGEX: String = "[A-Za-z0-9.\\-_]+"
