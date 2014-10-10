package net.nemerosa.ontrack.model.structure;

import java.util.Optional;

public interface BranchTemplateService {

    /**
     * Gets the template definition for a branch
     */
    Optional<TemplateDefinition> getTemplateDefinition(ID branchId);

}
