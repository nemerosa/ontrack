package net.nemerosa.ontrack.extension.svn.indexation;

import net.nemerosa.ontrack.extension.svn.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRevisionDao;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IndexationServiceImpl implements IndexationService {

    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final TransactionTemplate transactionTemplate;
    private final SVNConfigurationService configurationService;
    private final SVNRepositoryDao repositoryDao;
    private final SVNRevisionDao revisionDao;
    private final SVNClient svnClient;
    private final SecurityService securityService;
    private final TransactionService transactionService;

    /**
     * Current indexations
     */
    private final Map<String, IndexationJob> indexationJobs = new ConcurrentHashMap<>();

    @Autowired
    public IndexationServiceImpl(
            TransactionTemplate transactionTemplate,
            SVNConfigurationService configurationService,
            SVNRepositoryDao repositoryDao,
            SVNRevisionDao revisionDao,
            SVNClient svnClient,
            SecurityService securityService,
            TransactionService transactionService
    ) {
        this.transactionTemplate = transactionTemplate;
        this.configurationService = configurationService;
        this.repositoryDao = repositoryDao;
        this.revisionDao = revisionDao;
        this.svnClient = svnClient;
        this.securityService = securityService;
        this.transactionService = transactionService;
    }

    @Override
    public boolean isIndexationRunning(String name) {
        IndexationJob job = indexationJobs.get(name);
        return job != null && job.isRunning();
    }

    @Override
    public void reindex(String name) {
        // Gets the repository if it exists
        Integer repositoryId = repositoryDao.findByName(name);
        // If it exists, delete it
        if (repositoryId != null) {
            repositoryDao.delete(repositoryId);
        }
        // Creates the repository entry
        repositoryId = repositoryDao.create(name);
        // Gets the configuration
        SVNRepository repository = SVNRepository.of(repositoryId, configurationService.getConfiguration(name));
        // OK, launches a new indexation
        indexFromLatest(repository);
    }

    private void indexFromLatest(SVNRepository repository) {
        // FIXME Checks the SVNIndexation global function provided by the SVN extension
        try (Transaction ignored = transactionService.start()) {
            // Loads the repository information
            SVNURL url = SVNUtils.toURL(repository.getConfiguration().getUrl());
            // Last scanned revision
            long lastScannedRevision = revisionDao.getLast(repository.getId());
            if (lastScannedRevision <= 0) {
                lastScannedRevision = repository.getConfiguration().getIndexationStart();
            }
            // Logging
            logger.info("[svn-indexation] Repository={}, LastScannedRevision={}", repository.getId(), lastScannedRevision);
            // HEAD revision
            long repositoryRevision = svnClient.getRepositoryRevision(repository, url);
            // FIXME Request index of the range
            // indexRange(repositoryId, lastScannedRevision + 1, repositoryRevision);
        }

    }
}
