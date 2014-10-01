package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Request for the clone of a branch into another one.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BranchCloneRequest extends AbstractCopyRequest {

    /**
     * Target branch name
     */
    private final String name;

    @ConstructorProperties({"name", "propertyReplacements", "promotionLevelReplacements", "validationStampReplacements"})
    public BranchCloneRequest(String name, List<Replacement> propertyReplacements, List<Replacement> promotionLevelReplacements, List<Replacement> validationStampReplacements) {
        super(propertyReplacements, promotionLevelReplacements, validationStampReplacements);
        this.name = name;
    }
}
