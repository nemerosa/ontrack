package net.nemerosa.ontrack.extension.git.client;

import lombok.Data;
import org.eclipse.jgit.revwalk.RevCommit;

@Data
public class GitDiffEntry {

    private final GitChangeType changeType;
    private final String oldPath;
    private final String newPath;

    public String getReferencePath() {
        switch (changeType) {
            case DELETE:
                return oldPath;
            case MODIFY:
            case RENAME:
            case COPY:
            case ADD:
            default:
                return newPath;
        }
    }

    public String getReferenceId(RevCommit from, RevCommit to) {
        switch (changeType) {
            case DELETE:
                return from.getId().name();
            case MODIFY:
            case RENAME:
            case COPY:
            case ADD:
            default:
                return to.getId().name();
        }
    }
}
