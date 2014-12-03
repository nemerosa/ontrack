package net.nemerosa.ontrack.git;

import java.util.function.Consumer;

/**
 * Defines a client for a Git repository.
 */
public interface GitRepositoryClient {

    /**
     * Makes sure the repository is synchronised with its remote location.
     *
     * @param logger Used to log messages during the synchronisation
     */
    void sync(Consumer<String> logger);

}
