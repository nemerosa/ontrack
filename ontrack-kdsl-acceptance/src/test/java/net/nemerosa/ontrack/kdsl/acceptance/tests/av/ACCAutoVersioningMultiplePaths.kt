package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

@TestOnGitHub
class ACCAutoVersioningMultiplePaths : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning on multiple paths`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "one-version = 1.0.0"
                }
                repositoryFile("other.properties") {
                    "one-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties,other.properties",
                                    targetProperty = "one-version",
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            fileContains("gradle.properties") {
                                "one-version = 2.0.0"
                            }
                            fileContains("other.properties") {
                                "one-version = 2.0.0"
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on multiple branches`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties", branch = "main") {
                    "one-version = 1.0.0"
                }
                repositoryFile("gradle.properties", branch = "release/1.0") {
                    "one-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    val branchMain = branch {
                        configuredForGitHubRepository(ontrack, scmBranch = "main")
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "one-version",
                                )
                            )
                        )
                    }
                    val branchRelease = branch {
                        configuredForGitHubRepository(ontrack, scmBranch = "release/1.0")
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "one-version",
                                )
                            )
                        )
                    }

                    dependency.apply {
                        build(name = "2.0.0") {
                            promote("IRON")
                        }
                    }

                    waitForAutoVersioningCompletion()

                    assertThatGitHubRepository {
                        fileContains("gradle.properties", branch = "main") {
                            "one-version = 2.0.0"
                        }
                        fileContains("gradle.properties", branch = "release/1.0") {
                            "one-version = 2.0.0"
                        }
                    }

                }
            }
        }
    }

}