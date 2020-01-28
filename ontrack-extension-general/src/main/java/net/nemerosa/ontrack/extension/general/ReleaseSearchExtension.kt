package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.exceptions.NotFoundException
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
                            100.0
                    )
                }

        override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this@ReleaseSearchExtension)
    }

    override val indexerName: String = "Release property"

    override val indexName: String = RELEASE_SEARCH_INDEX

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
        val entity = try {
            item.entityType.getEntityFn(structureService).apply(ID.of(item.entityId))
        } catch (_: NotFoundException) {
            null
        }
        // Conversion
        return entity?.let {
            SearchResult(
                    entity.entityDisplayName,
                    "${entity.entityDisplayName} having version/label/release ${item.release}",
                    uriBuilder.getEntityURI(entity),
                    uriBuilder.getEntityPage(entity),
                    score
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
    override val id: String = "$entityType::$entityId"
    override val fields: Map<String, Any?> = asMap(ReleaseSearchItem::fields.name)
}
