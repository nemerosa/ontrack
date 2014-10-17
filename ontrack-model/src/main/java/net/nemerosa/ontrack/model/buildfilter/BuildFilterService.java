package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotFoundException;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotLoggedException;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;

/**
 * Management of build filters.
 */
public interface BuildFilterService {

    /**
     * Gets the default filter to use when none is defined.
     */
    BuildFilter defaultFilter();

    /**
     * Gets the list of all existing filters.
     *
     * @param branchId Branch to get the filters for
     * @return List of filters
     */
    Collection<BuildFilterResource<?>> getBuildFilters(ID branchId);

    /**
     * Gets the list of forms to create new filters
     *
     * @param branchId Branch to get the forms for
     * @return List of forms
     */
    Collection<BuildFilterForm> getBuildFilterForms(ID branchId);

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

    /**
     * Gets the form to edit an existing filter.
     *
     * @param branchId Branch to get the form on
     * @param name     name of the filter on the branch
     * @return An edition form
     * @throws BuildFilterNotFoundException  If the filter is not defined
     * @throws BuildFilterNotLoggedException If the user is not logged
     */
    BuildFilterForm getEditionForm(ID branchId, String name) throws BuildFilterNotFoundException, BuildFilterNotLoggedException;

    /**
     * Saves a filter for a branch. This method does nothing if the user is not logged,
     * or if the filter type is not found, or if the parameters are not valid.
     *
     * @param branchId   Branch to save the filter for
     * @param shared     If the filter must be shared in the branch (needs specific authorisation)
     * @param name       Name of the filter
     * @param type       Type of the filter
     * @param parameters Parameters for the filter
     */
    Ack saveFilter(ID branchId, boolean shared, String name, String type, JsonNode parameters);

    /**
     * Deletes a filter for a branch. This method does nothing if the user is not logged,
     * or if the filter is not found.
     *
     * @param branchId Branch to save the filter for
     * @param name     Name of the filter
     */
    Ack deleteFilter(ID branchId, String name);

    /**
     * Copies all the filters from one branch to another
     */
    void copyToBranch(ID sourceBranchId, ID targetBranchId);
}
