package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPostProcessing
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.GitHubRepositoryContext
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.github.autoversioning.GitHubPostProcessingSettings
import net.nemerosa.ontrack.kdsl.spec.extension.github.autoversioning.gitHubPostProcessing
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import org.junit.jupiter.api.Test
import java.util.*

@TestOnGitHubPostProcessing
class ACCAutoVersioningPostProcessing : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning post processing`() {
        withTestGitHubPostProcessingRepository {
            withAutoVersioning {
                // Ontrack as a dependency
                val ontrackDep = branchWithPromotion(promotion = "RELEASE")
                // Sample project to automatically upgrade & post-process
                project {
                    branch {
                        val configName = configuredForGitHubRepository(ontrack)
                        // Configures the GitHub post processing
                        ontrack.settings.gitHubPostProcessing.set(
                            GitHubPostProcessingSettings(
                                config = configName,
                                repository = "${ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.org}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.repository}",
                                workflow = ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.workflow,
                                branch = ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.branch,
                                retries = 10,
                                retriesDelaySeconds = 30,
                            )
                        )
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

                        waitForAutoVersioningCompletion(timeout = 300_000L)

                        assertThatGitHubRepository {
                            fileContains("gradle.properties", timeout = 300_000L) {
                                "${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.property} = ${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.version}"
                            }
                        }
                    }
                }
            }
        }
    }

}