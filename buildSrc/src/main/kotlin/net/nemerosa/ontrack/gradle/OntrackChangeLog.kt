package net.nemerosa.ontrack.gradle

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class OntrackChangeLog : AbstractOntrackTask() {

    @Input
    var ontrackProject: String = "ontrack"

    @Input
    var ontrackReleaseBranch: String = ""

    @Input
    var ontrackCurrentBuild: String = ""

    @Input
    var ontrackReleasePromotionLevel: String = "RELEASE"

    @Input
    var format: String = "text"

    @Internal
    var changeLog: String = ""

    @TaskAction
    fun run() {
        if (ontrackReleaseBranch.isBlank()) throw GradleException("Missing ontrackReleaseBranch property")
        logger.info("Getting the Ontrack log for $ontrackProject since last $ontrackReleasePromotionLevel on branch $ontrackReleaseBranch")
        // Gets the Ontrack client
        val ontrack = getOntrackClient(true)

        // Gets the current branch last build (the one being built probably)
        // and the last released build

        val data = ontrack.graphQL(
                """
                    query LastBuilds(
                        ${'$'}ontrackProject: String!,
                        ${'$'}ontrackReleaseBranch: String!,
                        ${'$'}ontrackCurrentBuild: String!,
                        ${'$'}ontrackReleasePromotionLevel: String!,
                    ) {
                      currentBuilds: builds(project: ${'$'}ontrackProject, branch: ${'$'}ontrackReleaseBranch, name: ${'$'}ontrackCurrentBuild) {
                        id
                      }
                      lastReleasesBuilds: builds(project: ${'$'}ontrackProject, branch: ${'$'}ontrackReleaseBranch, buildBranchFilter: {
                        withPromotionLevel: ${'$'}ontrackReleasePromotionLevel,
                        count: 1
                      }) {
                        id
                      }
                    }
                """,
                mapOf(
                        "ontrackProject" to ontrackProject,
                        "ontrackReleaseBranch" to ontrackReleaseBranch,
                        "ontrackCurrentBuild" to ontrackCurrentBuild,
                        "ontrackReleasePromotionLevel" to ontrackReleasePromotionLevel
                )
        )
        val lastBuildId = data.path("currentBuilds").path(0)
                .path("id").asInt()
        val lastReleaseId = data.path("lastReleasesBuilds").path(0)
                .path("id").asInt()

        logger.info("Ontrack last build ID = $lastBuildId")
        logger.info("Ontrack last release ID = $lastReleaseId")

        if (lastBuildId != 0 && lastReleaseId != 0 && lastBuildId != lastReleaseId) {
            val changeLog = ontrack.graphQL(
                    """
                       {
                          gitChangeLog(from: $lastReleaseId, to: $lastBuildId) {
                            export(request: {
                              format: "$format",
                              grouping: "Features=feature|Enhancements=enhancement|Bugs=bug"
                            })
                          }
                        }
                    """
            )
            this.changeLog = changeLog.path("gitChangeLog").path("export").asText()
        } else {
            // No change log
            this.changeLog = ""
        }

        // OK
        logger.info("Changelog:\n$changeLog")
    }

}
