package net.nemerosa.ontrack.extension.svn.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.BranchStatusView;
import net.nemerosa.ontrack.model.structure.BuildView;

import java.util.Collection;

/**
 * All the information about a revision in a repository, with its links with all
 * the projects.
 */
@Data
public class OntrackSVNRevisionInfo {

    /**
     * Repository
     */
    @JsonView({OntrackSVNRevisionInfo.class})
    private final SVNConfiguration configuration;

    /**
     * Basic info about the revision
     */
    private final SVNChangeLogRevision revisionInfo;

    /**
     * Collection of build views
     */
    private final Collection<BuildView> buildViews;

    /**
     * Collection of promotions for the branches
     */
    private final Collection<BranchStatusView> branchStatusViews;

}
