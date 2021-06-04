package net.nemerosa.ontrack.gradle

import net.nemerosa.ontrack.dsl.v4.Build
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class OntrackLastReleases : AbstractOntrackTask() {

    @Input
    var ontrackProject: String = "ontrack"

    @Input
    var ontrackReleasePromotionLevel: String = "RELEASE"

    @Input
    var releaseCount: Int = 5

    @Input
    var releaseBranchPattern: String = "release-.*"

    @Internal
    var releases: List<Build> = emptyList()

    @TaskAction
    fun run() {
        logger.info("Getting the last releases")
        // Gets the Ontrack client
        val ontrack = getOntrackClient(false)
        // Gets the Ontrack project
        val project = ontrack.project(ontrackProject)
        // List of releases
        val result = mutableListOf<Build>()
        // Patterns
        val releaseBranchRegex = "release-$releaseBranchPattern".toRegex()
        val releaseBuildRegex = "${releaseBranchPattern}\\.[\\d]+".toRegex()
        logger.debug("releaseBranchRegex=$releaseBranchRegex")
        logger.debug("releaseBuildRegex=$releaseBuildRegex")
        // Gets all branches
        var count = 0
        project.branches.forEach { branch ->
            // Only release/ branches
            if (count < releaseCount && branch.name.matches(releaseBranchRegex) && !branch.isDisabled) {
                logger.debug("Scanning branch ${branch.name}...")
                // ... and gets the last RELEASE build for each of them
                val builds = branch.standardFilter(mapOf(
                        "count" to 1,
                        "withPromotionLevel" to ontrackReleasePromotionLevel
                ))
                if (builds.isNotEmpty()) {
                    val build = builds.first()
                    logger.debug("Candidate build: ${build.name}")
                    if (build.name.matches(releaseBuildRegex)) {
                        logger.debug("Matched build: ${build.name}")
                        result += build
                        count++
                    }
                }
            }
        }
        logger.info("Releases: ${ result.map { it.name } }")
        this.releases = result
    }

}
