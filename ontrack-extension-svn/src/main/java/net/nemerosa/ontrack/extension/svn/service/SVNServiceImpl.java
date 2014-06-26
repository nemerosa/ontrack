package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRevisionDao;
import net.nemerosa.ontrack.extension.svn.db.TRevision;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionPath;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SVNServiceImpl implements SVNService {

    private final SVNRevisionDao revisionDao;
    private final SVNClient svnClient;

    @Autowired
    public SVNServiceImpl(SVNRevisionDao revisionDao, SVNClient svnClient) {
        this.revisionDao = revisionDao;
        this.svnClient = svnClient;
    }

    @Override
    public SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision) {
        TRevision t = revisionDao.get(repository.getId(), revision);
        return new SVNRevisionInfo(
                t.getRevision(),
                t.getAuthor(),
                t.getCreation(),
                t.getBranch(),
                t.getMessage(),
                repository.getRevisionBrowsingURL(t.getRevision())
        );
    }

    @Override
    public SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision) {
        // Gets the diff for the revision
        List<SVNRevisionPath> revisionPaths = svnClient.getRevisionPaths(repository, revision);
        // OK
        return new SVNRevisionPaths(
                getRevisionInfo(repository, revision),
                revisionPaths);
    }
}
