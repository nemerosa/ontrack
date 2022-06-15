package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import java.util.*

@TestOnGitHubPlayground
class ACCAutoVersioningSpecialFiles : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning in a very big file`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("Jenkinsfile") {
                    (listOf("@Library(\"my-library@0.1.0\") _") + (1..4000).map {
                        UUID.randomUUID().toString()
                    }).joinToString("\n")
                }
                val library = branchWithPromotion(promotion = "GOLD")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = library.project.name,
                                    sourceBranch = library.name,
                                    sourcePromotion = "GOLD",
                                    targetPath = "Jenkinsfile",
                                    targetRegex = """@Library\("my-library@(.*)"\).*""",
                                )
                            )
                        )

                        library.apply {
                            build(name = "0.1.1") {
                                promote("GOLD")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${library.project.name}-0.1.1-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("Jenkinsfile") {
                                """@Library("my-library@0.1.1") _"""
                            }
                        }

                    }
                }
            }
        }
    }

}