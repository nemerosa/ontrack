package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationService;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.stream.Collectors;

/**
 * @deprecated Must be replaced by {@link ConfigurationConnectorStatusIndicator}
 */
@Deprecated
public abstract class ConfigurationHealthIndicator<T extends UserPasswordConfiguration> implements HealthIndicator {

    private final ConfigurationService<T> configurationService;
    private final SecurityService securityService;
    private final HealthAggregator healthAggregator;

    protected ConfigurationHealthIndicator(ConfigurationService<T> configurationService, SecurityService securityService, HealthAggregator healthAggregator) {
        this.configurationService = configurationService;
        this.securityService = securityService;
        this.healthAggregator = healthAggregator;
    }

    @Override
    public Health health() {
        return healthAggregator.aggregate(
                securityService.asAdmin(() ->
                        configurationService.getConfigurations().stream()
                                .collect(
                                        Collectors.toMap(
                                                UserPasswordConfiguration::getName,
                                                this::getHealth
                                        )
                                )
                )
        );
    }

    protected abstract Health getHealth(T config);
}
