package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.extension.api.BranchLinksDecorationExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.links.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class BranchLinksServiceImpl(
    private val cachedSettingsService: CachedSettingsService,
    private val buildFilterService: BuildFilterService,
    private val structureService: StructureService,
    private val extensionManager: ExtensionManager
) : BranchLinksService {

    private val providers: Collection<BranchLinksDecorationExtension> by lazy {
        extensionManager.getExtensions(BranchLinksDecorationExtension::class.java)
    }

    override fun getBranchLinks(branch: Branch, direction: BranchLinksDirection): BranchLinksNode {
        // Settings
        val settings: BranchLinksSettings = cachedSettingsService.getCachedSettings(BranchLinksSettings::class.java)
        // Graph in progress
        val graph = Node(branch)
        // Index of nodes per branch
        val index = mutableMapOf<ID, Node>()
        index[branch.id] = graph
        // Processing stack
        val stack = ArrayDeque<Item>()
        // Gets the N builds of the branch
        fillStackFromBranch(0, branch, settings.history, stack)
        // Starts processing the stack
        while (stack.isNotEmpty()) {
            // Gets the current item
            val item = stack.pop()
            // Gets the corresponding node in the graph
            val node = index[item.build.branch.id]
                ?: error("Cannot find indexed node for ${item.build.branch.entityDisplayName}")
            // Gets the following builds using the current direction
            if (item.depth < settings.depth) {
                val nextBuilds = getNextBuilds(item.build, direction, settings.maxLinksPerLevel)
                // For every next build...
                nextBuilds.forEach { nextBuild ->
                    val nextBranch: Branch = nextBuild.branch
                    // Make sure we have a node for its branch
                    val nextNode = index.getOrPut(nextBranch.id) {
                        Node(nextBranch)
                    }
                    // Links this node to the current one
                    node.branches += nextNode
                    // Takes the branch N first builds to fill the stack
                    fillStackFromBranch(item.depth + 1, nextBranch, settings.history, stack)
                }
            }
        }
        // Converts the graph in progress to a node
        return graphToNode(graph, direction)
    }

    override fun getBuildLinks(build: Build, direction: BranchLinksDirection): BranchLinksNode {
        // Gets the model for the branch
        val branchGraph = getBranchLinks(build.branch, direction)
        // Visits the whole graph and replace the node & edges as we go
        return populate(branchGraph, build, direction)
    }

    private fun populate(node: BranchLinksNode, build: Build, direction: BranchLinksDirection): BranchLinksNode {
        // Recomputes the edges
        val edges = node.edges.map { edge ->
            populate(edge, BranchLinksNode(node.branch, build, node.edges), direction)
        }
        // OK
        return BranchLinksNode(
            node.branch,
            build,
            edges
        )
    }

    private fun populate(edge: BranchLinksEdge, source: BranchLinksNode, direction: BranchLinksDirection): BranchLinksEdge {
        // Gets the target build if any
        val target = source.build?.let { getEdgeBuild(it, edge.linkedTo.branch, direction) }
        // If no build, we return the edge as it is
        return if (target == null) {
            edge
        } else {
            val newLinkedNode = populate(edge.linkedTo, target, direction)
            val decorations = providers.mapNotNull { provider ->
                provider.getDecoration(source, newLinkedNode, direction)
            }
            BranchLinksEdge(
                direction = direction,
                linkedTo = newLinkedNode,
                decorations = decorations
            )
        }
    }

    private fun fillStackFromBranch(depth: Int, branch: Branch, history: Int, stack: Deque<Item>) {
        // Gets the N builds of the branch
        val builds = getBuilds(branch, history)
        // Puts these builds onto the stack
        builds.forEach { build ->
            stack.push(
                Item(depth, build)
            )
        }
    }

    private fun getBuilds(branch: Branch, history: Int): List<Build> =
        buildFilterService.standardFilterProviderData(history).build().filterBranchBuilds(branch)

    private fun getNextBuilds(build: Build, direction: BranchLinksDirection, maxLinksPerLevel: Int): List<Build> =
        when (direction) {
            BranchLinksDirection.USING ->
                structureService.getBuildsUsedBy(build, 0, maxLinksPerLevel).pageItems
            BranchLinksDirection.USED_BY ->
                structureService.getBuildsUsing(build, 0, maxLinksPerLevel).pageItems
        }

    private fun getEdgeBuild(build: Build, target: Branch, direction: BranchLinksDirection): Build? =
        when (direction) {
            BranchLinksDirection.USING ->
                structureService.getBuildsUsedBy(build, 0, 1) {
                    it.branch.id == target.id
                }.pageItems.firstOrNull()
            BranchLinksDirection.USED_BY ->
                structureService.getBuildsUsing(build, 0, 1) {
                    it.branch.id == target.id
                }.pageItems.firstOrNull()
        }

    private class Item(
        val depth: Int,
        val build: Build
    )

    private class Node(
        val branch: Branch,
        val branches: MutableList<Node> = mutableListOf()
    )

    private fun graphToNode(node: Node, direction: BranchLinksDirection): BranchLinksNode =
        BranchLinksNode(
            branch = node.branch,
            build = null,
            edges = node.branches.map { child ->
                graphToNode(child, direction).run {
                    val decorations = providers.mapNotNull { provider ->
                        provider.getDecoration(BranchLinksNode(node.branch, null, emptyList()), this, direction)
                    }
                    BranchLinksEdge(
                        direction = direction,
                        linkedTo = this,
                        decorations = decorations
                    )
                }
            }
        )

}