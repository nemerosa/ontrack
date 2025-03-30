package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Action;

import java.util.List;

/**
 * Filtered list of builds for a branch, with all their validation
 * stamps and promotion levels.
 *
 * @deprecated Will be removed in V6. Not used by Next UI.
 */
@Data
@Deprecated
public class BranchBuildView implements View {

    /**
     * Filtered list of build views (the filter is not managed by the view itself)
     */
    private final List<BuildView> buildViews;

    /**
     * List of build diff actions.
     */
    private final List<Action> buildDiffActions;

}
