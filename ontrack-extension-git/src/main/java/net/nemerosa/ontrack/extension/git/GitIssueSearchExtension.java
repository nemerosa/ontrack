package net.nemerosa.ontrack.extension.git;

import lombok.Data;
import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitIssueSearchExtension extends AbstractExtension implements SearchExtension {

    private final GitService gitService;
    private final URIBuilder uriBuilder;

    @Autowired
    public GitIssueSearchExtension(
            GitExtensionFeature extensionFeature,
            GitService gitService,
            URIBuilder uriBuilder) {
        super(extensionFeature);
        this.gitService = gitService;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new GitIssueSearchProvider(uriBuilder);
    }

    @Data
    protected static class BranchSearchConfiguration {

        private final Branch branch;
        private final GitBranchConfiguration gitBranchConfiguration;
        private final ConfiguredIssueService configuredIssueService;

    }

    protected class GitIssueSearchProvider extends AbstractSearchProvider {

        private final Collection<BranchSearchConfiguration> branchSearchConfigurations;

        public GitIssueSearchProvider(URIBuilder uriBuilder) {
            super(uriBuilder);
            branchSearchConfigurations = new ArrayList<>();
            gitService.forEachConfiguredBranch((branch, branchConfiguration) -> {
                GitConfiguration config = branchConfiguration.getConfiguration();
                ConfiguredIssueService configuredIssueService = config.getConfiguredIssueService().orElse(null);
                if (configuredIssueService != null) {
                    branchSearchConfigurations.add(new BranchSearchConfiguration(
                            branch,
                            branchConfiguration,
                            configuredIssueService
                    ));
                }
            });
        }

        @Override
        public boolean isTokenSearchable(String token) {
            return branchSearchConfigurations.stream()
                    .anyMatch(c -> c.getConfiguredIssueService().getIssueServiceExtension().validIssueToken(token));
        }

        @Override
        public Collection<SearchResult> search(String token) {
            // Map of results per project, with the first result being the one for the first corresponding branch
            Map<ID, SearchResult> projectResults = new LinkedHashMap<>();
            // For all the configurations
            for (BranchSearchConfiguration c : branchSearchConfigurations) {
                ID projectId = c.getBranch().getProjectId();
                // Skipping if associated project is already associated with the issue
                if (!projectResults.containsKey(projectId)) {
                    // ... searches for the issue token in the git repository
                    final boolean found = gitService.isPatternFound(c.getGitBranchConfiguration(), token);
                    // ... and if found
                    if (found) {
                        // ... loads the issue
                        Issue issue = c.getConfiguredIssueService().getIssue(token);
                        // Saves the result for the project if an issue has been found
                        if (issue != null) {
                            projectResults.put(
                                    projectId,
                                    new SearchResult(
                                            issue.getDisplayKey(),
                                            String.format("Issue %s found in project %s",
                                                    issue.getKey(),
                                                    c.getBranch().getProject().getName()
                                            ),
                                            uri(on(GitController.class).issueInfo(
                                                    c.getBranch().getId(),
                                                    issue.getKey()
                                            )),
                                            uriBuilder.page("extension/git/%d/issue/%s",
                                                    c.getBranch().id(),
                                                    issue.getKey()),
                                            100
                                    )
                            );
                        }
                    }
                }
            }
            // OK
            return projectResults.values();
        }

    }
}
