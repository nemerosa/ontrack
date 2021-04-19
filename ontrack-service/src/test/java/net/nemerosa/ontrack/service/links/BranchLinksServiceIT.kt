package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.extension.api.support.TestMetricsExportExtension
import net.nemerosa.ontrack.it.links.AbstractBranchLinksTestSupport
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BranchLinksServiceIT : AbstractBranchLinksTestSupport() {

    @Autowired
    private lateinit var testMetricsExportExtension: TestMetricsExportExtension

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

    @Test
    fun `One layer branch graph`() {
        withLinks {
            build("component", 1) linkTo build("library", 1)

            assertBranchLinks(branch("component"), BranchLinksDirection.USING) {
                assertLinkedTo(branch("library"))
            }

            assertBranchLinks(branch("library"), BranchLinksDirection.USED_BY) {
                assertLinkedTo(branch("component"))
            }
        }
    }

    @Test
    fun `Deep branch graph`() {
        withLinks {

            build("chart", 1) linkTo build("aggregator", 2)

            build("aggregator", 1) linkTo build("project", 1)
            build("aggregator", 2) linkTo build("project", 3)

            build("project", 1) linkTo build("component", 1)
            build("project", 1) linkTo build("component", 1)
            build("project", 2) linkTo build("component", 2)
            build("project", 3) linkTo build("component", 2)
            build("project", 3) linkTo build("other-library", 1)

            build("component", 1) linkTo build("library", 1)
            build("component", 2) linkTo build("library", 3)

            assertBranchLinks(branch("chart"), BranchLinksDirection.USING) {
                assertLinkedTo(branch("aggregator")) {
                    assertLinkedTo(branch("project")) {
                        assertLinkedTo(branch("component")) {
                            assertLinkedTo(branch("library"))
                        }
                        assertLinkedTo(branch("other-library"))
                    }
                }
            }

            assertBranchLinks(branch("library"), BranchLinksDirection.USED_BY) {
                assertLinkedTo(branch("component")) {
                    assertLinkedTo(branch("project")) {
                        assertLinkedTo(branch("aggregator")) {
                            assertLinkedTo(branch("chart"))
                        }
                    }
                }
            }

            assertBranchLinks(branch("other-library"), BranchLinksDirection.USED_BY) {
                assertLinkedTo(branch("project")) {
                    assertLinkedTo(branch("aggregator")) {
                        assertLinkedTo(branch("chart"))
                    }
                }
            }
        }
    }

    @Test
    fun `Build graph with one layer`() {
        withLinks {

            build("component", 1) linkTo build("library", 1)

            assertBuildLinks(build("component", 1), BranchLinksDirection.USING) {
                assertLinkedTo(build("library", 1))
            }
        }
    }

    @Test
    fun `Build graph with one unfilled layer`() {
        withLinks {

            build("component", 1) linkTo build("library", 1)
            build("component", 2)

            assertBuildLinks(build("component", 2), BranchLinksDirection.USING) {
                assertLinkedToNoBuild(branch("library"))
            }

            // Filling the gap
            build("component", 2) linkTo build("library", 1)
            assertBuildLinks(build("component", 2), BranchLinksDirection.USING) {
                assertLinkedTo(build("library", 1))
            }
        }
    }

    @Test
    fun `Build graph with two layers`() {
        withLinks {
            build("project", 1) linkTo build("component", 1)
            build("component", 1) linkTo build("library", 1)
            assertBuildLinks(build("project", 1), BranchLinksDirection.USING) {
                assertLinkedTo(build("component", 1)) {
                    assertLinkedTo(build("library", 1))
                }
            }
        }
    }

    @Test
    fun `Build graph with two layers with progressive fill`() {
        withLinks {
            build("project", 1) linkTo build("component", 1)
            build("component", 1) linkTo build("library", 1)

            val library = build("library", 2)
            assertBuildLinks(library, BranchLinksDirection.USED_BY) {
                assertLinkedToNoBuild(branch("component")) {
                    assertLinkedToNoBuild(branch("project"))
                }
            }

            val component = build("component", 2)
            assertBuildLinks(library, BranchLinksDirection.USED_BY) {
                assertLinkedToNoBuild(branch("component")) {
                    assertLinkedToNoBuild(branch("project"))
                }
            }

            component linkTo library
            assertBuildLinks(library, BranchLinksDirection.USED_BY) {
                assertLinkedTo(component) {
                    assertLinkedToNoBuild(branch("project"))
                }
            }

            val project = build("project", 2)
            assertBuildLinks(library, BranchLinksDirection.USED_BY) {
                assertLinkedTo(component) {
                    assertLinkedToNoBuild(branch("project"))
                }
            }

            project linkTo component
            assertBuildLinks(library, BranchLinksDirection.USED_BY) {
                assertLinkedTo(component) {
                    assertLinkedTo(project)
                }
            }
        }
    }

    @Test
    fun `Branches are processed only once`() {
        withLinks {
            testMetricsExportExtension.with {
                (1..20).forEach {
                    build("chart", it) linkTo build("aggregator", it)
                    build("aggregator", it) linkTo build("project", it)
                    build("project", it) linkTo build("component", it)
                    build("project", it) linkTo build("library-1", it)
                    build("component", it) linkTo build("library", it)
                    build("component", it) linkTo build("library-2", it)
                    build("library", it) linkTo build("library-3", it)
                }

                assertBranchLinks(branch("chart"), BranchLinksDirection.USING) {
                    assertLinkedTo(branch("aggregator")) {
                        assertLinkedTo(branch("project")) {
                            assertLinkedTo(branch("library-1"))
                            assertLinkedTo(branch("component")) {
                                assertLinkedTo(branch("library")) {
                                    assertLinkedTo(branch("library-3"))
                                }
                                assertLinkedTo(branch("library-2"))
                            }
                        }
                    }
                }
            }

            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_graph_branch",
                tags = mapOf(
                    "project" to project("chart").name,
                    "branch" to branch("chart").name
                ),
                fields = mapOf(
                    "stack" to 80.0,
                    "branches" to 8.0
                )
            )
        }
    }

}