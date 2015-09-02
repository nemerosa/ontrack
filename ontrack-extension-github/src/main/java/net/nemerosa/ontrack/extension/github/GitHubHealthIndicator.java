package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

/**
 * Well, we do not claim to check if GitHub is down or up, but just to see if we can connect
 * to it...
 */
@Component
public class GitHubHealthIndicator extends ConfigurationHealthIndicator<GitHubEngineConfiguration> {

    private final OntrackGitHubClientFactory gitHubClientFactory;

    @Autowired
    public GitHubHealthIndicator(
            ConfigurationService<GitHubEngineConfiguration> configurationService,
            SecurityService securityService,
            HealthAggregator healthAggregator,
            OntrackGitHubClientFactory gitHubClientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.gitHubClientFactory = gitHubClientFactory;
    }

    @Override
    protected Health getHealth(GitHubEngineConfiguration config) {
        try {
            // Gets the client
            OntrackGitHubClient client = gitHubClientFactory.create(config);
            // Gets the list of repositories
            client.getRepositories();
            // OK
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
