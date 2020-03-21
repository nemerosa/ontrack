package net.nemerosa.ontrack.extension.scm.catalog.mock

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

/**
 * Configurable [SCMCatalogProvider] used for tests.
 */
@Component
class MockSCMCatalogProvider : SCMCatalogProvider {

    /**
     * Entry key --> project id
     */
    private val mappings = mutableMapOf<String, Int>()

    /**
     * List of entries
     */
    private val storedEntries = mutableListOf<SCMCatalogSource>()

    override val id: String = "mocking"

    override val entries: List<SCMCatalogSource> get() = storedEntries.toList()

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean = mappings[entry.key] == project.id()

    /**
     * Clears all data
     */
    fun clear() {
        mappings.clear()
        storedEntries.clear()
    }

    /**
     * Stores an entry
     */
    fun storeEntry(entry: SCMCatalogEntry) {
        storedEntries += SCMCatalogSource(
                config = entry.config,
                repository = entry.repository,
                repositoryPage = entry.repositoryPage
        )
    }

    /**
     * Links a project to an entry
     */
    fun linkEntry(entry: SCMCatalogEntry, project: Project) {
        mappings[entry.key] = project.id()
    }
}