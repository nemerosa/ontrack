package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build

/**
 * Configuration of the display options for build links
 * targeting a project.
 *
 * @see [ReleasePropertyType]
 *
 * @property useLabel Configuration at project label to specify that a
 * build link decoration should use the release/label
 * of a build when available. By default, it displays
 * the build name.
 */
class BuildLinkDisplayProperty(
        val useLabel: Boolean
)

fun BuildLinkDisplayProperty?.getLabel(
        build: Build,
        labelProperty: ReleaseProperty?
): String = if (this == null || labelProperty == null || !useLabel) {
    build.name
} else {
    labelProperty.name
}
