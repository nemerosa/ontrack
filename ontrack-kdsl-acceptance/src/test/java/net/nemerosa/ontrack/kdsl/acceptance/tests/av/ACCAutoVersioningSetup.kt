package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHub
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoApprovalMode
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.getAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@TestOnGitHub
class ACCAutoVersioningSetup : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning configuration with auto approval mode set to SCM`() {
        withTestGitHubRepository {
            withAutoVersioning {
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
                                    autoApprovalMode = AutoApprovalMode.SCM,
                                )
                            )
                        )

                        val configs: List<AutoVersioningSourceConfig> = getAutoVersioningConfig()

                        assertEquals(
                            AutoApprovalMode.SCM,
                            configs.find { it.sourceBranch == dependency.name }?.autoApprovalMode
                        )

                    }
                }
            }
        }
    }

}