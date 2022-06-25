package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

@TestOnGitHub
class ACCAutoVersioningProperties : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning based on Java properties file explicitly`() {
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
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    targetPropertyType = "properties",
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
    fun `Auto versioning based on NPM`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("package.json") {
                    """
                        {
                          "name" : "@nemerosa/testing",
                          "dependencies": {
                            "@nemerosa/module-1" : "^4.0.1",
                            "@nemerosa/module-2" : "^1.0.0",
                            "@nemerosa/module-3" : "^7.3.1",
                            "@nemerosa/module-4" : "^5.1.2"
                          }
                        }
                    """.trimIndent()
                }
                val module3 = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = module3.project.name,
                                    sourceBranch = module3.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "package.json",
                                    targetProperty = "@nemerosa/module-3",
                                    targetPropertyType = "npm",
                                )
                            )
                        )

                        module3.apply {
                            build(name = "7.3.2") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${module3.project.name}-7.3.2-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("package.json") {
                                """
                                    {
                                      "name" : "@nemerosa/testing",
                                      "dependencies" : {
                                        "@nemerosa/module-1" : "^4.0.1",
                                        "@nemerosa/module-2" : "^1.0.0",
                                        "@nemerosa/module-3" : "^7.3.2",
                                        "@nemerosa/module-4" : "^5.1.2"
                                      }
                                    }
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning based on NPM using devDependencies`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("package.json") {
                    """
                        {
                          "name" : "@nemerosa/testing",
                          "devDependencies" : {
                            "@nemerosa/module-1" : "^4.0.1",
                            "@nemerosa/module-2" : "^1.0.0",
                            "@nemerosa/module-3" : "^7.3.1",
                            "@nemerosa/module-4" : "^5.1.2"
                          }
                        }
                    """.trimIndent()
                }
                val module3 = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = module3.project.name,
                                    sourceBranch = module3.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "package.json",
                                    targetProperty = "@nemerosa/module-3",
                                    targetPropertyType = "npm",
                                )
                            )
                        )

                        module3.apply {
                            build(name = "7.3.2") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${module3.project.name}-7.3.2-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("package.json") {
                                """
                                    {
                                      "name" : "@nemerosa/testing",
                                      "devDependencies" : {
                                        "@nemerosa/module-1" : "^4.0.1",
                                        "@nemerosa/module-2" : "^1.0.0",
                                        "@nemerosa/module-3" : "^7.3.2",
                                        "@nemerosa/module-4" : "^5.1.2"
                                      }
                                    }
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

}