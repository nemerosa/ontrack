package net.nemerosa.ontrack.model.support

/**
 * Converts a map of strings into a list of [NameValue]s
 */
fun Map<String, String>.toNameValues() = map { (name, value) -> NameValue(name, value) }
