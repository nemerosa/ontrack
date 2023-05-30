package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.extension.*
import org.jgrapht.alg.cycle.CycleDetector
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Service

@Service
class ExtensionManagerImpl(
        private val applicationContext: ApplicationContext
) : ExtensionManager {

    private val logger: Logger = LoggerFactory.getLogger(ExtensionManager::class.java)

    private val extensions: Collection<Extension> by lazy {
        logger.info("[extensions] Loading the extensions")
        val extensions = applicationContext.getBeansOfType(Extension::class.java).values
        val extensionFeatures: Collection<ExtensionFeature> = applicationContext.getBeansOfType(
                ExtensionFeature::class.java
        ).values
        logger.info("[extensions] Number of loaded extension features: {}", extensionFeatures.size)
        logger.info("[extensions] Number of loaded extensions: {}", extensions.size)
        logger.info("[extensions] Extension features:")
        for (feature in extensionFeatures) {
            logger.info("[extensions] * {} [{}]", feature.name, feature.id)
        }
        // Detects cycles (by forcing the load of the extension list)
        extensionList
        // OK
        extensions
    }

    override fun <T : Extension> getExtensions(extensionType: Class<T>): Collection<T> {
        val collection = extensions.filter { extensionType.isInstance(it) }
        @Suppress("UNCHECKED_CAST")
        return collection as Collection<T>
    }

    override val extensionList: ExtensionList by lazy {
        // Gets the list of all extensions
        val extensionFeatures = applicationContext.getBeansOfType(
                ExtensionFeature::class.java
        ).values
                .map(ExtensionFeature::featureDescription)
                .sortedBy { it.name }

        // Computing the dependencies
        val g = DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge::class.java
        )

        // Adds the extensions as vertexes
        extensionFeatures.forEach { feature ->
            g.addVertex(feature.id)
        }

        // Adds the dependencies as edges
        extensionFeatures.forEach { feature ->
            feature.options.dependencies.forEach { dependency ->
                g.addEdge(dependency, feature.id)
            }
        }

        // Cycle detection
        val cycleDetector = CycleDetector(g)

        // If there are cycles
        if (cycleDetector.detectCycles()) {
            val cycles = mutableListOf<List<String>>()
            val cycleVertices: MutableSet<String> = cycleDetector.findCycles()
            while (cycleVertices.isNotEmpty()) {
                val subCycleList = mutableListOf<String>()
                // Get a vertex involved in a cycle.
                val iterator: Iterator<String> = cycleVertices.iterator()
                val cycle = iterator.next()
                // Get all vertices involved with this vertex.
                val subCycle = cycleDetector.findCyclesContainingVertex(cycle)
                for (sub in subCycle) {
                    subCycleList.add(sub)
                    // Remove vertex so that this cycle is not encountered again.
                    cycleVertices.remove(sub)
                }
                // Adds to the list of cycles
                cycles.add(subCycleList)
            }
            throw ExtensionCycleException(cycles)
        } else {

            // Topological order to collect the leaf dependencies first
            // See https://en.wikipedia.org/wiki/Dependency_graph
            val orderIterator = TopologicalOrderIterator(g)

            // Order to extensions to load
            val order = mutableListOf<String>()
            while (orderIterator.hasNext()) {
                val extension = orderIterator.next()
                order.add(extension)
            }

            // Indexation of extensions per ID
            val index = extensionFeatures.associateBy { it.id }

            // OK
            ExtensionList(
                    order
                            .map { index.getValue(it) }
                            .map { convert(it) }
            )
        }
    }

    /**
     * For a development environment, distinct JS client files are still accepted.
     * In any other profile, they must be set to one file each only.
     */
    private fun convert(extensionFeatureDescription: ExtensionFeatureDescription): ExtensionFeatureDescription {
        val devProfile = applicationContext.environment.acceptsProfiles(
                Profiles.of(RunProfile.DEV)
        )
        return if (devProfile) {
            extensionFeatureDescription
        } else {
            extensionFeatureDescription.toProduction()
        }
    }
}