package net.nemerosa.ontrack.extension.svn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.model.structure.Branch;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNChangeLog extends SCMChangeLog<SVNRepository, SVNHistory> {

    @JsonIgnore // Not sent to the client
    private final SVNChangeLogRevisions revisions;

    protected SVNChangeLog(
            String uuid,
            Branch branch,
            SVNRepository scmBranch,
            SCMBuildView<SVNHistory> scmBuildFrom,
            SCMBuildView<SVNHistory> scmBuildTo, SVNChangeLogRevisions revisions) {
        super(uuid, branch, scmBranch, scmBuildFrom, scmBuildTo);
        this.revisions = revisions;
    }

    public SVNChangeLog(
            String uuid,
            Branch branch,
            SVNRepository scmBranch,
            SCMBuildView<SVNHistory> scmBuildFrom,
            SCMBuildView<SVNHistory> scmBuildTo) {
        this(uuid, branch, scmBranch, scmBuildFrom, scmBuildTo, null);
    }

    public SVNChangeLog withRevision(SVNChangeLogRevisions revisions) {
        return new SVNChangeLog(
                getUuid(),
                getBranch(),
                getScmBranch(),
                getScmBuildFrom(),
                getScmBuildTo(),
                revisions
        );
    }
}
