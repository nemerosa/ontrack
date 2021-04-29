package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch links graph settings")
class BranchLinksSettings(
    @APIDescription("Dependency depth to take into account when computing the branch links graph")
    val depth: Int,
    @APIDescription("Build history to take into account when computing the branch links graph")
    val history: Int,
    @APIDescription("Maximum number of links to follow per build")
    val maxLinksPerLevel: Int
) {
    companion object {
        /**
         * Default depth for the branch links
         */
        const val DEFAULT_DEPTH = 10

        /**
         * Default history for the branch links
         */
        const val DEFAULT_HISTORY = 10

        /**
         * Default maximum number of links to follow per build
         */
        const val DEFAULT_MAX_LINKS_PER_LEVEL = 20
    }
}
