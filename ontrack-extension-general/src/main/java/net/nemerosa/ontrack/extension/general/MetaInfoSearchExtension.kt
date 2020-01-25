package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.function.BiFunction
import java.util.function.Predicate

@Component
class MetaInfoSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val structureService: StructureService,
        private val securityService: SecurityService
) : AbstractExtension(extensionFeature), SearchExtension, SearchIndexer<MetaInfoSearchItem> {

    override fun getSearchProvider(): SearchProvider {
        return object : AbstractSearchProvider(uriBuilder) {
            override fun isTokenSearchable(token: String): Boolean {
                return this@MetaInfoSearchExtension.isTokenSearchable(token)
            }

            override fun search(token: String): Collection<SearchResult> {
                return this@MetaInfoSearchExtension.search(token)
            }

            override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this@MetaInfoSearchExtension)
        }
    }

    fun isTokenSearchable(token: String): Boolean = StringUtils.indexOf(token, ":") > 0

    protected fun search(token: String): Collection<SearchResult> {
        return if (isTokenSearchable(token)) {
            val name = StringUtils.substringBefore(token, ":")
            val value = StringUtils.substringAfter(token, ":")
            // Searchs for all entities with the value
            val entities = securityService.callAsAdmin {
                propertyService.searchWithPropertyValue(
                        MetaInfoPropertyType::class.java,
                        BiFunction { entityType, id -> entityType.getEntityFn(structureService).apply(id) },
                        Predicate { metaInfoProperty -> metaInfoProperty.matchNameValue(name, value) }
                )
            }.filter { entity ->
                securityService.isProjectFunctionGranted(entity, ProjectView::class.java)
            }
            // Returns search results
            entities.mapNotNull { entity -> toSearchResult(entity, name) }
        } else {
            emptyList()
        }
    }

    protected fun toSearchResult(entity: ProjectEntity, name: String): SearchResult? {
        // Gets the property value for the meta info name (required)
        val value = propertyService.getProperty(entity, MetaInfoPropertyType::class.java).value.getValue(name)
        // OK
        return value?.run {
            SearchResult(
                    entity.entityDisplayName,
                    String.format("%s -> %s", name, this),
                    uriBuilder.getEntityURI(entity),
                    uriBuilder.getEntityPage(entity),
                    100
            )
        }
    }

    override val indexerName: String = "Meta info properties"
    override val indexName: String = "meta-info-properties"

    override fun indexAll(processor: (MetaInfoSearchItem) -> Unit) {
        propertyService.forEachEntityWithProperty<MetaInfoPropertyType, MetaInfoProperty> { entityId, property ->
            property.items.forEach {
                val item = MetaInfoSearchItem(
                        name = it.name,
                        value = it.value,
                        link = it.link,
                        category = it.category,
                        entityType = entityId.type,
                        entityId = entityId.id
                )
                processor(item)
            }
        }
    }
}

class MetaInfoSearchItem(
        val name: String,
        val value: String?,
        val link: String?,
        val category: String?,
        val entityType: ProjectEntityType,
        val entityId: Int
) : SearchItem {

    override val id: String = "$entityType::$entityId"

    override val fields: Map<String, Any> = TODO("Convert POJO to map")

}