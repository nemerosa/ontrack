package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Two builds in a branch.
 */
@Data
public abstract class BuildDiff {

    /**
     * The associated branch
     */
    private final Branch branch;

    public abstract BuildView getFrom();

    public abstract BuildView getTo();

}
