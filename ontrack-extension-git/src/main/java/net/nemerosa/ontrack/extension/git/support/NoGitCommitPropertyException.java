package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.common.BaseException;

public class NoGitCommitPropertyException extends BaseException {
    public NoGitCommitPropertyException(String build) {
        super("No Git Commit property found on build %s", build);
    }
}
