package net.nemerosa.ontrack.gradle

import com.fasterxml.jackson.databind.JsonNode
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
    var releases: List<String> = emptyList()

    @TaskAction
    fun run() {
        logger.info("Getting the last releases")
        // Release branch regex
        val releaseBranchRegex = "release-$releaseBranchPattern"
        logger.debug("releaseBranchRegex={}", releaseBranchRegex)
        // Gets the Ontrack client
        val ontrack = getOntrackClient(true)
        // Running the GraphQL query to get all last RELEASE builds on the last release branches
        val data = ontrack.graphQL(
                """
                    query LastReleases(
                        ${'$'}ontrackProject: String!,
                        ${'$'}releaseBranchRegex: String!,
                        ${'$'}ontrackReleasePromotionLevel: String!,
                    ) {
                      projects(name: ${'$'}ontrackProject) {
                        branches(name: ${'$'}releaseBranchRegex) {
                          builds(filter: {withPromotionLevel: ${'$'}ontrackReleasePromotionLevel, count: 1}) {
                            name
                          }
                        }
                      }
                    }
                """,
                mapOf(
                        "ontrackProject" to ontrackProject,
                        "releaseBranchRegex" to releaseBranchRegex,
                        "ontrackReleasePromotionLevel" to ontrackReleasePromotionLevel
                )
        )
        // Collecting all build names
        val result = mutableListOf<String>()
        var count = 0
        val branches: JsonNode = data.path("projects").path(0).path("branches")
        branches.forEach { branch ->
                    if (count < releaseCount) {
                        val buildName = branch.path("builds").path(0)
                                ?.path("name")?.asText()
                        if (!buildName.isNullOrBlank()) {
                            result.add(buildName)
                            count++
                        }
                    }
                }
        // OK
        logger.info("Releases: $result")
        this.releases = result
    }

}
