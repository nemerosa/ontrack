package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.*
import java.time.LocalDateTime

/**
 * Sets an auto promotion property on a promotion level.
 */
var Project.autoVersioningProperty: AutoVersioningProjectProperty?
    get() = getProperty(AUTO_VERSIONING_PROJECT_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(AUTO_VERSIONING_PROJECT_PROPERTY, value)
        } else {
            deleteProperty(AUTO_VERSIONING_PROJECT_PROPERTY)
        }
    }

const val AUTO_VERSIONING_PROJECT_PROPERTY =
    "net.nemerosa.ontrack.extension.av.project.AutoVersioningProjectPropertyType"

data class AutoVersioningProjectProperty(
    val branchIncludes: List<String>? = null,
    val branchExcludes: List<String>? = null,
    val lastActivityDate: LocalDateTime? = null,
)
