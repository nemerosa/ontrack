package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component

@Component
class MetaInfoSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), SearchIndexer<MetaInfoSearchItem> {

    override val searchResultType = SearchResultType(
            extensionFeature.featureDescription,
            "build-meta-info",
            "Build with Meta Info",
            "Meta information pair using format name:[value] or value"
    )

    override val indexerName: String = "Meta info properties"

    override val indexName: String = META_INFO_SEARCH_INDEX

    override val indexMapping: SearchIndexMapping? = indexMappings<MetaInfoSearchItem> {
        +MetaInfoSearchItem::entityId to id { index = false }
        +MetaInfoSearchItem::entityType to keyword { index = false }
        +MetaInfoSearchItem::items to nested()
        +MetaInfoSearchItem::keys to keyword { scoreBoost = 3.0 } to text()
    }

    override fun indexAll(processor: (MetaInfoSearchItem) -> Unit) {
        propertyService.forEachEntityWithProperty<MetaInfoPropertyType, MetaInfoProperty> { entityId, property ->
            processor(
                    MetaInfoSearchItem(
                            entityId = entityId,
                            property = property
                    )
            )
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Parsing
        val item = source.parseOrNull<MetaInfoSearchItem>()
        // Conversion
        return item?.let { toSearchResult(it, score) }
    }

    private fun toSearchResult(item: MetaInfoSearchItem, score: Double): SearchResult? {
        // Loads the entity
        val entity: ProjectEntity? = item.entityType.getFindEntityFn(structureService).apply(ID.of(item.entityId))
        // Conversion (using legacy code)
        return entity?.let {
            SearchResult(
                    title = entity.entityDisplayName,
                    description = item.items.map { (name, value) -> "$name -> $value" }.sorted().joinToString(", "),
                    uri = uriBuilder.getEntityURI(entity),
                    page = uriBuilder.getEntityPage(entity),
                    accuracy = score,
                    type = searchResultType
            )
        }
    }

}

/**
 * Separator between name and value when looking for meta information.
 */
const val META_INFO_SEPARATOR = ":"

/**
 * Index name for the meta info search
 */
const val META_INFO_SEARCH_INDEX = "meta-info-properties"

@JsonIgnoreProperties(ignoreUnknown = true)
class MetaInfoSearchItem(
        val items: Map<String, String>,
        val entityType: ProjectEntityType,
        val entityId: Int
) : SearchItem {

    constructor(entity: ProjectEntity, property: MetaInfoProperty) : this(
            entityId = ProjectEntityID(entity.projectEntityType, entity.id()),
            property = property
    )

    constructor(entityId: ProjectEntityID, property: MetaInfoProperty) : this(
            items = property.items.map {
                it.name to (it.value ?: "")
            }.associate { it },
            entityType = entityId.type,
            entityId = entityId.id
    )

    val keys = items.map { (name, value) -> "$name$META_INFO_SEPARATOR$value" }

    override val id: String = "$entityType::$entityId"

    override val fields: Map<String, Any?> = mapOf(
            "keys" to items.map { (name, value) -> "$name$META_INFO_SEPARATOR$value" },
            "items" to items,
            "entityType" to entityType,
            "entityId" to entityId
    )

}