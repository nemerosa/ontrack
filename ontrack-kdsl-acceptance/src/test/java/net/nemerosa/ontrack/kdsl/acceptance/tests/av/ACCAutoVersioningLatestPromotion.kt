package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.scm.mockScmBranchProperty
import org.junit.jupiter.api.Test

class ACCAutoVersioningLatestPromotion : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning based on latest version triggers an update if on latest version`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {

                val release12 = project {
                    
                    configuredForMockScm()

                    branch("release-1.0") {
                        mockScmBranchProperty = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.1") {
                        mockScmBranchProperty = "release/1.1"
                        promotion("IRON")
                        build("1.1.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.2") {
                        mockScmBranchProperty = "release/1.2"
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
                        configuredForMockRepository()
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

                        assertThatMockScmRepository {

                            hasPR(
                                from = "feature/auto-upgrade-${release12.project.name}-1.2.1-*",
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
        withMockScmRepository(ontrack) {
            withAutoVersioning {

                val release10 = project {

                    configuredForMockScm()

                    val release10 = branch("release-1.0") {
                        mockScmBranchProperty = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                        this
                    }
                    branch("release-1.1") {
                        mockScmBranchProperty = "release/1.1"
                        promotion("IRON")
                        build("1.1.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.2") {
                        mockScmBranchProperty = "release/1.2"
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
                        configuredForMockRepository()
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

                        assertThatMockScmRepository {

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
        withMockScmRepository(ontrack) {
            withAutoVersioning {

                val release20 = project {

                    configuredForMockScm()

                    branch("release-1.0") {
                        mockScmBranchProperty = "release/1.0"
                        promotion("IRON")
                        build("1.0.0") {
                            promote("IRON")
                        }
                        this
                    }
                    branch("release-1.1") {
                        mockScmBranchProperty = "release/1.1"
                        promotion("IRON")
                        build("1.1.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-1.2") {
                        mockScmBranchProperty = "release/1.2"
                        promotion("IRON")
                        build("1.2.0") {
                            promote("IRON")
                        }
                    }
                    branch("release-2.0") {
                        mockScmBranchProperty = "release/2.0"
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
                        configuredForMockRepository()
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

                        assertThatMockScmRepository {

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