package net.nemerosa.ontrack.model.structure;

import java.util.Collection;

public interface SearchService {

    Collection<SearchResult> search(SearchRequest request);

}
