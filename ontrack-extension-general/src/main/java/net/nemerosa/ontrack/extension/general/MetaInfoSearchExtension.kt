package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class MetaInfoSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), SearchExtension {

    override fun getSearchProvider(): SearchProvider {
        return object : AbstractSearchProvider(uriBuilder) {
            override fun isTokenSearchable(token: String): Boolean {
                return this@MetaInfoSearchExtension.isTokenSearchable(token)
            }

            override fun search(token: String): Collection<SearchResult> {
                return this@MetaInfoSearchExtension.search(token)
            }
        }
    }

    fun isTokenSearchable(token: String): Boolean = StringUtils.indexOf(token, ":") > 0

    protected fun search(token: String): Collection<SearchResult> {
        return if (isTokenSearchable(token)) {
            val name = StringUtils.substringBefore(token, ":")
            val value = StringUtils.substringAfter(token, ":")
            // Searchs for all entities with the value
            val entities = propertyService.searchWithPropertyValue(
                    MetaInfoPropertyType::class.java,
                    { entityType, id -> entityType.getEntityFn(structureService).apply(id) },
                    { metaInfoProperty -> metaInfoProperty.matchNameValue(name, value) }
            )
            // Returns search results
            entities.map { entity -> toSearchResult(entity, name) }
        } else {
            emptyList()
        }
    }

    protected fun toSearchResult(entity: ProjectEntity, name: String): SearchResult {
        // Gets the property value for the meta info name (required)
        val value = propertyService.getProperty(entity, MetaInfoPropertyType::class.java).value.getValue(name)
                ?: throw IllegalStateException("Expecting to have a meta info property")
        // OK
        return SearchResult(
                entity.entityDisplayName,
                String.format("%s -> %s", name, value),
                uriBuilder.getEntityURI(entity),
                uriBuilder.getEntityPage(entity),
                100
        )
    }
}
