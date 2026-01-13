package net.nemerosa.ontrack.model.templating

fun TemplatingSourceConfig.getRequiredString(name: String): String =
    getString(name) ?: throw TemplatingMissingConfigParam(name)

fun TemplatingSourceConfig.getRequiredInt(name: String): Int =
    getInt(name) ?: throw TemplatingMissingConfigParam(name)

inline fun <reified E : Enum<E>> TemplatingSourceConfig.getEnum(name: String): E? {
    val value = getString(name) ?: return null
    return enumValueOf<E>(value)
}
