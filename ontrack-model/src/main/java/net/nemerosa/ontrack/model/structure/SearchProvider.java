package net.nemerosa.ontrack.model.structure;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;

public interface SearchProvider {

    boolean isTokenSearchable(String token);

    Collection<SearchResult> search(String token);

    /**
     * List of indexers associated with this provider.
     * <p>
     * Default implementation does not return any
     */
    @NonNull
    default Collection<SearchIndexer<?>> getSearchIndexers() {
        return Collections.emptyList();
    }

}
