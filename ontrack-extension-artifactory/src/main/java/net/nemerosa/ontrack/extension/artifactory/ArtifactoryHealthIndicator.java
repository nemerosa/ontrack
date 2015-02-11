package net.nemerosa.ontrack.extension.artifactory;

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.extension.support.ConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtifactoryHealthIndicator extends ConfigurationHealthIndicator<ArtifactoryConfiguration> {

    private final ArtifactoryClientFactory clientFactory;

    @Autowired
    public ArtifactoryHealthIndicator(ConfigurationService<ArtifactoryConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, ArtifactoryClientFactory clientFactory) {
        super(configurationService, securityService, healthAggregator);
        this.clientFactory = clientFactory;
    }

    @Override
    protected Health getHealth(ArtifactoryConfiguration config) {
        try {
            List<String> buildNames = clientFactory.getClient(config).getBuildNames();
            return Health.up().withDetail("buildNames", buildNames).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
