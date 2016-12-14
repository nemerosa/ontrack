package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
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
    private final GitProjectConfigurationProperty property;

    /**
     * Issue service
     */
    private final ConfiguredIssueService configuredIssueService;

    public BasicGitActualConfiguration(GitProjectConfigurationProperty property, ConfiguredIssueService configuredIssueService) {
        this.property = property;
        this.configuredIssueService = configuredIssueService;
    }

    @Override
    public String getType() {
        return "basic";
    }

    @Override
    public String getName() {
        return property.getConfiguration().getName();
    }

    @Override
    public String getRemote() {
        return property.getConfiguration().getRemote();
    }

    @Override
    public Optional<UserPassword> getCredentials() {
        String user = property.getConfiguration().getUser();
        String password = property.getConfiguration().getPassword();
        return StringUtils.isNotBlank(user) ?
                Optional.of(new UserPassword(user, password)) :
                Optional.empty();
    }

    @Override
    public String getCommitLink() {
        return property.getConfiguration().getCommitLink();
    }

    @Override
    public String getFileAtCommitLink() {
        return property.getConfiguration().getFileAtCommitLink();
    }

    @Override
    public int getIndexationInterval() {
        return property.getConfiguration().getIndexationInterval();
    }

    @Override
    public Optional<ConfiguredIssueService> getConfiguredIssueService() {
        return Optional.ofNullable(configuredIssueService);
    }
}
