package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.extension.api.support.TestMetricsExportExtension
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.it.links.AbstractBranchLinksTestSupport
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@AsAdminTest
class BranchLinksServiceIT : AbstractBranchLinksTestSupport() {

    @Autowired
    private lateinit var testMetricsExportExtension: TestMetricsExportExtension

    /**
     * Given the following dependencies:
     *
     * ```
     * source/main
     *    6
     *          preview -> target/preview/3
     *          default -> target/main/6
     *          default -> other/main/3
     *    5
     *    4
     *          preview -> target/preview/3
     *          default -> target/main/5
     *          default -> other/main/2
     *    3
     *          preview -> target/preview/2
     *          default -> target/main/5
     *          default -> other/main/2
     *    2
     *          preview -> target/preview/2
     *          default -> target/preview/1
     *          default -> other/main/2
     *    1
     * ```
     *
     * we should have the following links:
     *
     * * preview x target/preview
     * * default x target/main
     * * default x other/main
     */
    @Test
    fun `Direct branch downstream links`() {
        asAdmin {
            // Other
            lateinit var otherMain: Branch
            val other = project {
                otherMain = branch("other-main") {
                    build("other-1")
                    build("other-2")
                    build("other-3")
                }
            }
            // Target
            lateinit var targetMain: Branch
            lateinit var targetPreview: Branch
            val target = project {
                targetPreview = branch("target-preview") {
                    build("preview-1")
                    build("preview-2")
                    build("preview-3")
                }
                targetMain = branch("target-main") {
                    build("main-1")
                    build("main-2")
                    build("main-3")
                    build("main-4")
                    build("main-5")
                    build("main-6")
                }
            }
            // Source
            project {
                branch {
                    build("1") {
                        // No dependencies
                    }
                    build("2") {
                        linkTo(target, "preview-2", "preview")
                        linkTo(target, "preview-1", "")
                        linkTo(other, "other-2", "")
                    }
                    build("3") {
                        linkTo(target, "preview-2", "preview")
                        linkTo(target, "main-5", "")
                        linkTo(other, "other-2", "")
                    }
                    build("4") {
                        linkTo(target, "preview-3", "preview")
                        linkTo(target, "main-5", "")
                        linkTo(other, "other-2", "")
                    }
                    build("5") {
                        // No dependencies
                    }
                    build("6") {
                        linkTo(target, "preview-3", "preview")
                        linkTo(target, "main-6", "")
                        linkTo(other, "other-3", "")
                    }

                    // Gets the downstream branch links
                    val links = branchLinksService.getDownstreamDependencies(this, 5)

                    assertEquals(
                        listOf(
                            "preview" to targetPreview,
                            "" to targetMain,
                            "" to otherMain,
                        ).sortedWith(
                            compareBy(
                                { it.second.project.name },
                                { it.first },
                            )
                        ).map { (qualifier, branch) ->
                              qualifier to branch.name
                        },
                        links.map {
                            it.qualifier to it.branch.name
                        }
                    )
                }
            }
        }
    }

    @Test
    fun `No build links makes for one single node for the abstract graph`() {
        val build = build("build")
        asUserWithView(build) {
            val node = branchLinksService.getBranchLinks(build.branch, BranchLinksDirection.USING)
            assertEquals(build.branch.id, node.branch.id)
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
                    "stack" to 8.0
                )
            )
        }
    }

    @Test
    fun `Branches are processed independently on each graph path`() {

        /**
         * Building the following graph:
         *
         * project/main/1 ==> component/main/1 ==> library/release/1
         * project/main/2 ==> component/release/1 ==> library/release/1
         */

        val project = project()
        val projectMain = project.branch("main")
        val projectMain1 = projectMain.build("1")
        val projectMain2 = projectMain.build("2")

        val component = project()
        val componentMain = component.branch("main")
        val componentMain1 = componentMain.build("1")
        val componentRelease = component.branch("release")
        val componentRelease1 = componentRelease.build("1")

        val library = project()
        val libraryRelease = library.branch("release")
        val libraryRelease1 = libraryRelease.build("1")

        asAdmin {
            projectMain1.linkTo(componentMain1)
            projectMain2.linkTo(componentRelease1)

            componentMain1.linkTo(libraryRelease1)
            componentRelease1.linkTo(libraryRelease1)
        }

        /**
         * Computing the graph for project/main
         */

        withLinks {
            assertBranchLinks(projectMain, BranchLinksDirection.USING) {
                /**
                 * We expect the following dependencies:
                 *
                 * project/main --- component/main --- library/release
                 *              \--- component/release --- library/release
                 */
                assertLinkedTo(componentMain) {
                    assertLinkedTo(libraryRelease)
                }
                assertLinkedTo(componentRelease) {
                    assertLinkedTo(libraryRelease)
                }
            }
        }
    }

    @Test
    fun `Branches are the unit of processing`() {
        withLinks {

            build("component", 1) linkTo build("library", 1)

            build("component", 2) linkTo build("library", 2)
            build("project", 2) linkTo build("component", 2)

            assertBranchLinks(branch("library"), BranchLinksDirection.USED_BY) {
                assertLinkedTo(branch("component")) {
                    assertLinkedTo(branch("project"))
                }
            }
        }
    }

    @Test
    fun `Keeping all branches when building the graph`() {
        withLinks {

            val component = project("component")
            val componentFeature = component.branch(name = "feature")
            val componentFeature1 = componentFeature.build("1")
            val componentMain = component.branch(name = "main")
            val componentMain1 = componentMain.build("21")
            val componentMain2 = componentMain.build("22")

            val projectA = "projectA"
            build(projectA, 1) linkTo componentMain1
            build(projectA, 2) linkTo componentFeature1
            build(projectA, 3) linkTo componentFeature1
            build(projectA, 4) linkTo componentMain2

            assertBranchLinks(branch(projectA), BranchLinksDirection.USING) {
                assertLinkedTo(componentMain)
                assertLinkedTo(componentFeature)
            }

            val projectB = "projectB"
            build(projectB, 1) linkTo componentMain1
            build(projectB, 2) linkTo componentFeature1
            build(projectB, 3) linkTo componentFeature1

            assertBranchLinks(branch(projectB), BranchLinksDirection.USING) {
                assertLinkedTo(componentMain)
                assertLinkedTo(componentFeature)
            }

        }
    }

    @Test
    fun `Discarding old branches when building the graph`() {
        withBranchLinkSettings(history = 2) {
            withLinks {

                val component = project("component")
                val componentFeature = component.branch(name = "feature")
                val componentFeature1 = componentFeature.build("1")
                val componentMain = component.branch(name = "main")
                val componentMain1 = componentMain.build("21")
                val componentMain2 = componentMain.build("22")

                val projectA = "projectA"
                build(projectA, 1) linkTo componentFeature1
                build(projectA, 2) linkTo componentFeature1
                build(projectA, 3) linkTo componentMain1
                build(projectA, 4) linkTo componentMain2

                assertBranchLinks(branch(projectA), BranchLinksDirection.USING) {
                    assertLinkedTo(componentMain)
                    assertNotLinkedTo(componentFeature) // Feature branch is gone from history
                }

                val projectB = "projectB"
                build(projectB, 1) linkTo componentFeature1
                build(projectB, 2) linkTo componentFeature1
                build(projectB, 3) linkTo componentMain1

                assertBranchLinks(branch(projectB), BranchLinksDirection.USING) {
                    assertLinkedTo(componentMain)
                    assertLinkedTo(componentFeature) // Feature branch is still part of the history
                }

            }
        }
    }

}