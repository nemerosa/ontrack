package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ACCAutoVersioningPRTemplate : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Configuration of the PR title and body`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
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
                                    targetProperty = "some-version",
                                    prTitleTemplate = "Version of \${sourceProject} upgraded to \${VERSION}",
                                    prBodyTemplate = "The version of \${sourceProject} in \${PATH} has been upgraded to \${VERSION}.",
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
                            val pr = hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-*",
                                to = "main"
                            )
                            assertEquals(
                                "Version of ${dependency.project.name} upgraded to 2.0.0",
                                pr.title
                            )
                            assertEquals(
                                "The version of ${dependency.project.name} in gradle.properties has been upgraded to 2.0.0.",
                                pr.body
                            )
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Configuration of the PR title and body with a change log`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                // Configuration of this dependency
                lateinit var depTo: Build
                dependency.apply {
                    withMockScmRepository(ontrack) {
                        configuredForMockRepository()
                        // Registering the issues
                        repositoryIssue("ISS-20", "Last issue before the change log")
                        repositoryIssue("ISS-21", "Some new feature")
                        repositoryIssue("ISS-22", "Some fixes are needed")
                        repositoryIssue("ISS-23", "Some nicer UI")
                        // Creating a change log for this dependency
                        build("1.0.0") {
                            promote("IRON")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                        }
                        build("1.1.0") {
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        depTo = build("2.0.0") {
                            withRepositoryCommit("ISS-22 Fixing some bugs", property = false)
                            withRepositoryCommit("ISS-23 Fixing some CSS")
                        }
                    }
                }

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
                                    targetProperty = "some-version",
                                    prTitleTemplate = "Version of \${sourceProject} upgraded to \${VERSION}",
                                    prBodyTemplate = """
                                        The version of ${"$"}{sourceProject} in ${"$"}{PATH} has been upgraded to ${"$"}{VERSION}.
                                        
                                        ${'$'}{av.changeLog}
                                    """.trimIndent(),
                                )
                            )
                        )

                        dependency.apply {
                            depTo.promote("IRON")
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            val pr = hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-*",
                                to = "main"
                            )
                            assertEquals(
                                "Version of ${dependency.project.name} upgraded to 2.0.0",
                                pr.title
                            )
                            assertEquals(
                                """
                                    The version of ${dependency.project.name} in gradle.properties has been upgraded to 2.0.0.
                                    
                                    Change log of ${dependency.project.name} from 1.0.0 to 2.0.0
                                    
                                    * ISS-21 Some new feature
                                    * ISS-22 Some fixes are needed
                                    * ISS-23 Some nicer UI
                                """.trimIndent(),
                                pr.body
                            )
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                        }

                    }
                }
            }
        }
    }

}