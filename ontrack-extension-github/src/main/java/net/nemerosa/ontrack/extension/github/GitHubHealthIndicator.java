package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
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

    // private static final Logger logger = LoggerFactory.getLogger(GitHubHealthIndicator.class);

    private final GitRepositoryClientFactory repositoryClientFactory;

    @Autowired
    public GitHubHealthIndicator(ConfigurationService<GitHubEngineConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, GitRepositoryClientFactory repositoryClientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.repositoryClientFactory = repositoryClientFactory;
    }

    @Override
    protected Health getHealth(GitHubEngineConfiguration config) {
        try {
            // FIXME Gets the list of repositories?
//            GitRepositoryClient client = repositoryClientFactory.getClient(config.getGitRepository());
//            client.sync(logger::debug);
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
