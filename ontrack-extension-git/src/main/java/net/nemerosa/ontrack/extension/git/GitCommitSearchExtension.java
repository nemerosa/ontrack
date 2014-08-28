package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.git.client.GitCommit;
import net.nemerosa.ontrack.extension.git.model.GitUICommit;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class GitCommitSearchExtension extends AbstractExtension implements SearchExtension {

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
        return new GitCommitSearchProvider();
    }

    protected class GitCommitSearchProvider extends AbstractSearchProvider {

        public GitCommitSearchProvider() {
            super(uriBuilder);
        }

        @Override
        public boolean isTokenSearchable(String token) {
            return Pattern.matches("[a-f0-9]{40}", token);
        }

        @Override
        public Collection<SearchResult> search(String token) {
            Collection<SearchResult> results = new ArrayList<>();
            // For all Git-configured branches
            gitService.forEachConfiguredBranch((branch, config) -> {
                // ... scans for the commit
                Optional<GitUICommit> commit = gitService.lookupCommit(config, token);
                // ... and if found
                if (commit.isPresent()) {
                    GitCommit theCommit = commit.get().getCommit();
                    // ... creates a result entry
                    results.add(
                            new SearchResult(
                                    String.format("%s %s",
                                            theCommit.getId(),
                                            theCommit.getShortMessage()),
                                    String.format(""),
                                    null, // TODO URI
                                    String.format("extension/git/%d/commit/%s",
                                            branch.id(),
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
