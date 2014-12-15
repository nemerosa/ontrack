package net.nemerosa.ontrack.git.exceptions;

import org.eclipse.jgit.api.errors.GitAPIException;

public class GitRepositoryAPIException extends GitRepositoryException {
    public GitRepositoryAPIException(String remote, GitAPIException ex) {
        super(ex, "Git API exception on %s", remote);
    }
}
