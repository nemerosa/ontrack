package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import java.util.*

class ACCAutoVersioningSpecialFiles : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning in a very big file`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("Jenkinsfile") {
                    (listOf("@Library(\"my-library@0.1.0\") _") + (1..4000).map {
                        UUID.randomUUID().toString()
                    }).joinToString("\n")
                }
                val library = branchWithPromotion(promotion = "GOLD")
                project {
                    branch {
                        configuredForMockRepository()
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

                        assertThatMockScmRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${library.project.name}-0.1.1-*",
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