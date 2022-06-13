package net.nemerosa.ontrack.kdsl.spec.extension.general

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

var Project.buildLinkDisplayProperty: BuildLinkDisplayProperty?
    get() = getProperty(BUILD_LINK_DISPLAY_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(BUILD_LINK_DISPLAY_PROPERTY, value)
        } else {
            deleteProperty(BUILD_LINK_DISPLAY_PROPERTY)
        }
    }

var Project.buildLinkDisplayUseLabel: Boolean?
    get() = buildLinkDisplayProperty?.useLabel
    set(value) {
        if (value != null) {
            buildLinkDisplayProperty = BuildLinkDisplayProperty(useLabel = value)
        } else {
            buildLinkDisplayProperty = null
        }
    }

const val BUILD_LINK_DISPLAY_PROPERTY = "net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType"

data class BuildLinkDisplayProperty(
    val useLabel: Boolean,
)
