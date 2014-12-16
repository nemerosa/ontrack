package net.nemerosa.ontrack.git;

public interface GitRepositoryClientFactory {

    /**
     * Gets the Git client for a given remote repository and the associated credentials.
     */
    GitRepositoryClient getClient(GitRepository repository);

}
