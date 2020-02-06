package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.model.SVNRepositoryIssue;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.SearchResultType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class SVNIssueSearchExtension extends AbstractExtension implements SearchExtension {

    private final URIBuilder uriBuilder;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNConfigurationService configurationService;
    private final SVNService svnService;

    private final SearchResultType resultType;

    @Autowired
    public SVNIssueSearchExtension(
            SVNExtensionFeature extensionFeature,
            URIBuilder uriBuilder,
            IssueServiceRegistry issueServiceRegistry,
            SVNConfigurationService configurationService,
            SVNService svnService
    ) {
        super(extensionFeature);
        this.uriBuilder = uriBuilder;
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
        this.svnService = svnService;
        this.resultType = new SearchResultType(
                extensionFeature.getFeatureDescription(),
                "svn-issue",
                "SVN Issue",
                "Issue key as they appear in commit messages for Subversion repositories"
        );
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new AbstractSearchProvider(uriBuilder) {

            @Override
            public boolean isTokenSearchable(String token) {
                return issueServiceRegistry.getIssueServices().stream()
                        .filter(s -> s.validIssueToken(token))
                        .findAny()
                        .isPresent();
            }

            @Override
            public Collection<SearchResult> search(String token) {
                return configurationService.getConfigurationDescriptors().stream()
                        .map(descriptor -> svnService.getRepository(descriptor.getId()))
                        .map(repository -> svnService.searchIssues(repository, token))
                        .filter(Optional::isPresent).map(Optional::get)
                        .map(repositoryIssue -> new SearchResult(
                                repositoryIssue.getIssue().getKey(),
                                getSearchIssueDescription(repositoryIssue),
                                uri(on(SVNController.class).issueInfo(
                                        repositoryIssue.getRepository().getConfiguration().getName(),
                                        repositoryIssue.getIssue().getKey()
                                )),
                                uriBuilder.page("extension/svn/issue/%s/%s",
                                        repositoryIssue.getRepository().getConfiguration().getName(),
                                        repositoryIssue.getIssue().getKey()),
                                100,
                                resultType
                        ))
                        .collect(Collectors.toList());
            }
        };
    }

    private String getSearchIssueDescription(SVNRepositoryIssue repositoryIssue) {
        return String.format("Issue %s in %s repository: %s",
                repositoryIssue.getIssue().getKey(),
                repositoryIssue.getRepository().getConfiguration().getName(),
                repositoryIssue.getIssue().getSummary());
    }
}
