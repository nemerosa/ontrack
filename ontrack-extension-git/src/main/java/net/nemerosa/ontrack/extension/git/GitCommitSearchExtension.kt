package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.*
import java.util.function.BiConsumer
import java.util.regex.Pattern

@Component
class GitCommitSearchExtension(
        extensionFeature: GitExtensionFeature?,
        private val gitService: GitService,
        private val uriBuilder: URIBuilder
) : AbstractExtension(extensionFeature), SearchExtension {

    private val shaPattern = Pattern.compile("[a-f0-9]{40}|[a-f0-9]{7}")

    override fun getSearchProvider(): SearchProvider {
        return GitCommitSearchProvider(uriBuilder)
    }

    protected inner class GitCommitSearchProvider(uriBuilder: URIBuilder?) : AbstractSearchProvider(uriBuilder) {
        override fun isTokenSearchable(token: String): Boolean {
            return shaPattern.matcher(token).matches()
        }

        override fun search(token: String): Collection<SearchResult> { // List of results
            val results: MutableList<SearchResult> = ArrayList()
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { project: Project, gitConfiguration: GitConfiguration? ->
                // ... scans for the commit
                val commit = gitService.lookupCommit(gitConfiguration!!, token)
                // ... and if found
                if (commit != null) { // ... creates a result entry
                    results.add(
                            SearchResult(String.format("[%s] %s %s",
                                    project.name,
                                    commit.id,
                                    commit.shortMessage), String.format("%s - %s",
                                    commit.author.name,
                                    commit.fullMessage),
                                    uri(MvcUriComponentsBuilder.on(GitController::class.java)
                                            .commitProjectInfo(project.id, commit.id)),
                                    uriBuilder.page("extension/git/%d/commit/%s",
                                            project.id(),
                                            commit.id),
                                    100.0
                            )
                    )
                }
            })
            // OK
            return results
        }
    }

}