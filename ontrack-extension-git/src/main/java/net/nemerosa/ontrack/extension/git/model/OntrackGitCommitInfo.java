package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.BranchStatusView;
import net.nemerosa.ontrack.model.structure.BuildView;

import java.util.Collection;

/**
 * All the information about a commit in a Git configuration, with its links with all
 * the projects.
 */
@Data
public class OntrackGitCommitInfo {

    /**
     * Repository
     */
    @JsonView({OntrackGitCommitInfo.class})
    private final GitConfiguration configuration;

    /**
     * Basic info about the commit
     */
    private final GitUICommit uiCommit;

    /**
     * Collection of build views
     */
    private final Collection<BuildView> buildViews;

    /**
     * Collection of promotions for the branches
     */
    private final Collection<BranchStatusView> branchStatusViews;

}
