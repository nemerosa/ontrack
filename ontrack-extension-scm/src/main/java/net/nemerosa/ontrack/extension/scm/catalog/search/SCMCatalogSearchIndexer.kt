package net.nemerosa.ontrack.extension.scm.catalog.search

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.ui.SCMCatalogController
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.net.URI

@Component
class SCMCatalogSearchIndexer(
        extensionFeature: SCMExtensionFeature,
        private val scmCatalog: SCMCatalog,
        private val catalogLinkService: CatalogLinkService,
        private val uriBuilder: URIBuilder
) : SearchIndexer<SCMCatalogSearchItem> {

    override val indexerName: String = "SCM Catalog"

    override val indexName: String = "scm-catalog"

    override val searchResultType = SearchResultType(
            feature = extensionFeature.featureDescription,
            id = "scm-catalog",
            name = "SCM Catalog",
            description = "Indexed SCM repository, which might be associated or not with an Ontrack project"
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
            val uri: URI
            val page: URI
            if (project != null) {
                title = "${project.name} (${entry.repository})"
                description = "Project ${project.name} associated with SCM ${entry.repository} (${entry.scm} @ ${entry.config})"
                uri = uriBuilder.getEntityURI(project)
                page = uriBuilder.getEntityPage(project)
            } else {
                title = entry.repository
                description = "SCM ${entry.repository} (${entry.scm} @ ${entry.config}) not associated with any project"
                uri = uriBuilder.build(MvcUriComponentsBuilder.on(SCMCatalogController::class.java).entries())
                page = uriBuilder.page("extension/scm/scm-catalog")
            }
            // Result
            SearchResult(
                    title = title,
                    description = description,
                    uri = uri,
                    page = page,
                    accuracy = score,
                    type = searchResultType
            )
        }
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
