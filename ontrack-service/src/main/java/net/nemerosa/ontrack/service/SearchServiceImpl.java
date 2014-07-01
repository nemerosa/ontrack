package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.model.structure.SearchRequest;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final Collection<SearchProvider> providers;

    @Autowired
    public SearchServiceImpl(ApplicationContext applicationContext) {
        this.providers = applicationContext.getBeansOfType(SearchProvider.class).values();
    }

    @Override
    public Collection<SearchResult> search(SearchRequest request) {
        return providers.stream()
                .filter(provider -> provider.isTokenSearchable(request.getToken()))
                .flatMap(provider -> provider.search(request.getToken()).stream())
                .collect(Collectors.toList());
    }
}
