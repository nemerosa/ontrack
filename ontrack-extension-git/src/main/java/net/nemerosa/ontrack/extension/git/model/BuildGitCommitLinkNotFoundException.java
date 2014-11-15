package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.common.BaseException;

public class BuildGitCommitLinkNotFoundException extends BaseException {
    public BuildGitCommitLinkNotFoundException(String id) {
        super("Build Git commit link not found: %s", id);
    }
}
