package net.nemerosa.ontrack.git

interface GitRepositoryClientFactory {

    /**
     * Gets the Git client for a given remote repository and the associated credentials.
     */
    fun getClient(repository: GitRepository): GitRepositoryClient

    /**
     * Resets all repositories, forcing them to be closed again.
     */
    fun reset()

}
