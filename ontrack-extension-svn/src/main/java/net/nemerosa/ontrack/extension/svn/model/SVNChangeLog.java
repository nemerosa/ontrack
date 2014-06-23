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
    private SVNChangeLogRevisions revisions;
    @JsonIgnore // Not sent to the client
    private SVNChangeLogIssues issues;

    public SVNChangeLog(
            String uuid,
            Branch branch,
            SVNRepository scmBranch,
            SCMBuildView<SVNHistory> scmBuildFrom,
            SCMBuildView<SVNHistory> scmBuildTo) {
        super(uuid, branch, scmBranch, scmBuildFrom, scmBuildTo);
    }

    public SVNChangeLog withRevisions(SVNChangeLogRevisions revisions) {
        this.revisions = revisions;
        return this;
    }

    public SVNChangeLog withIssues(SVNChangeLogIssues issues) {
        this.issues = issues;
        return this;
    }
}
