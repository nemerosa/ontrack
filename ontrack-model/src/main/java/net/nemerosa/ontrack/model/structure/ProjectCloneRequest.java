package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Request for the clone of a project into another one.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ProjectCloneRequest extends AbstractCopyRequest {

    /**
     * Target project name
     */
    private final String name;
    /**
     * Source branch
     */
    private final ID sourceBranchId;

    @ConstructorProperties({"name", "sourceBranchId", "replacements"})
    public ProjectCloneRequest(String name, ID sourceBranchId, List<Replacement> replacements) {
        super(replacements);
        this.name = name;
        this.sourceBranchId = sourceBranchId;
    }
}
