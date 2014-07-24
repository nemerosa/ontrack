package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitException extends BaseException {
    public GitException(GitAPIException e) {
        super(e, "Git API exception");
    }
}
