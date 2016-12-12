package net.nemerosa.ontrack.extension.gitlab;

import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClient;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

/**
 * Well, we do not claim to check if GitLab is down or up, but just to see if we can connect
 * to it...
 */
@Component
public class GitLabHealthIndicator extends ConfigurationHealthIndicator<GitLabConfiguration> {

    private final OntrackGitLabClientFactory gitLabClientFactory;

    @Autowired
    public GitLabHealthIndicator(
            ConfigurationService<GitLabConfiguration> configurationService,
            SecurityService securityService,
            HealthAggregator healthAggregator,
            OntrackGitLabClientFactory gitLabClientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.gitLabClientFactory = gitLabClientFactory;
    }

    @Override
    protected Health getHealth(GitLabConfiguration config) {
        try {
            // Gets the client
            OntrackGitLabClient client = gitLabClientFactory.create(config);
            // Gets the list of repositories
            client.getRepositories();
            // OK
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
