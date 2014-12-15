package net.nemerosa.ontrack.git.exceptions;

import java.io.IOException;

public class GitRepositoryIOException extends GitRepositoryException {
    public GitRepositoryIOException(String remote, IOException ex) {
        super(ex, "Git IO exception on %s", remote);
    }
}
