package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLTypeBuildAutoVersioningIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Test
    fun `Getting the last audit entry for a dependency`() {
        asAdmin {
            val dependencyBuild = project<Build> {
                branch<Build>("release/1.1") {
                    build("1")
                }
            }
            val parentBuild = project<Build> {
                branch<Build> {
                    autoVersioningConfigurationService.setupAutoVersioning(
                        this,
                        AutoVersioningConfig(
                            configurations = listOf(
                                AutoVersioningTestFixtures.sourceConfig(
                                    sourceProject = dependencyBuild.project.name,
                                    sourceBranch = "release/1.1"
                                )
                            )
                        )
                    )
                    build("parent-1")
                }
            }

            // Creating an audit entry
            parentBuild.branch.createOrder(
                sourceProject = dependencyBuild.project.name,
                targetVersion = "1.1.1",
            ).apply {
                autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                autoVersioningAuditService.onReceived(this, "queue")
            }

            run(
                """
                {
                    build(id: ${dependencyBuild.id}) {
                        autoVersioning(branchId: ${parentBuild.branch.id}) {
                            status {
                                mostRecentState {
                                    state
                                }
                            }
                        }
                    }
                }
            """
            ) { data ->
                assertJsonNotNull(
                    data.path("build").path("autoVersioning"),
                    "AV config found"
                ) {
                    assertEquals(
                        "RECEIVED",
                        path("status").path("mostRecentState").path("state").asText()
                    )
                }
            }
        }
    }

}