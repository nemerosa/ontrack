package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BuildView;

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
