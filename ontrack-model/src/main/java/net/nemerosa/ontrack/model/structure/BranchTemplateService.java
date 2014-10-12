package net.nemerosa.ontrack.model.structure;

import java.util.Optional;

public interface BranchTemplateService {

    /**
     * Gets the template definition for a branch
     */
    Optional<TemplateDefinition> getTemplateDefinition(ID branchId);

    /**
     * Sets the branch as a template definition or updates the existing definition.
     */
    Branch setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition);
}
