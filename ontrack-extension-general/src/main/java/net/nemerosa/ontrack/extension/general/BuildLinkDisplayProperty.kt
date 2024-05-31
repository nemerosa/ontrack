package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration of the display options for build links
 * targeting a project.
 *
 * @see [ReleasePropertyType]
 *
 * @property useLabel Configuration at project label to specify that a
 * build link decoration should use the release/label
 * of a build when available. By default, it displays
 * the release name if available, and then the build name as a default.
 */
class BuildLinkDisplayProperty(
    @APIDescription(
        """Configuration at project label to specify that a
            build link decoration should use the release/label
            of a build when available. By default, it displays
            the release name if available, and then the build name as a default."""
    )
    val useLabel: Boolean
)

fun BuildLinkDisplayProperty?.getLabel(
    releaseProperty: ReleaseProperty?
): String? =
    if (this == null || useLabel) {
        releaseProperty?.name
    } else {
        null
    }
