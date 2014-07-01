package net.nemerosa.ontrack.model.structure;

import java.util.Collection;

public interface SearchProvider {

    boolean isTokenSearchable(String token);

    Collection<SearchResult> search(String token);
}
