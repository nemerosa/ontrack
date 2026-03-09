package net.nemerosa.ontrack.extension.config.model

/**
 * Gets the first non-null non-blank environment variable using the list of names.
 */
fun Map<String, String>.getEnv(vararg names: String): String? =
    names.firstNotNullOfOrNull {
        get(it)?.takeIf { value -> value.isNotBlank() }
    }
