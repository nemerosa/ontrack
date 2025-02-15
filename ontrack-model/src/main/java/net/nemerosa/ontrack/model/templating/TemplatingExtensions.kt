package net.nemerosa.ontrack.model.templating

fun Map<String, String>.getRequiredTemplatingParam(key: String): String =
    this[key] ?: throw TemplatingMissingConfigParam(key)

fun Map<String, String>.getBooleanTemplatingParam(key: String, defaultValue: Boolean = false): Boolean =
    this[key]?.toBooleanStrict() ?: defaultValue

fun Map<String, String>.getListStringsTemplatingParam(key: String): List<String>? {
    val value = this[key] ?: return null
    return value.split(",").mapNotNull {
        it.trim().takeIf { it.isNotBlank() }
    }
}

inline fun <reified E : Enum<E>> Map<String, String>.getEnumTemplatingParam(key: String): E? {
    val value = this[key] ?: return null
    return enumValueOf<E>(value)
}
