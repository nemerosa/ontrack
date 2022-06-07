package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import org.junit.jupiter.api.Test

@TestOnGitHubPlayground
class ACCAutoVersioningCore : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning on promotion`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion("IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-eb0a191797624dd3a48fa681d3061212",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0 
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

}