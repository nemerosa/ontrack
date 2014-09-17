package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * Request for the copy of a branch configuration into another one.
 */
@Data
public class BranchCopyRequest {

    /**
     * Source branch
     */
    private final ID sourceBranchId;

    /**
     * Replacements
     */
    private final List<Replacement> propertyReplacements;
    private final List<Replacement> promotionLevelReplacements;
    private final List<Replacement> validationStampReplacements;

}
