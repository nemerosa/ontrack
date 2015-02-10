package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JenkinsHealthIndicator implements HealthIndicator {

    private final JenkinsConfigurationService configurationService;
    private final SecurityService securityService;
    private final HealthAggregator healthAggregator;
    private final JenkinsClientFactory jenkinsClientFactory;

    @Autowired
    public JenkinsHealthIndicator(JenkinsConfigurationService configurationService, SecurityService securityService, HealthAggregator healthAggregator, JenkinsClientFactory jenkinsClientFactory) {
        this.configurationService = configurationService;
        this.securityService = securityService;
        this.healthAggregator = healthAggregator;
        this.jenkinsClientFactory = jenkinsClientFactory;
    }

    @Override
    public Health health() {
        return securityService.asAdmin(() -> {
            Map<String, Health> healths = configurationService.getConfigurations().stream()
                    .collect(
                            Collectors.toMap(
                                    JenkinsConfiguration::getName,
                                    this::getHealth
                            )
                    );
            return healthAggregator.aggregate(healths);
        });
    }

    private Health getHealth(JenkinsConfiguration config) {
        try {
            jenkinsClientFactory.getClient(config).getInfo();
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
