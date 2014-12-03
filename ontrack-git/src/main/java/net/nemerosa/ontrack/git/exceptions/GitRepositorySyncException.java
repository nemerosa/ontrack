package net.nemerosa.ontrack.git.exceptions;

public class GitRepositorySyncException extends GitRepositoryException {
    public GitRepositorySyncException() {
        super("Cannot sync repository.");
    }
}
