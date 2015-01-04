package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.structure.Project;

/**
 * This extension allows to an action on the selection of two {@link net.nemerosa.ontrack.model.structure.Build builds}
 * for the same {@link net.nemerosa.ontrack.model.structure.Project project}.
 * <p>
 * The {@link #getAction()} method must point to a REST endpoint that accepts a
 * {@link net.nemerosa.ontrack.extension.api.model.BuildDiffRequest} as a <code>GET</code> parameter and
 * returns a {@link net.nemerosa.ontrack.model.buildfilter.BuildDiff} as a decorated response.
 */
public interface BuildDiffExtension extends ActionExtension {

    /**
     * Checks if this action is applicable for the given {@code project}.
     */
    boolean apply(Project project);

}
