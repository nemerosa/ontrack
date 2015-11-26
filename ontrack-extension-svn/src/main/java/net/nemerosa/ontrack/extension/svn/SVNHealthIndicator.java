package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.support.ConfigurationHealthIndicator;
import net.nemerosa.ontrack.model.support.ConfigurationService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNURL;

@Component
public class SVNHealthIndicator extends ConfigurationHealthIndicator<SVNConfiguration> {

    private final SVNService svnService;
    private final TransactionService transactionService;
    private final SVNClient svnClient;

    @Autowired
    public SVNHealthIndicator(ConfigurationService<SVNConfiguration> configurationService, SecurityService securityService, HealthAggregator healthAggregator, SVNService svnService, TransactionService transactionService, SVNClient svnClient) {
        super(configurationService, securityService, healthAggregator);
        this.svnService = svnService;
        this.transactionService = transactionService;
        this.svnClient = svnClient;
    }

    @Override
    protected Health getHealth(SVNConfiguration config) {
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
