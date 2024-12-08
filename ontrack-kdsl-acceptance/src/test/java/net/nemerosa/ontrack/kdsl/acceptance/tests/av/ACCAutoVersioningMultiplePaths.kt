package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfigPath
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.general.setMetaInfoProperty
import org.junit.jupiter.api.Test

class ACCAutoVersioningMultiplePaths : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning with additional paths`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "one-version = 1.0.0"
                }
                repositoryFile("manifest.toml") {
                    """
                        [global]
                        oneVersion = "1.0.0-toml"
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "one-version",
                                    additionalPaths = listOf(
                                        AutoVersioningSourceConfigPath(
                                            path = "manifest.toml",
                                            property = "global.oneVersion",
                                            propertyType = "toml",
                                            versionSource = "metaInfo/toml",
                                        )
                                    )
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                setMetaInfoProperty("toml", "2.0.0-toml")
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "one-version = 2.0.0"
                            }
                            fileContains("manifest.toml") {
                                """
                                    [global]
                                    oneVersion = "2.0.0-toml"
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning with additional paths using the same path`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        one-version = 1.0.0
                        another-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "one-version",
                                    additionalPaths = listOf(
                                        AutoVersioningSourceConfigPath(
                                            path = "gradle.properties",
                                            property = "another-version",
                                        )
                                    )
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                setMetaInfoProperty("toml", "2.0.0-toml")
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                """
                                    one-version = 2.0.0
                                    another-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on multiple paths`() {
        withMockScmRepository(ontrack) {
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
                        configuredForMockRepository()
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

                        assertThatMockScmRepository {
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
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties", branch = "main") {
                    "one-version = 1.0.0"
                }
                repositoryFile("gradle.properties", branch = "release/1.0") {
                    "one-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForMockRepository("main")
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
                    branch {
                        configuredForMockRepository(scmBranch = "release/1.0")
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

                    assertThatMockScmRepository {
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