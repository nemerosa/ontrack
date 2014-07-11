package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ID;

/**
 * Management of build filters.
 */
public interface BuildFilterService {

    /**
     * Gets the default filter to use when none is defined.
     */
    BuildFilter defaultFilter();

    /**
     * Gets the list of all filters, and the forms to create new ones.
     *
     * @param branchId Branch to get the filters for
     * @return List of filters and the forms to define new ones
     */
    BuildFilters getBuildFilters(ID branchId);

    /**
     * Basic method to compute an actual filter from a type and a list of parameters.
     *
     * @param branchId   Branch to apply the filter on
     * @param type       Qualified type for the filter
     * @param parameters Parameters for the filter
     * @return Actual filter to use, or the {@linkplain #defaultFilter() default filter} if
     * the <code>type</code> cannot be resolved or if the filter cannot be instantiated using
     * the given parameters.
     */
    BuildFilter computeFilter(ID branchId, String type, JsonNode parameters);
}
