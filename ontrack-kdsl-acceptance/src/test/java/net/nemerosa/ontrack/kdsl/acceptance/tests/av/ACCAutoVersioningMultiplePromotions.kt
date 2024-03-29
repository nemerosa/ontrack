package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

class ACCAutoVersioningMultiplePromotions : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning from multiple promotions on the same source project`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {

                val app = project {
                    branch("main") {
                        promotion("SILVER")
                        promotion("GOLD")
                        build("1.2.2") {
                            this
                        }
                    }
                }

                repositoryFile("gradle.properties") {
                    """
                        dev-version = 1.2.1
                        prod-version = 1.2.1
                    """.trimIndent()
                }

                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = app.branch.project.name,
                                    sourceBranch = "main",
                                    sourcePromotion = "SILVER",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dev-version",
                                    upgradeBranchPattern = "feature/auto-upgrade-<project>-<version>-silver",
                                ),
                                AutoVersioningSourceConfig(
                                    sourceProject = app.branch.project.name,
                                    sourceBranch = "main",
                                    sourcePromotion = "GOLD",
                                    targetPath = "gradle.properties",
                                    targetProperty = "prod-version",
                                    upgradeBranchPattern = "feature/auto-upgrade-<project>-<version>-gold",
                                ),
                            )
                        )

                        // Promoting the app to SILVER will change the dep-version

                        app.promote("SILVER")
                        waitForAutoVersioningCompletion()
                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                """
                                    dev-version = 1.2.2
                                    prod-version = 1.2.1
                                """.trimIndent()
                            }
                        }

                        // Promoting the app to GOLD will change the prod-version

                        app.promote("GOLD")
                        waitForAutoVersioningCompletion()
                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                """
                                    dev-version = 1.2.2
                                    prod-version = 1.2.2
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

}