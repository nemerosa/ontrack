package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNHistory;
import net.nemerosa.ontrack.extension.svn.model.SVNReference;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionPath;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;

public interface SVNClient {

    boolean exists(SVNRepository repository, SVNURL url, SVNRevision revision);

    long getRepositoryRevision(SVNRepository repository, SVNURL url);

    void log(SVNRepository repository, SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision,
             boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions,
             ISVNLogEntryHandler isvnLogEntryHandler);

    boolean isTrunkOrBranch(SVNRepository repository, String path);

    boolean isTagOrBranch(SVNRepository repository, String path);

    boolean isTag(SVNRepository repository, String path);

    List<Long> getMergedRevisions(SVNRepository repository, SVNURL url, long revision);

    SVNReference getReference(SVNRepository repository, String path);

    SVNHistory getHistory(SVNRepository repository, String path);

    List<SVNRevisionPath> getRevisionPaths(SVNRepository repository, long revision);

    List<String> getBranches(SVNRepository repository, SVNURL url);

    String getDiff(SVNRepository repository, String path, List<Long> revisions);
}
