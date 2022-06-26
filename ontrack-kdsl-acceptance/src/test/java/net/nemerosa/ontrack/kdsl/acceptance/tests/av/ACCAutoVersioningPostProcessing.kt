package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPostProcessing
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.GitHubRepositoryContext
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import java.util.*

@TestOnGitHubPostProcessing
class ACCAutoVersioningPostProcessing : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning post processing`() {
        withTestGitHubPostProcessingRepository {
            withAutoVersioning {
                // TODO Configures the GitHub post processing
                // Ontrack as a dependency
                val ontrackDep = branchWithPromotion(promotion = "RELEASE")
                // Sample project to automatically upgrade & post-process
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = ontrackDep.project.name,
                                    sourceBranch = ontrackDep.name,
                                    sourcePromotion = "RELEASE",
                                    targetPath = "gradle.properties",
                                    targetProperty = ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.property,
                                    postProcessing = "github",
                                    postProcessingConfig = mapOf(
                                        "dockerImage" to "openjdk:11",
                                        "dockerCommand" to "./gradlew dependencies --write-locks",
                                        "commitMessage" to "Dependency locks",
                                    ).asJson(),
                                )
                            )
                        )

                        // Creates a new promoted version of the dependency
                        ontrackDep.apply {
                            build(name = ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.version!!) {
                                promote("RELEASE")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            fileContains("gradle.properties") {
                                "${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.property} = ${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.version}"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun withTestGitHubPostProcessingRepository(
        code: GitHubRepositoryContext.() -> Unit,
    ) {
        // Unique name for the repository
        val uuid = UUID.randomUUID().toString()
        val repo = "ontrack-auto-versioning-test-$uuid"

        // Forking the sample repository
        gitHubClient.postForLocation(
            "/repos/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.org}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.repository}/forks",
            mapOf(
                "organization" to ACCProperties.GitHub.organization
            )
        )

        // TODO Waiting until the repository is created

        // Rename the repository
        gitHubClient.patchForObject(
            "/repos/${ACCProperties.GitHub.organization}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.repository}",
            mapOf(
                "name" to repo
            ),
            JsonNode::class.java
        )

        // Working with this repository
        try {

            // Context
            val context = GitHubRepositoryContext(repo)

            // Running the code
            context.code()


        } finally {
            // Deleting the repository
            gitHubClient.delete("/repos/${ACCProperties.GitHub.organization}/$repo")
        }
    }

}