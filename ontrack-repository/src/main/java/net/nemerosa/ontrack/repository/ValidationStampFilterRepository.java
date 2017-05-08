package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ValidationStampFilter;

import java.util.List;
import java.util.Optional;

public interface ValidationStampFilterRepository {

    /**
     * Gets the list of global filters
     */
    List<ValidationStampFilter> getGlobalValidationStampFilters();

    /**
     * Gets the list of filters associated with a project
     *
     * @param project Project
     */
    List<ValidationStampFilter> getProjectValidationStampFilters(Project project);

    /**
     * Gets the list of filters associated with a branch
     *
     * @param branch Branch
     */
    List<ValidationStampFilter> getBranchValidationStampFilters(Branch branch);

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
    Ack deleteValidationStampFilter(ID filterId);

    /**
     * Loads a filter by ID
     */
    ValidationStampFilter getValidationStampFilter(ID filterId);

    /**
     * Shares a filter with project
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter, Project project);

    /**
     * Shares a filter globally
     */
    ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter);
}
