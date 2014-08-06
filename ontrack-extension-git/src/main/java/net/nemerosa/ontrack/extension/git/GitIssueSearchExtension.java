package net.nemerosa.ontrack.extension.git;

import lombok.Data;
import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitIssueSearchExtension extends AbstractExtension implements SearchExtension {

    private final GitService gitService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final URIBuilder uriBuilder;

    @Autowired
    public GitIssueSearchExtension(
            GitExtensionFeature extensionFeature,
            GitService gitService,
            IssueServiceRegistry issueServiceRegistry,
            URIBuilder uriBuilder) {
        super(extensionFeature);
        this.gitService = gitService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new GitIssueSearchProvider();
    }

    @Data
    protected static class BranchSearchConfiguration {

        private final Branch branch;
        private final GitConfiguration gitConfiguration;
        private final ConfiguredIssueService configuredIssueService;

    }

    protected class GitIssueSearchProvider extends AbstractSearchProvider {

        private final Collection<BranchSearchConfiguration> branchSearchConfigurations;

        public GitIssueSearchProvider() {
            super(uriBuilder);
            branchSearchConfigurations = new ArrayList<>();
            gitService.forEachConfiguredBranch((branch, config) -> {
                String issueServiceConfigurationIdentifier = config.getIssueServiceConfigurationIdentifier();
                if (StringUtils.isNotBlank(issueServiceConfigurationIdentifier)) {
                    ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(issueServiceConfigurationIdentifier);
                    if (configuredIssueService != null) {
                        branchSearchConfigurations.add(new BranchSearchConfiguration(
                                branch,
                                config,
                                configuredIssueService
                        ));
                    }
                }
            });
        }

        @Override
        public boolean isTokenSearchable(String token) {
            return branchSearchConfigurations.stream()
                    .filter(c -> c.getConfiguredIssueService().getIssueServiceExtension().validIssueToken(token))
                    .findAny()
                    .isPresent();
        }

        @Override
        public Collection<SearchResult> search(String token) {
            Collection<SearchResult> results = new ArrayList<>();
            // For all the configurations
            for (BranchSearchConfiguration c : branchSearchConfigurations) {
                // ... searches for the issue token in the git repository
                boolean found = gitService.scanCommits(c.getGitConfiguration(), commit -> scanIssue(c, commit, token));
                // ... and if found
                if (found) {
                    // ... loads the issue
                    Issue issue = c.getConfiguredIssueService().getIssue(token);
                    // ... and creates a result entry
                    results.add(
                            new SearchResult(
                                    issue.getKey(),
                                    String.format("Issue %s in Git repository %s for branch %s/%s",
                                            issue.getKey(),
                                            c.getGitConfiguration().getName(),
                                            c.getBranch().getProject().getName(),
                                            c.getBranch().getName()
                                    ),
                                    uri(on(GitController.class).issueInfo(
                                            c.getBranch().getId(),
                                            issue.getKey()
                                    )),
                                    String.format("extension/git/%d/issue/%s",
                                            c.getBranch().id(),
                                            issue.getKey()),
                                    100
                            )
                    );
                }
            }
            // OK
            return results;
        }

        private boolean scanIssue(BranchSearchConfiguration c, RevCommit commit, String key) {
            String message = commit.getFullMessage();
            Set<String> keys = c.getConfiguredIssueService().extractIssueKeysFromMessage(message);
            return c.getConfiguredIssueService().containsIssueKey(key, keys);
        }
    }
}
