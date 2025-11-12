package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class ReleaseSearchExtension(
    extensionFeature: GeneralExtensionFeature,
    private val propertyService: PropertyService,
    private val structureService: StructureService
) : AbstractExtension(
    extensionFeature
), SearchIndexer<ReleaseSearchItem> {

    override val searchResultType = SearchResultType(
        feature = extensionFeature.featureDescription,
        id = SEARCH_RESULT_TYPE,
        name = "Build with Release",
        description = "Release, label or version attached to a build",
        order = SearchResultType.ORDER_PROPERTIES + 10,
    )

    override val indexerName: String = "Release property"

    override val indexName: String = RELEASE_SEARCH_INDEX

    override val indexSettings: SearchIndexSettings = autoCompleteSearchIndexSettings()

    override val indexMapping: SearchIndexMapping = indexMappings {
        +ReleaseSearchItem::entityId to id { index = false }
        +ReleaseSearchItem::entityType to keyword { index = false }
        +ReleaseSearchItem::release to autoCompleteText {
            scoreBoost = 5.0
        }
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
                accuracy = score,
                type = searchResultType,
                data = mapOf(
                    SearchResult.SEARCH_RESULT_BUILD to entity,
                    SEARCH_RESULT_RELEASE to item.release,
                )
            )
        }
    }

    companion object {
        const val SEARCH_RESULT_RELEASE = "release"
        const val SEARCH_RESULT_TYPE = "build-release"
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
