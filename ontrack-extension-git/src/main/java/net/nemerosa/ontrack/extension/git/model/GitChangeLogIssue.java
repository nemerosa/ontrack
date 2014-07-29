package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogIssue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class GitChangeLogIssue extends SCMChangeLogIssue {

    private final List<GitUICommit> commits;

    protected GitChangeLogIssue(Issue issue, List<GitUICommit> commits) {
        super(issue);
        this.commits = commits;
    }

    public static GitChangeLogIssue of(Issue issue, GitUICommit uiCommit) {
        return new GitChangeLogIssue(
                issue,
                new ArrayList<>(
                        Collections.singletonList(uiCommit)
                )
        );
    }

    public GitChangeLogIssue add(GitUICommit uiCommit) {
        commits.add(uiCommit);
        return this;
    }

}
