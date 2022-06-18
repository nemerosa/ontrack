package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.git.gitBranchConfigurationPropertyBranch
import org.junit.jupiter.api.Test

@TestOnGitHubPlayground
class ACCAutoVersioningLatestPromotion : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning based on latest version triggers an update if on latest version`() {
        withTestGitHubRepository {
            withAutoVersioning {

                val release12 = project {
                    
                    configuredForGitHub(ontrack)

                    branch("release-1.0") {
                        gitBranchConfigurationPropertyBranch = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.1") {
                        gitBranchConfigurationPropertyBranch = "release/1.1"
                        promotion("IRON")
                        build("1.1.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.2") {
                        gitBranchConfigurationPropertyBranch = "release/1.2"
                        promotion("IRON")
                        build("1.2.0") {
                            promote("IRON")
                        }
                        this
                    }
                }

                repositoryFile("gradle.properties") {
                    "dep-version = 1.1.0"
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = release12.project.name,
                                    sourceBranch = """release\/1\..*""",
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dep-version",
                                )
                            )
                        )

                        release12.apply {
                            build(name = "1.2.1") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasPR(
                                from = "feature/auto-upgrade-${release12.project.name}-1.2.1-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                "dep-version = 1.2.1"
                            }
                        }

                    }
                }
            }
        }
    }

}