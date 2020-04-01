package net.nemerosa.ontrack.model.structure

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class NameDescription(
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = NAME, message = "The name $NAME_MESSAGE_SUFFIX")
        val name: String,
        val description: String?
) {

    companion object {

        /**
         * List of allowed characters
         */
        private const val ALLOWED_CHARS = "A-Za-z0-9\\._-"

        /**
         * Regular expression to validate a name.
         */
        const val NAME = "[$ALLOWED_CHARS]+"

        /**
         * Valid name regular expression
         */
        private val VALID_NAME_REGEX = NAME.toRegex()

        /**
         * Regular expression object for the non matching characters
         */
        private val UNALLOWED_CHARS_REGEX = "[^$ALLOWED_CHARS]".toRegex()

        /**
         * Message associated with the regular expression
         */
        const val NAME_MESSAGE_SUFFIX = "can only have letters, digits, dots (.), dashes (-) or underscores (_)."


        /**
         * Simple builder
         */
        @JvmStatic
        fun nd(name: String, description: String?) = NameDescription(name, description)

        /**
         * Makes sure the given <code>name</code> is escaped properly before being used as a valid name.
         *
         * @param name Name to convert
         * @return Name which is safe to use
         * @see #NAME
         */
        @JvmStatic
        fun escapeName(name: String?): String =
                when {
                    name.isNullOrBlank() -> throw IllegalArgumentException("Blank or null is not a valid name.")
                    VALID_NAME_REGEX.matches(name) -> name
                    else -> name.replace(UNALLOWED_CHARS_REGEX, "-")
                }

    }

    @JvmOverloads
    fun asState(disabled: Boolean = false) = NameDescriptionState(name, description, disabled)

}
