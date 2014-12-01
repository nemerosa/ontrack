package net.nemerosa.ontrack.git;

import java.util.Optional;

/**
 * Used to provide credentials when connecting to a Git remote repository.
 */
@FunctionalInterface
public interface GitCredentialsProvider {

    Optional<GitCredentials> getCredentials();

}
