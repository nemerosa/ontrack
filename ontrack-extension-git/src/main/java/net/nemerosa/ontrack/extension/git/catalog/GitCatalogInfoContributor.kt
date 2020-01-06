package net.nemerosa.ontrack.extension.git.catalog

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.scm.catalog.AbstractCatalogInfoContributor
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GitCatalogInfoContributor(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val gitRepositoryClientFactory: GitRepositoryClientFactory
) : AbstractCatalogInfoContributor<GitCatalogInfo>(extensionFeature) {

    override fun collectInfo(project: Project, entry: SCMCatalogEntry): GitCatalogInfo? =
            gitService.getProjectConfiguration(project)
                    ?.let { collectInfo(it) }

    private fun collectInfo(gitConfiguration: GitConfiguration): GitCatalogInfo? {
        // Gets a Git client
        val client = gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository)
        // Gets the last commit
        val hash = client.getLastCommitForExpression(".*")
        // Gets the commit info
        return hash?.run {
            client.getCommitFor(this)
        }?.run {
            gitService.toUICommit(gitConfiguration, this)
        }?.run {
            GitCatalogInfo(this)
        }
    }

    override fun asJson(info: GitCatalogInfo): JsonNode = info.asJson()

    override fun fromJson(node: JsonNode): GitCatalogInfo = node.parse()

    override val name: String = "Git information"

}