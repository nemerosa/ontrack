package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionInfo;

/**
 * Layer on top of the basic Subversion client and of the repositories.
 */
public interface SVNService {

    SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision);

}
