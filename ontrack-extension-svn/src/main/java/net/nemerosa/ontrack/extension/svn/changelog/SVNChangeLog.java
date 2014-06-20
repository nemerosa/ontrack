package net.nemerosa.ontrack.extension.svn.changelog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.model.structure.Branch;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNChangeLog extends SCMChangeLog<SVNRepository, SVNHistory> {

    public SVNChangeLog(
            Branch branch,
            SVNRepository scmBranch,
            SCMBuildView<SVNHistory> scmBuildFrom,
            SCMBuildView<SVNHistory> scmBuildTo) {
        super(branch, scmBranch, scmBuildFrom, scmBuildTo);
    }

}
