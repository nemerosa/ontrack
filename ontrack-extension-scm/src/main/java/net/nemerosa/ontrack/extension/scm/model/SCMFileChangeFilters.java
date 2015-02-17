package net.nemerosa.ontrack.extension.scm.model;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Defines a list of filters
 */
@Data
public class SCMFileChangeFilters {

    /**
     * List of filters
     */
    private final List<SCMFileChangeFilter> filters;

    public static SCMFileChangeFilters create() {
        return new SCMFileChangeFilters(Collections.emptyList());
    }

    public SCMFileChangeFilters save(SCMFileChangeFilter filter) {
        Map<String, SCMFileChangeFilter> store = new TreeMap<>(
                filters.stream()
                        .collect(Collectors.toMap(
                                SCMFileChangeFilter::getName,
                                f -> f
                        ))
        );
        store.put(filter.getName(), filter);
        return new SCMFileChangeFilters(Lists.newArrayList(store.values()));
    }
}
