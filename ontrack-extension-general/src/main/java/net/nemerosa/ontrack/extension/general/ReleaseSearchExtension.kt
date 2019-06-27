package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component

@Component
class ReleaseSearchExtension(
        extensionFeature: ExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : AbstractExtension(
        extensionFeature
), SearchExtension {

    override fun getSearchProvider() = object : AbstractSearchProvider(uriBuilder) {

        /**
         * Any token is searchable
         */
        override fun isTokenSearchable(token: String): Boolean = true

        override fun search(token: String): Collection<SearchResult> {
            TODO()
        }

    }

}