package net.nemerosa.ontrack.model.structure


/**
 * Name suitable for generating a code variable.
 */
val ProjectEntityType.varName: String
    get() = name.lowercase()
        .split("_")
        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
        .replaceFirstChar { it.lowercase() }

/**
 * Name suitable for generating a code type.
 */
val ProjectEntityType.typeName: String
    get() = varName.replaceFirstChar { it.titlecase() }
