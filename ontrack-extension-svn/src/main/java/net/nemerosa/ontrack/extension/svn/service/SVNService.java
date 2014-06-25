package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionPaths;

/**
 * Layer on top of the basic Subversion client and of the repositories.
 */
public interface SVNService {

    /**
     * Gets the information about a revision
     *
     * @param revision Revision to get information about
     * @return Full details about this revision
     */
    SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision);

    /**
     * Gets the list of changes for a revision
     */
    SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision);

}
