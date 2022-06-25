package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.git.gitBranchConfigurationPropertyBranch
import org.junit.jupiter.api.Test

@TestOnGitHub
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

    @Test
    fun `Auto versioning based on latest version does not trigger an update if not on latest version 10 for 1x when already on 11`() {
        withTestGitHubRepository {
            withAutoVersioning {

                val release10 = project {

                    configuredForGitHub(ontrack)

                    val release10 = branch("release-1.0") {
                        gitBranchConfigurationPropertyBranch = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                        this
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
                    }

                    release10
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
                                    sourceProject = release10.project.name,
                                    sourceBranch = """release\/1\..*""",
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dep-version",
                                )
                            )
                        )

                        release10.apply {
                            build(name = "1.0.1") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoPR(
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                "dep-version = 1.1.0"
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning based on latest version does not trigger an update if not on different version 20 for 1x`() {
        withTestGitHubRepository {
            withAutoVersioning {

                val release20 = project {

                    configuredForGitHub(ontrack)

                    branch("release-1.0") {
                        gitBranchConfigurationPropertyBranch = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                        this
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
                    }
                    branch("release-2.0") {
                        gitBranchConfigurationPropertyBranch = "release/2.0"
                        promotion("IRON")
                        build("2.0.0") {
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
                                    sourceProject = release20.project.name,
                                    sourceBranch = """release\/1\..*""",
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dep-version",
                                )
                            )
                        )

                        release20.apply {
                            build(name = "2.0.1") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoPR(
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                "dep-version = 1.1.0"
                            }
                        }

                    }
                }
            }
        }
    }

}