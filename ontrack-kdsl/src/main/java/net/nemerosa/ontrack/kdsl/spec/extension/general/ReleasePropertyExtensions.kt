package net.nemerosa.ontrack.kdsl.spec.extension.general

import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

/**
 * Sets a release property (label) on a build.
 *
 * Alias for [setReleaseProperty].
 */
var Build.label: String?
    get() = releaseProperty
    set(value) {
        releaseProperty = value
    }

/**
 * Sets a release property (label) on a build.
 */
var Build.releaseProperty: String?
    get() = getProperty(RELEASE_PROPERTY)?.path("name")?.asText()
    set(value) {
        if (value != null) {
            setProperty(RELEASE_PROPERTY, mapOf("name" to value))
        } else {
            deleteProperty(RELEASE_PROPERTY)
        }
    }

const val RELEASE_PROPERTY = "net.nemerosa.ontrack.extension.general.ReleasePropertyType"