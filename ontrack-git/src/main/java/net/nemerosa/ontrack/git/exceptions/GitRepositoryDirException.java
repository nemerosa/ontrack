package net.nemerosa.ontrack.git.exceptions;

import java.io.File;
import java.io.IOException;

public class GitRepositoryDirException extends GitRepositoryException {

    public GitRepositoryDirException(File dir, IOException ex) {
        super(ex, "Cannot prepare repository directory at %s", dir.getPath());
    }

}
