package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class GitIssueNotFoundException extends NotFoundException {

    public GitIssueNotFoundException(String key) {
        super("Issue key not found: %s", key);
    }
}
