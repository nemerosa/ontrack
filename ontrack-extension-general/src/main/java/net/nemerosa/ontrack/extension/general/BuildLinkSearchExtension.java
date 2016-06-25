package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searching on the build links.
 */
@Component
public class BuildLinkSearchExtension extends AbstractExtension implements SearchExtension {

    private final URIBuilder uriBuilder;
    private final StructureService structureService;

    @Autowired
    public BuildLinkSearchExtension(GeneralExtensionFeature extensionFeature, URIBuilder uriBuilder, StructureService structureService) {
        super(extensionFeature);
        this.uriBuilder = uriBuilder;
        this.structureService = structureService;
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new AbstractSearchProvider(uriBuilder) {
            @Override
            public boolean isTokenSearchable(String token) {
                return BuildLinkSearchExtension.this.isTokenSearchable(token);
            }

            @Override
            public Collection<SearchResult> search(String token) {
                return BuildLinkSearchExtension.this.search(token);
            }
        };
    }

    protected boolean isTokenSearchable(String token) {
        return StringUtils.indexOf(token, ":") > 0;
    }

    protected Collection<SearchResult> search(String token) {
        if (isTokenSearchable(token)) {
            String project = StringUtils.substringBefore(token, ":");
            String buildName = StringUtils.substringAfter(token, ":");
            // Searchs for all builds which are linked to project:build*
            List<Build> builds = structureService.searchBuildsLinkedTo(project, buildName);
            // Returns search results
            return builds.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    protected SearchResult toSearchResult(Build build) {
        return new SearchResult(
                build.getEntityDisplayName(),
                String.format("%s -> %s", build.getProject().getName(), build.getName()),
                uriBuilder.getEntityURI(build),
                uriBuilder.getEntityPage(build),
                100
        );
    }
}
