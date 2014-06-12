package net.nemerosa.ontrack.extension.svn.indexation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.extension.svn.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRevisionDao;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.Time;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    /**
     * Runner for the indexation jobs
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("Indexation %s")
                    .build()
    );

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
            // Request index of the range
            indexRange(repository, lastScannedRevision + 1, repositoryRevision);
        }

    }

    private void indexRange(SVNRepository repository, Long from, Long to) {
        logger.info("[svn-indexation] Repository={}, Range={}->{}", repository.getConfiguration().getName(), from, to);
        long min;
        long max;
        if (from == null) {
            min = max = to;
        } else if (to == null) {
            min = max = from;
        } else {
            min = Math.min(from, to);
            max = Math.max(from, to);
        }
        // Indexation job
        DefaultIndexationJob job = new DefaultIndexationJob(repository, min, max);
        indexationJobs.put(repository.getConfiguration().getName(), job);
        // Schedule the scan
        executor.submit(job);
    }

    private static interface IndexationListener {

        void setRevision(long revision);

    }

    private class DefaultIndexationJob implements IndexationJob, Runnable, IndexationListener {

        private final SVNRepository repository;
        private final long min;
        private final long max;
        private boolean running;
        private long current;

        private DefaultIndexationJob(SVNRepository repository, long min, long max) {
            this.repository = repository;
            this.min = min;
            this.max = max;
            this.current = min;
        }

        @Override
        public SVNRepository getRepository() {
            return repository;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public long getMin() {
            return min;
        }

        @Override
        public long getMax() {
            return max;
        }

        @Override
        public long getCurrent() {
            return current;
        }

        @Override
        public int getProgress() {
            double value = (current - min) / (double) (max - min);
            return (int) (value * 100);
        }

        @Override
        public void run() {
            try {
                running = true;
                index(repository, min, max, this);
            } catch (Exception ex) {
                logger.error(String.format("Could not index range from %s to %s", min, max), ex);
            } finally {
                indexationJobs.remove(repository.getId());
            }
        }

        @Override
        public void setRevision(long revision) {
            this.current = revision;
        }
    }

    private class IndexationHandler implements ISVNLogEntryHandler {

        private final SVNRepository repository;
        private final IndexationListener indexationListener;

        public IndexationHandler(SVNRepository repository, IndexationListener indexationListener) {
            this.repository = repository;
            this.indexationListener = indexationListener;
        }

        @Override
        public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
            // Transaction
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    try {
                        indexationListener.setRevision(logEntry.getRevision());
                        indexInTransaction(repository, logEntry);
                    } catch (Exception ex) {
                        logger.error("Cannot index revision " + logEntry.getRevision(), ex);
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    /**
     * This method is executed within a transaction
     */
    private void indexInTransaction(SVNRepository repository, SVNLogEntry logEntry) throws SVNException {
        // Log values
        long revision = logEntry.getRevision();
        String author = logEntry.getAuthor();
        String message = logEntry.getMessage();
        Date date = logEntry.getDate();
        // Sanitizes the possible null values
        author = Objects.toString(author, "");
        message = Objects.toString(message, "");
        // Date to date time
        LocalDateTime dateTime = Time.from(date, Time.now());
        // Branch for the revision
        // TODO String branch = getBranchForRevision(repository, logEntry);
        // Logging
        logger.info(String.format("Indexing revision %d", revision));
        // Inserting or updating the revision
        // TODO revisionDao.addRevision(repository.getId(), revision, author, dateTime, message, branch);
        // Merge relationships (using a nested SVN client)
        try (Transaction ignored = transactionService.start(true)) {
            // TODO List<Long> mergedRevisions = subversionService.getMergedRevisions(repository, SVNUtils.toURL(repository.getUrl(), branch), revision);
            // TODO revisionDao.addMergedRevisions(repository.getId(), revision, mergedRevisions);
        }
        // Subversion events
        // TODO indexSVNEvents(repository, logEntry);
        // Indexes the issues
        // TODO indexIssues(repository, logEntry);
    }

    /**
     * Indexation of a range in a thread for one repository - since it is called by a single thread executor, we can
     * be sure that only one call of this method is running at one time for one given repository.
     */
    protected void index(SVNRepository repository, long from, long to, IndexationListener indexationListener) {
        // Ordering
        if (from > to) {
            long t = from;
            from = to;
            to = t;
        }

        // Opens a transaction
        try (Transaction ignored = transactionService.start()) {
            // SVN URL
            SVNURL url = SVNUtils.toURL(repository.getConfiguration().getUrl());
            // Filters the revision range using the repository configuration
            long startRevision = repository.getConfiguration().getIndexationStart();
            from = Math.max(startRevision, from);
            // Filters the revision range using the SVN repository
            long repositoryRevision = svnClient.getRepositoryRevision(repository, url);
            to = Math.min(to, repositoryRevision);
            // Final check of range
            if (from > to) {
                throw new IllegalArgumentException(String.format("Cannot index range from %d to %d", from, to));
            }
            // Log
            logger.info("[svn-indexation] Repository={}, Range: {}-{}", repository.getId(), from, to);
            // SVN range
            SVNRevision fromRevision = SVNRevision.create(from);
            SVNRevision toRevision = SVNRevision.create(to);
            // Calls the indexer, including merge revisions
            IndexationHandler handler = new IndexationHandler(repository, indexationListener);
            svnClient.log(repository, url, SVNRevision.HEAD, fromRevision, toRevision, true, true, 0, false, handler);
        }
    }
}
