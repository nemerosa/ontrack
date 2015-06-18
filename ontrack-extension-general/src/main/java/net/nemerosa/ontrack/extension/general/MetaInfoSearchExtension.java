package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class MetaInfoSearchExtension extends AbstractExtension implements SearchExtension {

    private final URIBuilder uriBuilder;
    private final PropertyService propertyService;

    @Autowired
    public MetaInfoSearchExtension(GeneralExtensionFeature extensionFeature, URIBuilder uriBuilder, PropertyService propertyService) {
        super(extensionFeature);
        this.uriBuilder = uriBuilder;
        this.propertyService = propertyService;
    }

    @Override
    public SearchProvider getSearchProvider() {
        return new AbstractSearchProvider(uriBuilder) {
            @Override
            public boolean isTokenSearchable(String token) {
                return MetaInfoSearchExtension.this.isTokenSearchable(token);
            }

            @Override
            public Collection<SearchResult> search(String token) {
                return MetaInfoSearchExtension.this.search(token);
            }
        };
    }

    protected boolean isTokenSearchable(String token) {
        return StringUtils.indexOf(token, ":") > 0;
    }

    protected Collection<SearchResult> search(String token) {
        if (isTokenSearchable(token)) {
            String name = StringUtils.substringBefore(token, ":");
            String value = StringUtils.substringAfter(token, ":");
            // Searchs for all entities with the value
            Collection<ProjectEntity> entities = propertyService.searchWithPropertyValue(
                    MetaInfoPropertyType.class,
                    metaInfoProperty -> metaInfoProperty.matchNameValue(name, value)
            );
            // FIXME Returns entities
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }
}
