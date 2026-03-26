package net.nemerosa.ontrack.git

import net.nemerosa.ontrack.git.support.GitConnectionConfig

@Deprecated("Will be removed in V6. Always use native methods of the SCM.")
interface GitRepositoryClientFactory {

    /**
     * Gets the Git client for a given remote repository and the associated credentials.
     */
    fun getClient(repository: GitRepository, gitConnectionConfig: GitConnectionConfig): GitRepositoryClient

    /**
     * Resets all repositories, forcing them to be closed again.
     */
    fun reset()

}
