package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.model.support.ConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JIRAHealthIndicator extends ConfigurationHealthIndicator<JIRAConfiguration> {

    private final JIRASessionFactory sessionFactory;

    @Autowired
    public JIRAHealthIndicator(ConfigurationService<JIRAConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, JIRASessionFactory sessionFactory) {
        super(configurationService, securityService, healthAggregator);
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Health getHealth(JIRAConfiguration config) {
        try (JIRAClient client = sessionFactory.create(config).getClient()) {
            List<String> projects = client.getProjects();
            return Health.up().withDetail("projects", projects).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
