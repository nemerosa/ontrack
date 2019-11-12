package net.nemerosa.ontrack.common

/**
 * Converts a [String] into a [Version].
 */
fun String.toVersion() = Version.parseVersion(this)
