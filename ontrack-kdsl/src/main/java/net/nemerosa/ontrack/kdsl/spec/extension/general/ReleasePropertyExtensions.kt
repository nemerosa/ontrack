package net.nemerosa.ontrack.kdsl.spec.extension.general

import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.setProperty

/**
 * Sets a release property (label) on a build.
 *
 * Alias for [setReleaseProperty].
 */
fun Build.setLabel(value: String) = setReleaseProperty(value)

/**
 * Sets a release property (label) on a build.
 */
fun Build.setReleaseProperty(value: String) = setProperty(
    "net.nemerosa.ontrack.extension.general.ReleasePropertyType",
    mapOf(
        "name" to value
    )
)
