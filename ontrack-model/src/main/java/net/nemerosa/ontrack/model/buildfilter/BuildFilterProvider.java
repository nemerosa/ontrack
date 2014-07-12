package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Optional;

public interface BuildFilterProvider<T> {

    /**
     * Display name
     */
    String getName();

    /**
     * If this method returns <code>true</code>, there is no need to configure the filter.
     */
    boolean isPredefined();

    /**
     * Gets the form for a new filter on the given branch
     */
    BuildFilterForm newFilterForm(ID branchId);

    BuildFilterForm getFilterForm(ID branchId, T data);

    /**
     * Builds an actual filter using the given set of parameters
     */
    BuildFilter filter(ID branchId, T data);

    Optional<T> parse(JsonNode data);

}
