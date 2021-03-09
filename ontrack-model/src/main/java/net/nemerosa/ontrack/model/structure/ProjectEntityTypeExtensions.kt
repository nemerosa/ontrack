package net.nemerosa.ontrack.model.structure


/**
 * Name suitable for generating a code variable.
 */
val ProjectEntityType.varName: String
    get() = name.toLowerCase().split("_").joinToString("") { it.capitalize() }.decapitalize()
/**
 * Name suitable for generating a code type.
 */
val ProjectEntityType.typeName: String
    get() = varName.capitalize()
