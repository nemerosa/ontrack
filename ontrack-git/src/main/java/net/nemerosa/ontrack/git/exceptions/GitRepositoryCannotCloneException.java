package net.nemerosa.ontrack.git.exceptions;

public class GitRepositoryCannotCloneException extends GitRepositoryException {
    public GitRepositoryCannotCloneException(String remote) {
        super("Repository for %s cloning did not succeed.", remote);
    }
}
