package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.git.model.GitUICommit;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.git.model.GitCommit;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
            // List of results
            List<SearchResult> results = new ArrayList<>();
            // For all Git-configured projects
            gitService.forEachConfiguredProject((project, gitConfiguration) -> {
                // ... scans for the commit
                Optional<GitUICommit> commit = gitService.lookupCommit(gitConfiguration, token);
                // ... and if found
                if (commit.isPresent()) {
                    GitCommit theCommit = commit.get().getCommit();
                    // ... creates a result entry
                    results.add(
                            new SearchResult(
                                    String.format("[%s] %s %s",
                                            project.getName(),
                                            theCommit.getId(),
                                            theCommit.getShortMessage()),
                                    String.format("%s - %s",
                                            theCommit.getAuthor().getName(),
                                            commit.get().getFullAnnotatedMessage()),
                                    uri(on(GitController.class)
                                            .commitProjectInfo(project.getId(), theCommit.getId())),
                                    uriBuilder.page("extension/git/%d/commit/%s",
                                            project.id(),
                                            theCommit.getId()),
                                    100
                            )
                    );
                }
            });
            // OK
            return results;
        }
    }
}
