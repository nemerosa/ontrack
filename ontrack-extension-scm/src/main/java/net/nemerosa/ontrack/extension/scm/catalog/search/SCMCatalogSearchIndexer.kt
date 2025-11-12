package net.nemerosa.ontrack.extension.scm.catalog.search

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class SCMCatalogSearchIndexer(
    extensionFeature: SCMExtensionFeature,
    private val scmCatalog: SCMCatalog,
    private val catalogLinkService: CatalogLinkService,
    scmExtensionConfigProperties: SCMExtensionConfigProperties,
) : SearchIndexer<SCMCatalogSearchItem> {

    override val indexerName: String = "SCM Catalog"

    override val indexName: String = "scm-catalog"

    override val enabled: Boolean = scmExtensionConfigProperties.catalog.enabled

    override val searchResultType = SearchResultType(
        feature = extensionFeature.featureDescription,
        id = "scm-catalog",
        name = "SCM Catalog",
        description = "Indexed SCM repository, which might be associated or not with an Ontrack project",
        order = SearchResultType.ORDER_PROPERTIES + 100,
    )

    override fun indexAll(processor: (SCMCatalogSearchItem) -> Unit) {
        scmCatalog.catalogEntries.forEach { entry ->
            val item = SCMCatalogSearchItem(entry)
            processor(item)
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Gets the associated entry
        val entry = scmCatalog.getCatalogEntry(id)
        // Returns the result
        return entry?.let {
            // Gets the associated project
            val project = catalogLinkService.getLinkedProject(entry)
            // Title and description
            val title: String
            val description: String
            if (project != null) {
                title = "${project.name} (${entry.repository})"
                description =
                    "Project ${project.name} associated with SCM ${entry.repository} (${entry.scm} @ ${entry.config})"
            } else {
                title = entry.repository
                description = "SCM ${entry.repository} (${entry.scm} @ ${entry.config}) not associated with any project"
            }
            // Result
            SearchResult(
                title = title,
                description = description,
                accuracy = score,
                type = searchResultType,
                data = mapOf(
                    SEARCH_RESULT_SCM_CATALOG_ENTRY to entry,
                    SearchResult.SEARCH_RESULT_PROJECT to project,
                )
            )
        }
    }

    companion object {
        const val SEARCH_RESULT_SCM_CATALOG_ENTRY = "scmCatalogEntry"
    }

    override val isIndexationDisabled: Boolean = false

    override val indexerSchedule: Schedule = Schedule.EVERY_WEEK

    override val indexMapping: SearchIndexMapping = indexMappings<SCMCatalogSearchItem> {
        +SCMCatalogSearchItem::scm to keyword()
        +SCMCatalogSearchItem::config to keyword()
        +SCMCatalogSearchItem::repository to keyword { scoreBoost = 3.0 } to text()
    }
}

data class SCMCatalogSearchItem(
    override val id: String,
    val scm: String,
    val config: String,
    val repository: String
) : SearchItem {

    constructor(entry: SCMCatalogEntry) : this(
        id = entry.key,
        scm = entry.scm,
        config = entry.config,
        repository = entry.repository
    )

    override val fields: Map<String, Any?> = asMap(
        this::scm,
        this::config,
        this::repository
    )

}
