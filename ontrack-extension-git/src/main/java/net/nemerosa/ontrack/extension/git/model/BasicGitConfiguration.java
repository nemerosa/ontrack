package net.nemerosa.ontrack.extension.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.support.UserPassword;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Git configuration based on direct definition.
 */
@Data
@AllArgsConstructor
public class BasicGitConfiguration implements GitConfiguration {

    /**
     * Name of this configuration
     */
    @Wither
    private final String name;

    /**
     * Remote path to the source repository
     */
    @Wither
    private final String remote;

    /**
     * User name
     */
    @Wither
    private final String user;

    /**
     * User password
     */
    @Wither
    private final String password;

    /**
     * Link to a commit, using {commit} as placeholder
     */
    @Wither
    private final String commitLink;

    /**
     * Link to a file at a given commit, using {commit} and {path} as placeholders
     */
    @Wither
    private final String fileAtCommitLink;

    /**
     * Indexation interval
     */
    @Wither
    private final int indexationInterval;

    /**
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    @Wither
    private final String issueServiceConfigurationIdentifier;

    @Override
    public String getType() {
        return "basic";
    }

    @Override
    public Optional<UserPassword> getCredentials() {
        return StringUtils.isNotBlank(user) ?
                Optional.of(new UserPassword(user, password)) :
                Optional.empty();
    }
}
