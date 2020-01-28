package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.*
import java.util.function.BiConsumer

@Component
class GitIssueSearchExtension(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val uriBuilder: URIBuilder
) : AbstractExtension(extensionFeature), SearchExtension {

    override fun getSearchProvider(): SearchProvider {
        return GitIssueSearchProvider(uriBuilder)
    }

    protected inner class GitIssueSearchProvider(uriBuilder: URIBuilder) : AbstractSearchProvider(uriBuilder) {

        override fun isTokenSearchable(token: String): Boolean {
            var match = false
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { _, gitConfiguration ->
                if (!match) {
                    // Gets issue service
                    if (gitConfiguration.configuredIssueService.isPresent) {
                        match = gitConfiguration.configuredIssueService.get()
                                .issueServiceExtension.validIssueToken(token)
                    }
                }
            })
            return match
        }

        override fun search(token: String): Collection<SearchResult> {
            // Map of results per project, with the first result being the one for the first corresponding branch
            val projectResults = LinkedHashMap<ID, SearchResult>()
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { project, gitConfiguration ->
                val configuredIssueService: ConfiguredIssueService? =
                        gitConfiguration.configuredIssueService.orElse(null)
                if (configuredIssueService != null) {
                    val projectId: ID = project.id
                    // Skipping if associated project is already associated with the issue
                    if (!projectResults.containsKey(projectId)) {
                        // ... searches for the issue token in the git repository
                        val found = configuredIssueService.issueServiceExtension.validIssueToken(token) &&
                                gitService.isPatternFound(gitConfiguration, token)
                        // ... and if found
                        if (found) {
                            // ... loads the issue
                            val issue: Issue? = configuredIssueService.getIssue(token)
                            // Saves the result for the project if an issue has been found
                            if (issue != null) {
                                projectResults[projectId] = SearchResult(
                                        issue.displayKey,
                                        String.format("Issue %s found in project %s",
                                                issue.key,
                                                project.name
                                        ),
                                        uri(on(GitController::class.java).issueProjectInfo(
                                                project.id,
                                                issue.key
                                        )),
                                        uriBuilder.page("extension/git/%d/issue/%s",
                                                project.id(),
                                                issue.key),
                                        100.0
                                )
                            }
                        }
                    }
                }
            })
            // OK
            return projectResults.values
        }

    }
}
