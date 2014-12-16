package net.nemerosa.ontrack.git.exceptions;

import java.io.IOException;

public class GitRepositoryInitException extends GitRepositoryException {
    public GitRepositoryInitException(IOException e) {
        super(e, "Cannot initialise Git repository.");
    }
}
