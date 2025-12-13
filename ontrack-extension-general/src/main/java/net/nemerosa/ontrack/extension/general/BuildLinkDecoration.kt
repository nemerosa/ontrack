package net.nemerosa.ontrack.extension.general

/**
 * List of build links, plus an indicator (link to the build page)
 * showing if there is extra links.
 *
 * @property buildId ID of the parent link
 * @property linksCount Number of downstream links
 */
class BuildLinkDecorationList(
        val buildId: Int, // Needed for NextUI
        val linksCount: Int, // Needed for NextUI
)
