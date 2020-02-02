package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.SearchRequest;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * End point for the search.
 */
@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Collection<SearchResult> search(@RequestBody SearchRequest request) {
        return searchService.search(request);
    }

}
