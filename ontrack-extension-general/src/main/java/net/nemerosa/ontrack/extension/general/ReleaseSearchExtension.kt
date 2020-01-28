package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
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
), SearchExtension {

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
    }

}