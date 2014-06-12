package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import org.tmatesoft.svn.core.SVNURL;

public interface SVNClient {

    long getRepositoryRevision(SVNRepository repository, SVNURL url);

}
