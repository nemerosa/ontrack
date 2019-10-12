package net.nemerosa.ontrack.gradle

import net.nemerosa.ontrack.dsl.Build
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class OntrackLastReleases : AbstractOntrackTask() {

    @Input
    var ontrackProject: String = "ontrack"

    @Input
    var ontrackReleasePromotionLevel: String = "RELEASE"

    @Input
    var releaseCount: Int = 5

    @Input
    var releasePattern: String = ".*"

    var releases: List<Build> = emptyList()

    companion object {
        val RELEASE_BRANCH = "release-.*".toRegex()
    }

    @TaskAction
    fun run() {
        logger.info("Getting the last releases")
        // Gets the Ontrack client
        val ontrack = getOntrackClient(false)
        // Gets the Ontrack project
        val project = ontrack.project(ontrackProject)
        // List of releases
        val result = mutableListOf<Build>()
        // Gets all branches
        var count = 0
        project.branches.forEach { branch ->
            // Only release/ branches
            if (count < releaseCount && branch.name.matches(RELEASE_BRANCH) && !branch.isDisabled) {
                // ... and gets the last RELEASE build for each of them
                val builds = branch.standardFilter(mapOf(
                        "count" to 1,
                        "withPromotionLevel" to ontrackReleasePromotionLevel
                ))
                if (builds.isNotEmpty()) {
                    val build = builds.first()
                    if (build.name.matches(releasePattern.toRegex())) {
                        result += build
                        count++
                    }
                }
            }
        }
        this.releases = result
    }

}
