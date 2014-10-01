package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Request for the copy of a branch configuration into another one.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BranchCopyRequest extends AbstractCopyRequest {

    /**
     * Source branch
     */
    private final ID sourceBranchId;

    @ConstructorProperties({"sourceBranchId", "replacements"})
    public BranchCopyRequest(ID sourceBranchId, List<Replacement> replacements) {
        super(replacements);
        this.sourceBranchId = sourceBranchId;
    }
}
