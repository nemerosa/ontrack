package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;

import java.util.concurrent.ExecutionException;

public class GitRepositoryManagerException extends BaseException {
    public GitRepositoryManagerException(String remote, ExecutionException e) {
        super(e, "Cannot get the repository manager for remote at %s.", remote);
    }
}
