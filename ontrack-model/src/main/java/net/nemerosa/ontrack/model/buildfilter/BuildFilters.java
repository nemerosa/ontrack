package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;

import java.util.Collection;

@Data
public class BuildFilters {

    /**
     * List of available filter types
     */
    private final Collection<BuildFilterForm> buildFilterForms;

    /**
     * List of filters
     */
    private final Collection<BuildFilterResource> buildFilterResources;

}
