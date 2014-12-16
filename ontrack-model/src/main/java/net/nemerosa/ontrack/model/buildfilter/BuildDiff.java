package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.BuildView;
import net.nemerosa.ontrack.model.structure.Project;

/**
 * Two builds in a branch.
 */
@Data
public abstract class BuildDiff {

    /**
     * The associated project
     */
    private final Project project;

    public abstract BuildView getFrom();

    public abstract BuildView getTo();

}
