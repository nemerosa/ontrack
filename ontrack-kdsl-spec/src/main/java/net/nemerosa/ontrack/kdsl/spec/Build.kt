package net.nemerosa.ontrack.kdsl.spec

/**
 * Representation of a build.
 *
 * @property id Build ID
 * @property name Build name
 * @property description Build description
 */
class Build(
    val id: UInt,
    val name: String,
    val description: String?,
)
