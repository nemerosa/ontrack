package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

@Component
public class JenkinsHealthIndicator extends ConfigurationHealthIndicator<JenkinsConfiguration> {

    private final JenkinsClientFactory jenkinsClientFactory;

    @Autowired
    public JenkinsHealthIndicator(ConfigurationService<JenkinsConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, JenkinsClientFactory jenkinsClientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.jenkinsClientFactory = jenkinsClientFactory;
    }

    @Override
    protected Health getHealth(JenkinsConfiguration config) {
        try {
            jenkinsClientFactory.getClient(config).getInfo();
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
