package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Explicit association between a branch and its template instance.
 */
@Data
public class BranchTemplateInstance {

    private final ID branchId;
    private final TemplateInstance templateInstance;

}
