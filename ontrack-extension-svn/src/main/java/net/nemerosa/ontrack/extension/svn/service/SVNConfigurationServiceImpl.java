package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.model.SVNURLFormatException;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.ConfigurationValidationException;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Objects;

@Service
@Transactional
public class SVNConfigurationServiceImpl extends AbstractConfigurationService<SVNConfiguration> implements SVNConfigurationService {

    private final SVNRepositoryDao repositoryDao;
    private final TransactionService transactionService;
    private final SVNClient svnClient;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public SVNConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, SVNRepositoryDao repositoryDao, EventPostService eventPostService, EventFactory eventFactory, OntrackConfigProperties ontrackConfigProperties, TransactionService transactionService, SVNClient svnClient, IssueServiceRegistry issueServiceRegistry) {
        super(SVNConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
        this.repositoryDao = repositoryDao;
        this.transactionService = transactionService;
        this.svnClient = svnClient;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public void deleteConfiguration(String name) {
        super.deleteConfiguration(name);
        Integer id = repositoryDao.findByName(name);
        if (id != null) {
            repositoryDao.delete(id);
        }
    }

    @Override
    protected void validateAndCheck(SVNConfiguration configuration) {
        super.validateAndCheck(configuration);
        // Checks the issue service identifier
        String issueServiceConfigurationIdentifier = configuration.getIssueServiceConfigurationIdentifier();
        if (StringUtils.isNotBlank(issueServiceConfigurationIdentifier)) {
            ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(issueServiceConfigurationIdentifier);
            if (configuredIssueService == null || configuredIssueService.getIssueServiceConfiguration() == null) {
                throw new ConfigurationValidationException(
                        configuration,
                        String.format(
                                "Issue service configuration cannot be validated: %s",
                                issueServiceConfigurationIdentifier
                        )
                );
            }
        }
    }

    @Override
    protected ConnectionResult validate(SVNConfiguration configuration) {
        // No trailing slash
        String url = configuration.getUrl();
        if (StringUtils.endsWith(url, "/")) {
            throw new SVNURLFormatException(
                    "The Subversion URL must not end with a slash: %s",
                    url
            );
        }
        try (Transaction ignored = transactionService.start()) {
            // Creates a repository
            SVNRepository repository = SVNRepository.of(
                    0,
                    configuration,
                    null
            );
            // Configuration URL
            SVNURL svnurl = SVNUtils.toURL(configuration.getUrl());
            // Connection to the root
            if (!svnClient.exists(
                    repository,
                    svnurl,
                    SVNRevision.HEAD
            )) {
                return ConnectionResult.error(configuration.getUrl() + " does not exist.");
            }
            // Gets base info
            SVNInfo info = svnClient.getInfo(repository, svnurl, SVNRevision.HEAD);
            // Checks the repository root
            if (!Objects.equals(
                    info.getRepositoryRootURL(),
                    svnurl
            )) {
                return ConnectionResult.error(configuration.getUrl() + " must be the root of the repository.");
            }
            // OK
            return ConnectionResult.ok();
        } catch (Exception ex) {
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
