package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Request for the bulk update of a branch.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BranchBulkUpdateRequest extends AbstractCopyRequest {

    @ConstructorProperties({"replacements"})
    public BranchBulkUpdateRequest(List<Replacement> replacements) {
        super(replacements);
    }
}
