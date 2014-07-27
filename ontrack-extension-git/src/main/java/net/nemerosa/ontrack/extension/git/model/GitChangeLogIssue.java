package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class GitChangeLogIssue {

    private final Issue issue;
    private final List<GitUICommit> commits;

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
