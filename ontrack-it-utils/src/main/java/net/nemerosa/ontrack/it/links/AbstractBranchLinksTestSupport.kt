package net.nemerosa.ontrack.it.links

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.links.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

abstract class AbstractBranchLinksTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var branchLinksService: BranchLinksService

    protected fun withLinks(
        code: WithLinksContext.() -> Unit
    ) {
        asAdmin {
            WithLinksContext().code()
        }
    }

    protected inner class WithLinksContext {

        val project = mutableMapOf<String, Project>()
        val branches = mutableMapOf<String, Branch>()
        val builds = mutableMapOf<Pair<String, Int>, Build>()

        fun project(id: String): Project =
            project.getOrPut(id) {
                project(NameDescription.nd(id + uid("x"), ""))
            }

        fun branch(id: String): Branch =
            branches.getOrPut(id) {
                project(id).branch("main")
            }

        fun build(id: String, no: Int): Build =
            builds.getOrPut(id to no) {
                branch(id).build("$id-$no")
            }

        fun assertBranchLinks(branch: Branch, direction: BranchLinksDirection, code: BranchNodeTestContext.() -> Unit) {
            val node = branchLinksService.getBranchLinks(branch, direction)
            assertEquals(branch.id, node.branch.id, "Node on the same project")
            BranchNodeTestContext(node).code()
        }

        fun assertBuildLinks(build: Build, direction: BranchLinksDirection, code: BuildNodeTestContext.() -> Unit) {
            val node = branchLinksService.getBuildLinks(build, direction)
            assertEquals(build.branch.id, node.branch.id, "Node on the same branch")
            assertEquals(build.id, node.build?.id, "Node on the same build")
            BuildNodeTestContext(node).code()
        }

    }

    protected class BranchNodeTestContext(
        val node: BranchLinksNode
    ) {
        fun assertLinkedTo(target: Branch, code: BranchNodeTestContext.() -> Unit = {}) {
            val edge = node.edges.find { it.linkedTo.branch.id == target.id }
            assertNotNull(edge,
                "Cannot find any link between ${node.branch.entityDisplayName} and ${target.entityDisplayName}") {
                BranchNodeTestContext(it.linkedTo).code()
            }
        }
        fun assertNotLinkedTo(target: Branch) {
            val edge = node.edges.find { it.linkedTo.branch.id == target.id }
            assertNull(edge,
                "Found a link between ${node.branch.entityDisplayName} to ${target.entityDisplayName} when none was expected"
            )
        }
    }

    protected class EdgeTestContext(
        val edge: BranchLinksEdge
    ) {
        fun assertDecoration(id: String, code: BranchLinksDecoration.() -> Unit = {}) {
            val decoration = edge.decorations.find { it.id == id }
            assertNotNull(decoration, "Decoration $id is present") {
                it.code()
            }
        }
    }

    protected class BuildNodeTestContext(
        val node: BranchLinksNode
    ) {
        fun assertEdge(target: Build, code: EdgeTestContext.() -> Unit = {}) {
            val edge = node.edges.find { it.linkedTo.branch.id == target.branch.id }
            assertNotNull(edge,
                "Cannot find any link between ${node.branch.entityDisplayName} and ${target.entityDisplayName}") {
                // Checks the build
                assertEquals(target.id,
                    it.linkedTo.build?.id,
                    "Expected ${target.entityDisplayName} under node ${target.branch.entityDisplayName}")
                // Going on
                EdgeTestContext(it).code()
            }
        }

        fun assertLinkedTo(target: Build, code: BuildNodeTestContext.() -> Unit = {}) {
            val edge = node.edges.find { it.linkedTo.branch.id == target.branch.id }
            assertNotNull(edge,
                "Cannot find any link between ${node.branch.entityDisplayName} and ${target.branch.entityDisplayName}") {
                // Checks the build
                assertEquals(target.id,
                    it.linkedTo.build?.id,
                    "Expected ${target.entityDisplayName} under node ${target.branch.entityDisplayName}")
                // Going on
                BuildNodeTestContext(it.linkedTo).code()
            }
        }

        fun assertLinkedToNoBuild(target: Branch, code: BuildNodeTestContext.() -> Unit = {}) {
            val edge = node.edges.find { it.linkedTo.branch.id == target.id }
            assertNotNull(edge,
                "Cannot find any link between ${node.branch.entityDisplayName} and ${target.entityDisplayName}") {
                assertNull(it.linkedTo.build, "Node for ${target.entityDisplayName} has no build")
                // Going on
                BuildNodeTestContext(it.linkedTo).code()
            }
        }
    }

    protected fun build(name: String = "build") = project<Build> {
        branch<Build> {
            build(name)
        }
    }

    protected fun withBranchLinkSettings(
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