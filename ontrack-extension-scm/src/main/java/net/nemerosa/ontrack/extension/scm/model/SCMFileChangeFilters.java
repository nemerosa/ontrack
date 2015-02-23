package net.nemerosa.ontrack.extension.scm.model;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
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
        return with(store -> store.put(filter.getName(), filter));
    }

    public SCMFileChangeFilters remove(String name) {
        return with(store -> store.remove(name));
    }

    protected SCMFileChangeFilters with(Consumer<Map<String, SCMFileChangeFilter>> action) {
        Map<String, SCMFileChangeFilter> store = new TreeMap<>(
                filters.stream()
                        .collect(Collectors.toMap(
                                SCMFileChangeFilter::getName,
                                f -> f
                        ))
        );
        action.accept(store);
        return new SCMFileChangeFilters(Lists.newArrayList(store.values()));
    }
}
