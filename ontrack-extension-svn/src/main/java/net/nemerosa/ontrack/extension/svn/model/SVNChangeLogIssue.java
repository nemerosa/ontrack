package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNChangeLogIssue extends SCMChangeLogIssue {

    private final List<SVNRevisionInfo> revisions;

    protected SVNChangeLogIssue(Issue issue, List<SVNRevisionInfo> revisions) {
        super(issue);
        this.revisions = revisions;
    }

    public SVNChangeLogIssue(Issue issue) {
        this(issue, Collections.<SVNRevisionInfo>emptyList());
    }

    public SVNChangeLogIssue addRevision(SVNRevisionInfo revision) {
        List<SVNRevisionInfo> list = new ArrayList<>(this.revisions);
        list.add(revision);
        return new SVNChangeLogIssue(getIssue(), list);
    }

    public SVNRevisionInfo getLastRevision() {
        if (revisions != null && !revisions.isEmpty()) {
            return revisions.get(revisions.size() - 1);
        } else {
            return null;
        }
    }
}
