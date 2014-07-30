package net.nemerosa.ontrack.extension.git.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GitChangeLogFile {

    private final SCMChangeLogFileChangeType changeType;
    private final String oldPath;
    private final String newPath;
    private final String url;

    public static GitChangeLogFile of(SCMChangeLogFileChangeType changeType, String path) {
        return new GitChangeLogFile(changeType, path, null, "");
    }

    public static GitChangeLogFile of(SCMChangeLogFileChangeType changeType, String oldPath, String newPath) {
        return new GitChangeLogFile(changeType, oldPath, newPath, "");
    }

    public GitChangeLogFile withUrl(String url) {
        return new GitChangeLogFile(changeType, oldPath, newPath, url);
    }
}
