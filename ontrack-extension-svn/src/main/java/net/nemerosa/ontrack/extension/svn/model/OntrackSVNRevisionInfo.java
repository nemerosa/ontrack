package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

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

}
