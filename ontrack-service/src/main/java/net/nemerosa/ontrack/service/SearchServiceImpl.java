package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchRequest;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final List<SearchProvider> providers;

    @Autowired
    public SearchServiceImpl(List<SearchProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Collection<SearchResult> search(SearchRequest request) {
        return providers.stream()
                .filter(provider -> provider.isTokenSearchable(request.getToken()))
                .flatMap(provider -> provider.search(request.getToken()).stream())
                .collect(Collectors.toList());
    }
}
