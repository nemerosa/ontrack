package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Service

@Service
class DefaultFileLoaderService(
    private val gitHubClientFactory: OntrackGitHubClientFactory,
    private val propertyService: PropertyService,
    private val gitService: GitService,
) : FileLoaderService {

    override fun loadFile(branch: Branch, path: String): String? {
        val gitHubProjectProperty =
            propertyService.getPropertyValue(branch.project, GitHubProjectConfigurationPropertyType::class.java)
                ?: return null
        val pr = gitService.getBranchAsPullRequest(branch)
        val ref = if (pr != null) {
            pr.source
        } else {
            val gitBranchProperty =
                propertyService.getPropertyValue(branch, GitBranchConfigurationPropertyType::class.java)
                    ?: return null
            gitBranchProperty.branch
        }
        val client = gitHubClientFactory.create(gitHubProjectProperty.configuration)
        val binaryContent =
            client.getFileContent(gitHubProjectProperty.repository, ref, path) ?: return null
        // Assuming UTF-8
        return binaryContent.toString(Charsets.UTF_8)
    }
}