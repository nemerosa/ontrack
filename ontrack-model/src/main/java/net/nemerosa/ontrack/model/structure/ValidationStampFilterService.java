package net.nemerosa.ontrack.model.structure;

import java.util.List;

/**
 * Accessing the {@link ValidationStampFilter} at all levels, and managing them.
 */
public interface ValidationStampFilterService {

    /**
     * Gets the list of global filters
     */
    List<ValidationStampFilter> getGlobalValidationStampFilters();

    /**
     * Gets the list of filters associated with a project
     *
     * @param project    Project
     * @param includeAll <code>true</code> if the global filters must be included as well
     */
    List<ValidationStampFilter> getProjectValidationStampFilters(Project project, boolean includeAll);

    /**
     * Gets the list of filters associated with a branch
     *
     * @param branch     Branch
     * @param includeAll <code>true</code> if the project filters & global filters must be included as well
     */
    List<ValidationStampFilter> getBranchValidationStampFilters(Branch branch, boolean includeAll);

    /**
     * Creates a filter
     */
    ValidationStampFilter newValidationStampFilter(ValidationStampFilter filter);

    /**
     * Updates a filter
     */
    ValidationStampFilter updateValidationStampFilter(ValidationStampFilter filter);

    /**
     * Deletes a filter
     */
    ValidationStampFilter deleteValidationStampFilter(ValidationStampFilter filter);

    /**
     * Shares a filter with project
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter, Project project);

    /**
     * Shares a filter globally
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter);

}
