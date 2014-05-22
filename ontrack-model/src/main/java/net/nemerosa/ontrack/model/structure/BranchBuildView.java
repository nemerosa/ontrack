package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * Filtered list of builds for a branch, with all their validation
 * stamps and promotion levels.
 */
@Data
public class BranchBuildView implements View {

    /**
     * Filtered list of builds (the filter is not managed by the view itself)
     */
    private final List<Build> builds;

}
