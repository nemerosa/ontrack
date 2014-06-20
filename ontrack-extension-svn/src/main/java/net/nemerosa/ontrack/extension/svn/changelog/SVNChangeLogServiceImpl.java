package net.nemerosa.ontrack.extension.svn.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SVNChangeLogServiceImpl extends AbstractSCMChangeLogService implements SVNChangeLogService {

    @Autowired
    public SVNChangeLogServiceImpl(StructureService structureService) {
        super(structureService);
    }

    @Override
    public SVNChangeLog changeLog(BuildDiffRequest request) {
        Branch branch = structureService.getBranch(request.getBranch());
        SVNRepository svnRepository = getSVNRepository(branch);
        return new SVNChangeLog(
                branch,
                svnRepository,
                getSCMBuildView(svnRepository, request.getFrom()),
                getSCMBuildView(svnRepository, request.getTo())
        );
    }

    protected SCMBuildView<SVNHistory> getSCMBuildView(SVNRepository svnRepository, ID buildId) {
        // Gets the build view
        BuildView buildView = getBuildView(buildId);
        // Gets the history for the build
        SVNHistory history = getBuildSVNHistory(svnRepository, buildView.getBuild());
        // OK
        return new SCMBuildView<>(buildView, history);
    }

    private SVNHistory getBuildSVNHistory(SVNRepository svnRepository, Build build) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.changelog.SVNChangeLogServiceImpl.getBuildSVNHistory
        return null;
    }

    protected SVNRepository getSVNRepository(Branch branch) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.changelog.SVNChangeLogServiceImpl.getSVNRepository
        return null;
    }

}
