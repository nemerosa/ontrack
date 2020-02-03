package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.stream.Collectors

/**
 * Searching on the build links.
 */
@Component
class BuildLinkSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), SearchExtension {

    private val resultType = SearchResultType(
            extensionFeature.featureDescription,
            "build-link",
            "Build Link"
    )

    override fun getSearchProvider(): SearchProvider {
        return object : AbstractSearchProvider(uriBuilder) {
            override fun isTokenSearchable(token: String): Boolean {
                return this@BuildLinkSearchExtension.isTokenSearchable(token)
            }

            override fun search(token: String): Collection<SearchResult> {
                return this@BuildLinkSearchExtension.search(token)
            }
        }
    }

    fun isTokenSearchable(token: String): Boolean {
        return StringUtils.indexOf(token, ":") > 0
    }

    protected fun search(token: String): Collection<SearchResult> {
        return if (isTokenSearchable(token)) {
            val project = StringUtils.substringBefore(token, ":")
            val buildName = StringUtils.substringAfter(token, ":")
            // Searches for all builds which are linked to project:build*
            val builds = structureService.searchBuildsLinkedTo(project, buildName)
            // Returns search results
            builds.map { build: Build -> toSearchResult(build) }
        } else {
            emptyList()
        }
    }

    protected fun toSearchResult(build: Build): SearchResult {
        return SearchResult(
                build.entityDisplayName, "${build.project.name} -> ${build.name}",
                uriBuilder.getEntityURI(build),
                uriBuilder.getEntityPage(build),
                100.0,
                resultType
        )
    }

}