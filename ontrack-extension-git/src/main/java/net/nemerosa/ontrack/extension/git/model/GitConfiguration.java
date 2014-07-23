package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

@Data
public class GitConfiguration implements Configuration<GitConfiguration> {

    /**
     * Name of this configuration
     */
    private final String name;

    /**
     * Remote path to the source repository
     */
    private final String remote;

    /**
     * User name
     */
    private final String user;

    /**
     * User password
     */
    private final String password;

    /**
     * Link to a commit, using {commit} as placeholder
     */
    private final String commitLink;

    /**
     * Link to a file at a given commit, using {commit} and {path} as placeholders
     */
    private final String fileAtCommitLink;

    /**
     * Indexation interval
     */
    private final int indexationInterval;

    /**
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    private final String issueServiceConfigurationIdentifier;

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(name, remote);
    }

    @Override
    public GitConfiguration obfuscate() {
        return this;
    }

}
