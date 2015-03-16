package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Explicit association between a branch and its template definition.
 */
@Data
public class LoadedBranchTemplateDefinition {

    private final Branch branch;
    private final TemplateDefinition templateDefinition;

}
