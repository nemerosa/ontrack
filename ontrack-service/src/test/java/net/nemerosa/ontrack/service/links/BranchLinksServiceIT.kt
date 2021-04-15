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
        withBranchLinkSettings(depth = 1) {
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

    @Test
    fun `History must be taken into account`() {
        val a = build()
        val b = build()
        val c = build()
        project {
            val branch = branch {
                build {
                    linkTo(a)
                }
                build {
                    linkTo(b)
                }
                build {
                    linkTo(c)
                }
            }
            val node = branchLinksService.getBranchLinks(branch, BranchLinksDirection.USING)
            assertEquals(
                listOf(a, b, c).map { it.branch.name }.toSet(),
                node.edges.map { it.linkedTo.branch.name }.toSet()
            )
        }
    }

    @Test
    fun `History limit`() {
        val a = build()
        val b = build()
        val c = build()
        project {
            val branch = branch {
                build {
                    linkTo(a)
                }
                build {
                    linkTo(b)
                }
                build {
                    linkTo(c)
                }
            }
            withBranchLinkSettings(history = 2) {
                val node = branchLinksService.getBranchLinks(branch, BranchLinksDirection.USING)
                assertEquals(
                    listOf(b, c).map { it.branch.name }.toSet(), // History = 2, 3rd build's not taken
                    node.edges.map { it.linkedTo.branch.name }.toSet()
                )
            }
        }
    }

    @Test
    fun `Max links per level`() {
        val dependencies = (1..10).map { build("$it") }
        project {
            val branch = branch {
                build {
                    dependencies.forEach { linkTo(it) }
                }
            }
            withBranchLinkSettings(maxLinksPerLevel = 5) {
                val node = branchLinksService.getBranchLinks(branch, BranchLinksDirection.USING)
                assertEquals(
                    dependencies.takeLast(5).map { it.branch.name }.toSet(), // Taking only the five first links
                    node.edges.map { it.linkedTo.branch.name }.toSet()
                )
            }
        }
    }

    private fun build(name: String = "build") = project<Build> {
        branch<Build> {
            build(name)
        }
    }

    private fun withBranchLinkSettings(
        depth: Int = BranchLinksSettings.DEFAULT_DEPTH,
        history: Int = BranchLinksSettings.DEFAULT_HISTORY,
        maxLinksPerLevel: Int = BranchLinksSettings.DEFAULT_MAX_LINKS_PER_LEVEL,
        code: () -> Unit
    ) {
        asAdmin {
            withSettings<BranchLinksSettings> {
                settingsManagerService.saveSettings(
                    BranchLinksSettings(
                        depth = depth,
                        history = history,
                        maxLinksPerLevel = maxLinksPerLevel
                    )
                )
                code()
            }
        }
    }

}