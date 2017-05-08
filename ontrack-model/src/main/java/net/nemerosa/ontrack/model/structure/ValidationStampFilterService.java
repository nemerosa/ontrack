package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.Ack;

import java.util.List;
import java.util.Optional;

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
     * Gets a validation stamp filter by name for a branch
     */
    Optional<ValidationStampFilter> getValidationStampFilterByName(Branch branch, String name);

    /**
     * Creates a filter
     */
    ValidationStampFilter newValidationStampFilter(ValidationStampFilter filter);

    /**
     * Updates a filter
     */
    void saveValidationStampFilter(ValidationStampFilter filter);

    /**
     * Deletes a filter
     */
    Ack deleteValidationStampFilter(ValidationStampFilter filter);

    /**
     * Shares a filter with project
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter, Project project);

    /**
     * Shares a filter globally
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter);

    /**
     * Gets a validation stamp filter by ID
     */
    ValidationStampFilter getValidationStampFilter(ID id);
}
