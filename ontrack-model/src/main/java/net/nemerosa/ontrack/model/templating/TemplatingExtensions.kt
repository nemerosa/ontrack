package net.nemerosa.ontrack.model.templating

fun Map<String, String>.getRequiredTemplatingParam(key: String): String =
    this[key] ?: throw TemplatingMissingConfigParam(key)

fun Map<String, String>.getBooleanTemplatingParam(key: String, defaultValue: Boolean = false): Boolean =
    this[key]?.toBooleanStrict() ?: defaultValue
