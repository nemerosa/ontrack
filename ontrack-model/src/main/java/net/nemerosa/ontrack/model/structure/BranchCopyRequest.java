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

    @ConstructorProperties({"sourceBranchId", "propertyReplacements", "promotionLevelReplacements", "validationStampReplacements"})
    public BranchCopyRequest(ID sourceBranchId, List<Replacement> propertyReplacements, List<Replacement> promotionLevelReplacements, List<Replacement> validationStampReplacements) {
        super(propertyReplacements, promotionLevelReplacements, validationStampReplacements);
        this.sourceBranchId = sourceBranchId;
    }
}
