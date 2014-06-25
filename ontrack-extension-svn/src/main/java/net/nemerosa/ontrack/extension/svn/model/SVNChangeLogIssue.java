package net.nemerosa.ontrack.extension.svn.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNChangeLogIssue {

    private final Issue issue;
    private final List<SVNRevisionInfo> revisions;

    public SVNChangeLogIssue(Issue issue) {
        this(issue, Collections.<SVNRevisionInfo>emptyList());
    }

    public SVNChangeLogIssue addRevision(SVNRevisionInfo revision) {
        List<SVNRevisionInfo> list = new ArrayList<>(this.revisions);
        list.add(revision);
        return new SVNChangeLogIssue(issue, list);
    }

    public SVNRevisionInfo getLastRevision() {
        if (revisions != null && !revisions.isEmpty()) {
            return revisions.get(revisions.size() - 1);
        } else {
            return null;
        }
    }
}
