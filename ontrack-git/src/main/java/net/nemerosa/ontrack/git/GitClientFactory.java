package net.nemerosa.ontrack.git;

public interface GitClientFactory {

    /**
     * Gets the Git client for a given remote repository and the associated credentials.
     */
    GitClient getClient(GitRepository repository, GitCredentialsProvider credentialsProvider);

}
