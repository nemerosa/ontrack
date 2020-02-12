package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.model.SVNRepositoryRevision;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionNotFoundException;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.SearchResultType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class SVNRevisionSearchExtension extends AbstractExtension implements SearchExtension {

    private final URIBuilder uriBuilder;
    private final SVNConfigurationService configurationService;
    private final SVNService svnService;

    private final SearchResultType resultType;

    @Autowired
    public SVNRevisionSearchExtension(
            SVNExtensionFeature extensionFeature,
            URIBuilder uriBuilder,
            SVNConfigurationService configurationService,
            SVNService svnService
    ) {
        super(extensionFeature);
        this.uriBuilder = uriBuilder;
        this.configurationService = configurationService;
        this.svnService = svnService;
        this.resultType = new SearchResultType(
                extensionFeature.getFeatureDescription(),
                "svn-revision",
                "SVN Revision",
                "SVN Revision number"
        );
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new AbstractSearchProvider(uriBuilder) {

            @Override
            public boolean isTokenSearchable(String token) {
                if (StringUtils.isNumeric(token)) {
                    long value = Long.parseLong(token, 10);
                    return value > 0 && value < Integer.MAX_VALUE;
                } else {
                    return false;
                }
            }

            @Override
            public Collection<SearchResult> search(String token) {
                return configurationService.getConfigurationDescriptors().stream()
                        .map(descriptor -> svnService.getRepository(descriptor.getId()))
                        .map(repository -> {
                            try {
                                return new SVNRepositoryRevision(
                                        repository,
                                        svnService.getRevisionInfo(repository, Long.valueOf(token, 10))
                                );
                            } catch (SVNRevisionNotFoundException ex) {
                                return null;
                            }
                        })
                        .filter(repositoryRevision -> repositoryRevision != null)
                        .map(repositoryRevision -> new SearchResult(
                                String.valueOf(repositoryRevision.getRevisionInfo().getRevision()),
                                getSearchRevisionDescription(repositoryRevision),
                                uri(on(SVNController.class).revisionInfo(
                                        repositoryRevision.getRepository().getConfiguration().getName(),
                                        repositoryRevision.getRevisionInfo().getRevision()
                                )),
                                uriBuilder.page("extension/svn/revision/%s/%d",
                                        repositoryRevision.getRepository().getConfiguration().getName(),
                                        repositoryRevision.getRevisionInfo().getRevision()),
                                100,
                                resultType
                        ))
                        .collect(Collectors.toList());
            }
        };
    }

    private String getSearchRevisionDescription(SVNRepositoryRevision repositoryRevision) {
        return String.format("Revision %d in %s repository",
                repositoryRevision.getRevisionInfo().getRevision(),
                repositoryRevision.getRepository().getConfiguration().getName()
        );
    }
}
