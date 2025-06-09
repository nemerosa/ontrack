package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.function.BiConsumer

@Component
class GitBranchSearchIndexer(
    extensionFeature: GitExtensionFeature,
    private val gitService: GitService,
    private val structureService: StructureService,
) : SearchIndexer<GitBranchSearchItem> {

    override val searchResultType = SearchResultType(
        feature = extensionFeature.featureDescription,
        id = "git-branch",
        name = "Git Branch",
        description = "Git branch associated to an Ontrack branch",
        order = SearchResultType.ORDER_PROPERTIES + 50,
    )

    override val indexerName: String = "Git Branches"

    override val indexName: String = GIT_BRANCH_SEARCH_INDEX

    override val indexMapping: SearchIndexMapping? = indexMappings<GitBranchSearchItem> {
        +GitBranchSearchItem::branchId to id { index = false }
        +GitBranchSearchItem::gitBranch to keyword { scoreBoost = 4.0 } to text()
    }

    override fun indexAll(processor: (GitBranchSearchItem) -> Unit) {
        gitService.forEachConfiguredBranch(BiConsumer { branch, branchConfig ->
            processor(
                GitBranchSearchItem(branch, branchConfig)
            )
        })
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        val branchId = id.toIntOrNull(10)
        val branch = branchId?.let { structureService.findBranchByID(ID.of(branchId)) }
        val branchConfig = branch?.let { gitService.getBranchConfiguration(branch) }
        return if (branch != null && branchConfig != null) {
            SearchResult(
                title = branch.entityDisplayName,
                description = "Git branch ${branchConfig.branch}",
                accuracy = score,
                type = searchResultType,
                data = mapOf(
                    SearchResult.SEARCH_RESULT_BRANCH to branch,
                    SEARCH_RESULT_GIT_BRANCH to branchConfig.branch,
                ),
            )
        } else null
    }

    companion object {
        const val SEARCH_RESULT_GIT_BRANCH = "gitBranch"
    }
}

const val GIT_BRANCH_SEARCH_INDEX = "git-branch"

class GitBranchSearchItem(
    val branchId: Int,
    val gitBranch: String
) : SearchItem {

    constructor(branch: Branch, branchConfig: GitBranchConfiguration) : this(
        branchId = branch.id(),
        gitBranch = branchConfig.branch
    )

    override val id: String = branchId.toString()

    override val fields: Map<String, Any?> = asMap(
        this::gitBranch
    )
}