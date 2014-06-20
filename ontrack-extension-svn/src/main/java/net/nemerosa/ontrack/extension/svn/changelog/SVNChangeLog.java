package net.nemerosa.ontrack.extension.svn.changelog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BuildView;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNChangeLog extends SCMChangeLog {

    protected SVNChangeLog(Branch branch, BuildView fromBuild, BuildView toBuild) {
        super(branch, fromBuild, toBuild);
    }

    public static SVNChangeLog of(SCMChangeLog scmChangeLog) {
        return new SVNChangeLog(
                scmChangeLog.getBranch(),
                scmChangeLog.getFromBuild(),
                scmChangeLog.getToBuild()
        );
    }
}
