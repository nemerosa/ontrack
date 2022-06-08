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
                val dependency = branchWithPromotion(promotion = "IRON")
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
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
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

    @Test
    fun `Auto versioning on promotion for a release branch`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(name = "release-1.0", promotion = "IRON")
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
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
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

    @Test
    fun `Auto versioning on promotion for a release branch identified using Git`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(name = "release-1.0", promotion = "IRON") {
                    configuredForGitHubRepository(ontrack, "release/1.0")
                }
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = "release/1.0",
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
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
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