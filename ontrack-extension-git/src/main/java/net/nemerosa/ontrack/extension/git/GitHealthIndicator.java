package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.model.support.ConfigurationService;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

@Component
public class GitHealthIndicator extends ConfigurationHealthIndicator<BasicGitConfiguration> {

    private final GitRepositoryClientFactory repositoryClientFactory;

    @Autowired
    public GitHealthIndicator(ConfigurationService<BasicGitConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, GitRepositoryClientFactory repositoryClientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.repositoryClientFactory = repositoryClientFactory;
    }

    @Override
    protected Health getHealth(BasicGitConfiguration config) {
        try {
            GitRepositoryClient client = repositoryClientFactory.getClient(config.asGitRepository());
            client.getRemoteBranches();
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }

}
