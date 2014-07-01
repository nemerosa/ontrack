package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.SearchExtension;
import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link net.nemerosa.ontrack.model.structure.SearchProvider} based on extensions.
 */
@Component
public class ExtensionSearchProvider implements SearchProvider {

    private final ExtensionManager extensionManager;

    @Autowired
    public ExtensionSearchProvider(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    protected Stream<SearchProvider> providers() {
        return extensionManager.getExtensions(SearchExtension.class).stream().map(SearchExtension::getSearchProvider);
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return providers().filter(p -> p.isTokenSearchable(token)).findAny().isPresent();
    }

    @Override
    public Collection<SearchResult> search(String token) {
        return providers()
                .filter(p -> p.isTokenSearchable(token))
                .flatMap(p -> p.search(token).stream())
                .collect(Collectors.toList());
    }
}
