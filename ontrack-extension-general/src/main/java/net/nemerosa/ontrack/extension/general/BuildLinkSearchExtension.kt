package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

/**
 * Searching on the build links.
 */
@Component
class BuildLinkSearchExtension(
        extensionFeature: GeneralExtensionFeature,
        private val uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val buildDisplayNameService: BuildDisplayNameService
) : AbstractExtension(extensionFeature), SearchExtension, SearchIndexer<BuildLinkSearchItem> {

    override fun getSearchProvider(): SearchProvider {
        return object : AbstractSearchProvider(uriBuilder) {
            override fun isTokenSearchable(token: String): Boolean {
                return this@BuildLinkSearchExtension.isTokenSearchable(token)
            }

            override fun search(token: String): Collection<SearchResult> {
                return this@BuildLinkSearchExtension.search(token)
            }

            override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(
                    this@BuildLinkSearchExtension
            )
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
                searchResultType
        )
    }

    override val indexerName: String = "Build links"

    override val indexName: String = BUILD_LINK_SEARCH_INDEX

    override val indexerSchedule: Schedule = Schedule.EVERY_DAY

    override val searchResultType = SearchResultType(
            feature = extensionFeature.featureDescription,
            id = "build-link",
            name = "Linked Build"
    )

    override val indexMapping: SearchIndexMapping? = indexMappings<BuildLinkSearchItem> {
        +BuildLinkSearchItem::fromBuildId to id { index = false }
        +BuildLinkSearchItem::targetBuildId to id { index = false }
        +BuildLinkSearchItem::targetProject to keyword()
        +BuildLinkSearchItem::targetBuild to keyword()
        +BuildLinkSearchItem::targetKey to text { scoreBoost = 3.0 }
    }

    override fun indexAll(processor: (BuildLinkSearchItem) -> Unit) {
        structureService.forEachBuildLink { from, to ->
            processor(BuildLinkSearchItem(from, to))
            // Alternative name
            val otherName = buildDisplayNameService.getBuildDisplayName(to)
            if (otherName != to.name) {
                processor(BuildLinkSearchItem(from, to, otherName))
            }
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Parsing
        val item = source.parseOrNull<BuildLinkSearchItem>()
        // Loading
        return item?.run {
            // Loads the source build
            structureService.findBuildByID(ID.of(item.fromBuildId))
        }?.run {
            SearchResult(
                    entityDisplayName,
                    "Linked to ${item.targetProject}:${item.targetBuild}",
                    uriBuilder.getEntityURI(this),
                    uriBuilder.getEntityPage(this),
                    score,
                    searchResultType
            )
        }
    }
}

/**
 * Index name for the build links
 */
const val BUILD_LINK_SEARCH_INDEX = "build-links"

class BuildLinkSearchItem(
        val fromBuildId: Int,
        val targetBuildId: Int,
        val targetProject: String,
        val targetBuild: String
) : SearchItem {

    constructor(from: Build, to: Build, targetBuildName: String = to.name): this(
            fromBuildId = from.id(),
            targetBuildId = to.id(),
            targetProject = to.project.name,
            targetBuild = targetBuildName
    )

    override val id: String = "$fromBuildId::$targetBuildId"

    val targetKey = "$targetProject:$targetBuild"

    override val fields: Map<String, Any?> = asMap(
            this::fromBuildId,
            this::targetBuildId,
            this::targetProject,
            this::targetBuild,
            this::targetKey
    )

}