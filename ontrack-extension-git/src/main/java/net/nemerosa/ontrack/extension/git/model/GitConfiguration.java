package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.model.support.UserPassword;

import java.util.Optional;

/**
 * Definition of a Git configuration.
 */
public interface GitConfiguration {

    /**
     * Type
     */
    String getType();

    /**
     * Name in the type
     */
    String getName();

    /**
     * Remote URL-ish
     */
    String getRemote();

    /**
     * Credentials
     */
    Optional<UserPassword> getCredentials();

    /**
     * Link to a commit, using {commit} as placeholder
     */
    String getCommitLink();

    /**
     * Link to a file at a given commit, using {commit} and {path} as placeholders
     */
    String getFileAtCommitLink();

    /**
     * Indexation interval
     */
    int getIndexationInterval();

    /**
     * Gets the associated issue service configuration (if any)
     */
    Optional<ConfiguredIssueService> getConfiguredIssueService();

    /**
     * Gets the Git repository for this configuration
     */
    @JsonIgnore
    default GitRepository getGitRepository() {
        Optional<UserPassword> credentials = getCredentials();
        return new GitRepository(
                getType(),
                getName(),
                getRemote(),
                credentials.map(UserPassword::getUser).orElse(""),
                credentials.map(UserPassword::getPassword).orElse("")
        );
    }

}
