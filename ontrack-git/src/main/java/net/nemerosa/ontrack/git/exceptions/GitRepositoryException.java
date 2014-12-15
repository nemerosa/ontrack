package net.nemerosa.ontrack.git.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public abstract class GitRepositoryException extends BaseException {

    public GitRepositoryException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }

    public GitRepositoryException(Exception ex, String pattern, Object... parameters) {
        super(ex, pattern, parameters);
    }
}
