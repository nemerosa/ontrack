package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component

@Component
class ReleaseSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(
        extensionFeature
), SearchExtension, SearchIndexer<ReleaseSearchItem> {

    override val searchResultType = SearchResultType(
            extensionFeature.featureDescription,
            "build-release",
            "Build with Release"
    )

    override fun getSearchProvider() = object : AbstractSearchProvider(uriBuilder) {

        /**
         * Any token is searchable
         */
        override fun isTokenSearchable(token: String): Boolean = true

        override fun search(token: String): Collection<SearchResult> =
                propertyService.findByEntityTypeAndSearchkey(
                        ProjectEntityType.BUILD,
                        ReleasePropertyType::class.java,
                        token
                ).map { id ->
                    structureService.getBuild(id)
                }.map { build ->
                    SearchResult(
                            build.entityDisplayName,
                            "Build ${build.entityDisplayName} having version/label/release $token",
                            uriBuilder.getEntityURI(build),
                            uriBuilder.getEntityPage(build),
                            100.0,
                            searchResultType
                    )
                }

        override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this@ReleaseSearchExtension)
    }

    override val indexerName: String = "Release property"

    override val indexName: String = RELEASE_SEARCH_INDEX

    override val indexMapping: SearchIndexMapping = indexMappings<ReleaseSearchItem> {
        +ReleaseSearchItem::entityId to id { index = false }
        +ReleaseSearchItem::entityType to keyword { index = false }
        +ReleaseSearchItem::release to keyword { scoreBoost = 5.0 }
    }

    override fun indexAll(processor: (ReleaseSearchItem) -> Unit) {
        propertyService.forEachEntityWithProperty<ReleasePropertyType, ReleaseProperty> { entityId, property ->
            processor(
                    ReleaseSearchItem(
                            release = property.name,
                            entityType = entityId.type,
                            entityId = entityId.id
                    )
            )
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Parsing
        val item = source.parseOrNull<ReleaseSearchItem>()
        // Conversion
        return item?.let { toSearchResult(it, score) }
    }

    private fun toSearchResult(item: ReleaseSearchItem, score: Double): SearchResult? {
        // Loads the entity
        val entity: ProjectEntity? = item.entityType.getFindEntityFn(structureService).apply(ID.of(item.entityId))
        // Conversion
        return entity?.let {
            SearchResult(
                    title = entity.entityDisplayName,
                    description = "${entity.entityDisplayName} having version/label/release ${item.release}",
                    uri = uriBuilder.getEntityURI(entity),
                    page = uriBuilder.getEntityPage(entity),
                    accuracy = score,
                    type = searchResultType
            )
        }
    }
}

/**
 * Release property search index
 */
const val RELEASE_SEARCH_INDEX = "releases"

data class ReleaseSearchItem(
        val release: String,
        val entityType: ProjectEntityType,
        val entityId: Int
) : SearchItem {

    constructor(entity: ProjectEntity, property: ReleaseProperty) : this(
            release = property.name,
            entityType = entity.projectEntityType,
            entityId = entity.id()
    )

    override val id: String = "$entityType::$entityId"
    override val fields: Map<String, Any?> = mapOf(
            "release" to release,
            "entityType" to entityType,
            "entityId" to entityId
    )
}
