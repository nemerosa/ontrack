package net.nemerosa.ontrack.extension.svn.service;

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
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(SVNConfigurationService.class);

    private final SVNRepositoryDao repositoryDao;
    private final OntrackConfigProperties ontrackConfigProperties;
    private final TransactionService transactionService;
    private final SVNClient svnClient;

    @Autowired
    public SVNConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, SVNRepositoryDao repositoryDao, EventPostService eventPostService, EventFactory eventFactory, OntrackConfigProperties ontrackConfigProperties, TransactionService transactionService, SVNClient svnClient) {
        super(SVNConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
        this.repositoryDao = repositoryDao;
        this.ontrackConfigProperties = ontrackConfigProperties;
        this.transactionService = transactionService;
        this.svnClient = svnClient;
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
    protected ConnectionResult validate(SVNConfiguration configuration) {
        // No trailing slash
        String url = configuration.getUrl();
        if (StringUtils.endsWith(url, "/")) {
            throw new SVNURLFormatException(
                    "The Subversion URL must not end with a slash: %s",
                    url
            );
        }
        if (ontrackConfigProperties.isConfigurationTest()) {
            //noinspection unused
            try (Transaction tx = transactionService.start()) {
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
        } else {
            logger.warn("[svn] SVN configuration URL checks have been disabled.");
            return ConnectionResult.ok();
        }
    }
}
