package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class SVNIssueSearchExtension extends AbstractExtension implements SearchExtension {

    private final URIBuilder uriBuilder;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNConfigurationService configurationService;

    @Autowired
    public SVNIssueSearchExtension(
            SVNExtensionFeature extensionFeature,
            URIBuilder uriBuilder,
            IssueServiceRegistry issueServiceRegistry,
            SVNConfigurationService configurationService
    ) {
        super(extensionFeature);
        this.uriBuilder = uriBuilder;
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
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
                // FIXME SVN search
                return Collections.emptyList();
            }
        };
    }
}
