package net.nemerosa.ontrack.extension.git.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GitChangeLogFile implements SCMChangeLogFile {

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

    @Override
    public String getPath() {
        return StringUtils.isNotBlank(oldPath) ? oldPath : newPath;
    }
}
