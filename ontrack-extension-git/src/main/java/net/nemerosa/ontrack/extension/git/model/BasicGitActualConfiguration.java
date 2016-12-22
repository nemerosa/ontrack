package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.model.support.UserPassword;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Git configuration based on direct definition.
 */
public class BasicGitActualConfiguration implements GitConfiguration {

    /**
     * Configuration property
     */
    private final BasicGitConfiguration configuration;

    /**
     * Issue service
     */
    private final ConfiguredIssueService configuredIssueService;

    public BasicGitActualConfiguration(BasicGitConfiguration configuration, ConfiguredIssueService configuredIssueService) {
        this.configuration = configuration;
        this.configuredIssueService = configuredIssueService;
    }

    public static GitConfiguration of(BasicGitConfiguration basicGitConfiguration) {
        return new BasicGitActualConfiguration(
                basicGitConfiguration,
                null
        );
    }

    @Override
    public String getType() {
        return BasicGitConfiguration.TYPE;
    }

    @Override
    public String getName() {
        return configuration.getName();
    }

    @Override
    public String getRemote() {
        return configuration.getRemote();
    }

    @Override
    public Optional<UserPassword> getCredentials() {
        String user = configuration.getUser();
        String password = configuration.getPassword();
        return StringUtils.isNotBlank(user) ?
                Optional.of(new UserPassword(user, password)) :
                Optional.empty();
    }

    @Override
    public String getCommitLink() {
        return configuration.getCommitLink();
    }

    @Override
    public String getFileAtCommitLink() {
        return configuration.getFileAtCommitLink();
    }

    @Override
    public int getIndexationInterval() {
        return configuration.getIndexationInterval();
    }

    @Override
    public Optional<ConfiguredIssueService> getConfiguredIssueService() {
        return Optional.ofNullable(configuredIssueService);
    }
}
