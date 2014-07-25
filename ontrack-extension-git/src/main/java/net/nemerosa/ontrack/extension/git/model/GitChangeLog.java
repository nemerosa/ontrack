package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog;
import net.nemerosa.ontrack.model.structure.Branch;

@EqualsAndHashCode(callSuper = false)
@Data
public class GitChangeLog extends SCMChangeLog<GitConfiguration, GitBuildInfo> {

    @JsonIgnore // Not sent to the client
    private GitChangeLogCommits commits;

    public GitChangeLog(
            String uuid,
            Branch branch,
            GitConfiguration configuration,
            SCMBuildView<GitBuildInfo> scmBuildFrom,
            SCMBuildView<GitBuildInfo> scmBuildTo) {
        super(uuid, branch, configuration, scmBuildFrom, scmBuildTo);
    }

    public GitChangeLog withRevisions(GitChangeLogCommits commits) {
        this.commits = commits;
        return this;
    }

}
