package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class GitCommitNotFoundException extends NotFoundException {

    public GitCommitNotFoundException(String commit) {
        super("Commit not found: %s", commit);
    }
}
