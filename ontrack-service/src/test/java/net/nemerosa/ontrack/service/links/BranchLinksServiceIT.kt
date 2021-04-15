package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksService
import net.nemerosa.ontrack.model.links.BranchLinksSettings
import net.nemerosa.ontrack.model.structure.Build
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BranchLinksServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var branchLinksService: BranchLinksService

    @Test
    fun `No build links makes for one single node for the abstract graph`() {
        val build = build("build")
        asUserWithView(build) {
            val node = branchLinksService.getBranchLinks(build.branch, BranchLinksDirection.USING)
            assertSame(build.branch, node.branch)
            assertNull(node.build, "No build")
            assertTrue(node.edges.isEmpty(), "No edge")
        }
    }

    @Test
    fun `Abstract graph for regular links in the 'using' direction`() {
        val library = build("library")
        val component = build("component")
        val project = build("project")
        asAdmin {
            project.linkTo(component)
            component.linkTo(library)
        }

        asUserWithView(project, component, library) {
            val projectNode = branchLinksService.getBranchLinks(project.branch, BranchLinksDirection.USING)
            assertEquals(project.branch.id, projectNode.branch.id)
            assertNull(projectNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(1, projectNode.edges.size)

            val projectEdge = projectNode.edges.first()
            assertEquals(BranchLinksDirection.USING, projectEdge.direction)

            val componentNode = projectEdge.linkedTo
            assertEquals(component.branch.id, componentNode.branch.id)
            assertNull(componentNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(1, componentNode.edges.size)

            val componentEdge = componentNode.edges.first()
            assertEquals(BranchLinksDirection.USING, componentEdge.direction)

            val libraryNode = componentEdge.linkedTo
            assertEquals(library.branch.id, libraryNode.branch.id)
            assertNull(libraryNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(0, libraryNode.edges.size)
        }
    }

    @Test
    fun `Abstract graph for regular links in the 'usedBy' direction`() {
        val library = build("library")
        val component = build("component")
        val project = build("project")
        asAdmin {
            project.linkTo(component)
            component.linkTo(library)
        }

        asUserWithView(project, component, library) {
            val libraryNode = branchLinksService.getBranchLinks(library.branch, BranchLinksDirection.USED_BY)
            assertEquals(library.branch.id, libraryNode.branch.id)
            assertNull(libraryNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(1, libraryNode.edges.size)

            val componentEdge = libraryNode.edges.first()
            assertEquals(BranchLinksDirection.USED_BY, componentEdge.direction)

            val componentNode = componentEdge.linkedTo
            assertEquals(component.branch.id, componentNode.branch.id)
            assertNull(componentNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(1, componentNode.edges.size)

            val projectEdge = componentNode.edges.first()
            assertEquals(BranchLinksDirection.USED_BY, projectEdge.direction)

            val projectNode = projectEdge.linkedTo
            assertEquals(project.branch.id, projectNode.branch.id)
            assertNull(projectNode.build, "Abstract graph nodes don't contain builds")
            assertEquals(0, projectNode.edges.size)
        }
    }

    @Test
    fun `Depth limit`() {
        asAdmin {
            withSettings<BranchLinksSettings> {
                settingsManagerService.saveSettings(
                    BranchLinksSettings(
                        depth = 1,
                        history = BranchLinksSettings.DEFAULT_HISTORY,
                        maxLinksPerLevel = BranchLinksSettings.DEFAULT_MAX_LINKS_PER_LEVEL
                    )
                )

                val library = build("library")
                val component = build("component")
                val project = build("project")
                asAdmin {
                    project.linkTo(component)
                    component.linkTo(library)
                }

                val libraryNode = branchLinksService.getBranchLinks(library.branch, BranchLinksDirection.USED_BY)
                assertEquals(library.branch.id, libraryNode.branch.id)
                assertNull(libraryNode.build, "Abstract graph nodes don't contain builds")
                assertEquals(1, libraryNode.edges.size)

                val componentEdge = libraryNode.edges.first()
                assertEquals(BranchLinksDirection.USED_BY, componentEdge.direction)

                val componentNode = componentEdge.linkedTo
                assertEquals(component.branch.id, componentNode.branch.id)
                assertNull(componentNode.build, "Abstract graph nodes don't contain builds")
                assertEquals(0, componentNode.edges.size) // We don't go further because depth = 1
            }
        }
    }

    private fun build(name: String) = project<Build> {
        branch<Build> {
            build(name)
        }
    }

}