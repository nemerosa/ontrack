package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Two builds in a branch.
 */
@Data
public class BuildDiff {

    /**
     * The associated branch
     */
    private final Branch branch;

    /**
     * From the build...
     */
    private final BuildView fromBuild;

    /**
     * ... to the build
     */
    private final BuildView toBuild;

}
