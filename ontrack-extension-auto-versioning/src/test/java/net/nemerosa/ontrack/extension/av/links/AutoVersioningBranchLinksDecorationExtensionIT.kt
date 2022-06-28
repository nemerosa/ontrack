package net.nemerosa.ontrack.extension.av.links

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AutoVersioningBranchLinksDecorationExtensionIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Test
    fun `Decoration between two nodes when there is an auto versioning link (direction using)`() {
        withLinks {
            build("parent", 1) linkTo build("component", 1)
            branch("parent").apply {
                setAutoVersioning {
                    autoVersioningConfig {
                        sourceProject(project("component").name)
                        sourceBranch(branch("component").name)
                        sourcePromotion("IRON")
                    }
                }
                createOrder(sourceProject = project("component").name, targetVersion = "2.0.0").apply {
                    autoVersioningAuditService.onQueuing(this, "routing")
                    autoVersioningAuditService.onReceived(this, "queue")
                }

                assertBuildLinks(build("parent", 1), BranchLinksDirection.USING) {
                    assertEdge(build("component", 1)) {
                        assertDecoration("auto_versioning") {
                            assertEquals(AutoVersioningAuditState.RECEIVED.name, text)
                            assertEquals(
                                "Auto version of <b>${project("parent").name}/${branch("parent").name}</b> to <b>${
                                    project("component").name
                                }</b> version <b>2.0.0</b>", description
                            )
                            assertEquals("received", icon)
                            assertEquals(
                                "urn:test:#:extension/collibra/auto-versioning-audit/branch/${project("parent").name}/${
                                    branch(
                                        "parent"
                                    ).name
                                }", url
                            )
                        }
                    }
                }
            }

        }
    }

    @Test
    fun `Decoration between two nodes when there is an auto versioning link (direction used_by)`() {
        withLinks {
            build("parent", 1) linkTo build("component", 1)
            branch("parent").apply {
                setAutoVersioning {
                    autoVersioningConfig {
                        sourceProject(project("component").name)
                        sourceBranch(branch("component").name)
                        sourcePromotion("IRON")
                    }
                }
                createOrder(sourceProject = project("component").name, targetVersion = "2.0.0").apply {
                    autoVersioningAuditService.onQueuing(this, "routing")
                    autoVersioningAuditService.onReceived(this, "queue")
                }

                assertBuildLinks(build("component", 1), BranchLinksDirection.USED_BY) {
                    assertEdge(build("parent", 1)) {
                        assertDecoration("auto_versioning") {
                            assertEquals(AutoVersioningAuditState.RECEIVED.name, text)
                            assertEquals(
                                "Auto version of <b>${project("parent").name}/${branch("parent").name}</b> to <b>${
                                    project("component").name
                                }</b> version <b>2.0.0</b>", description
                            )
                            assertEquals("received", icon)
                            assertEquals(
                                "urn:test:#:extension/collibra/auto-versioning-audit/source/${project("component").name}",
                                url
                            )
                        }
                    }
                }
            }

        }
    }

    @Test
    fun `No decoration between two nodes when there is none being configured`() {
        withLinks {
            build("parent", 1) linkTo build("component", 1)
            branch("parent").apply {
                assertBuildLinks(build("parent", 1), BranchLinksDirection.USING) {
                    assertEdge(build("component", 1)) {
                        assertNoDecoration("auto_versioning")
                    }
                }
            }

        }
    }

    private fun EdgeTestContext.assertNoDecoration(id: String) {
        val decoration = edge.decorations.find { it.id == id }
        assertNull(decoration, "Decoration $id is not supposed to be present")
    }

}