package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.git.model.GitCommit;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitUICommit;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitCommitSearchExtension extends AbstractExtension implements SearchExtension {

    private final Pattern shaPattern = Pattern.compile("[a-f0-9]{40}|[a-f0-9]{7}");

    private final GitService gitService;
    private final URIBuilder uriBuilder;

    @Autowired
    public GitCommitSearchExtension(
            GitExtensionFeature extensionFeature,
            GitService gitService,
            URIBuilder uriBuilder) {
        super(extensionFeature);
        this.gitService = gitService;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new GitCommitSearchProvider(uriBuilder);
    }

    protected class GitCommitSearchProvider extends AbstractSearchProvider {

        public GitCommitSearchProvider(URIBuilder uriBuilder) {
            super(uriBuilder);
        }

        @Override
        public boolean isTokenSearchable(String token) {
            return shaPattern.matcher(token).matches();
        }

        @Override
        public Collection<SearchResult> search(String token) {
            // Map of results per project, with the first result being the one for the first corresponding branch
            Map<ID, SearchResult> projectResults = new LinkedHashMap<>();
            // For all Git-configured branches
            gitService.forEachConfiguredBranch((branch, branchConfiguration) -> {
                GitConfiguration config = branchConfiguration.getConfiguration();
                ID projectId = branch.getProjectId();
                // Skipping if associated project is already associated with the issue
                if (!projectResults.containsKey(projectId)) {
                    // ... scans for the commit
                    Optional<GitUICommit> commit = gitService.lookupCommit(config, token);
                    // ... and if found
                    if (commit.isPresent()) {
                        GitCommit theCommit = commit.get().getCommit();
                        // ... creates a result entry
                        projectResults.put(
                                projectId,
                                new SearchResult(
                                        String.format("[%s] %s %s",
                                                branch.getProject().getName(),
                                                theCommit.getId(),
                                                theCommit.getShortMessage()),
                                        String.format("%s - %s",
                                                theCommit.getAuthor().getName(),
                                                commit.get().getFullAnnotatedMessage()),
                                        uri(on(GitController.class)
                                                .commitInfo(branch.getId(), theCommit.getId())),
                                        uriBuilder.page("extension/git/%d/commit/%s",
                                                branch.id(),
                                                theCommit.getId()),
                                        100
                                )
                        );
                    }
                }
            });
            // OK
            return projectResults.values();
        }
    }
}
