package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SVNHealthIndicator implements HealthIndicator {

    private final SVNConfigurationService configurationService;
    private final SVNService svnService;
    private final SVNClient svnClient;
    private final TransactionService transactionService;
    private final SecurityService securityService;
    private final HealthAggregator healthAggregator;

    @Autowired
    public SVNHealthIndicator(SVNConfigurationService configurationService, SVNService svnService, SVNClient svnClient, TransactionService transactionService, SecurityService securityService, HealthAggregator healthAggregator) {
        this.configurationService = configurationService;
        this.svnService = svnService;
        this.svnClient = svnClient;
        this.securityService = securityService;
        this.healthAggregator = healthAggregator;
        this.transactionService = transactionService;
    }

    @Override
    public Health health() {
        return securityService.asAdmin(() -> {
            // Gets the status for all configurations
            Map<String, Health> healths = configurationService.getConfigurations().stream()
                    .collect(Collectors.toMap(
                            SVNConfiguration::getName,
                            this::getConfigurationHealth
                    ));
            // Aggregates the healths
            return healthAggregator.aggregate(healths);
        });
    }

    private Health getConfigurationHealth(SVNConfiguration config) {
        // Just gets the latest revision
        SVNRepository repository = svnService.getRepository(config.getName());
        try (Transaction ignored = transactionService.start()) {
            SVNURL url = SVNUtils.toURL(repository.getConfiguration().getUrl());
            Health.Builder builder = Health.unknown();
            try {
                long repositoryRevision = svnClient.getRepositoryRevision(repository, url);
                return builder.up().withDetail("revision", repositoryRevision).build();
            } catch (Exception ex) {
                return builder.down(ex).build();
            }
        }
    }

}
