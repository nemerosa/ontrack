package net.nemerosa.ontrack.extension.svn.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchRevision {
    private final String path;
    private final long revision;
    private final boolean merge;
    private final boolean complete;

    public BranchRevision(String path, long revision, boolean merge) {
        this(path, revision, merge, false);
    }

    public BranchRevision complete() {
        return new BranchRevision(path, revision, merge, true);
    }

    public static boolean areComplete(Collection<BranchRevision> branchRevisions) {
        return branchRevisions.stream().allMatch(BranchRevision::isComplete);
    }
}
