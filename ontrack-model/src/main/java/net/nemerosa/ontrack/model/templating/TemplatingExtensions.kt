package net.nemerosa.ontrack.model.templating

fun Map<String, String>.getRequiredTemplatingParam(key: String) =
    this[key] ?: throw TemplatingMissingConfigParam(key)
