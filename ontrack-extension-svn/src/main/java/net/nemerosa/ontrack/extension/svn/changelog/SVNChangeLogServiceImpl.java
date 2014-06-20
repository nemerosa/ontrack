package net.nemerosa.ontrack.extension.svn.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog;
import net.nemerosa.ontrack.model.structure.StructureService;
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
        // Loads the default SCM change log
        SCMChangeLog scmChangeLog = defaultChangeLog(request);
        // TODO Decorations for the change log
        // OK
        return SVNChangeLog.of(scmChangeLog);
    }

}
