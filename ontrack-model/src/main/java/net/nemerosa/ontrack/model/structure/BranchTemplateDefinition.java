package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Explicit association between a branch and its template definition.
 */
@Data
public class BranchTemplateDefinition {

    private final ID branchId;
    private final TemplateDefinition templateDefinition;

}
