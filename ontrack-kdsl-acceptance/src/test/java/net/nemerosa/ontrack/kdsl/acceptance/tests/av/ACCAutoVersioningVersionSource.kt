package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.general.setMetaInfoProperty
import org.junit.jupiter.api.Test

class ACCAutoVersioningVersionSource : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Using meta information to get the version`() {
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
                                    targetPropertyType = "properties",
                                    versionSource = "metaInfo/customVersion",
                                )
                            )
                        )

                        dependency.apply {
                            build {
                                setMetaInfoProperty("customVersion", "2.0.0")
                                promote("IRON")

                                waitForAutoVersioningCompletion()

                                assertThatMockScmRepository {
                                    hasPR(
                                        from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-*",
                                        to = "main"
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
    }

}