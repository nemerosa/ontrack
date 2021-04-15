package net.nemerosa.ontrack.service.links

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
    private val structureService: StructureService
) : BranchLinksService {

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

    override fun getBuildLinks(branch: Branch, direction: BranchLinksDirection): BranchLinksNode {
        TODO("Not yet implemented")
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
                BranchLinksEdge(
                    direction = direction,
                    linkedTo = graphToNode(child, direction),
                    decorations = emptyList()
                )
            }
        )

}