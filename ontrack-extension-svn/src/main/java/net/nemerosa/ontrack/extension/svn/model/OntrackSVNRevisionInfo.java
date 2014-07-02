package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
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
    private final SVNConfiguration configuration;

    /**
     * Basic info about the revision
     */
    private final SVNChangeLogRevision revisionInfo;

    /**
     * Collection of build views
     */
    private final Collection<BuildView> buildViews;

}
