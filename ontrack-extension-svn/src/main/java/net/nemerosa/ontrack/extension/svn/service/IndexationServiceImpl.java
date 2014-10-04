package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.IndexationRange;
import net.nemerosa.ontrack.extension.svn.model.LastRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.model.SVNIndexationException;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.Time;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class IndexationServiceImpl implements IndexationService, JobProvider {

    public static final String SVN_INDEXATION_JOB_GROUP = "SVNIndexation";
    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final TransactionTemplate transactionTemplate;
    private final SVNConfigurationService configurationService;
    private final SVNRepositoryDao repositoryDao;
    private final SVNRevisionDao revisionDao;
    private final SVNEventDao eventDao;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNClient svnClient;
    private final SecurityService securityService;
    private final TransactionService transactionService;
    private final ApplicationContext applicationContext;
    private final JobQueueService jobQueueService;

    @Autowired
    public IndexationServiceImpl(
            PlatformTransactionManager transactionManager,
            SVNConfigurationService configurationService,
            SVNRepositoryDao repositoryDao,
            SVNRevisionDao revisionDao,
            SVNEventDao eventDao,
            SVNIssueRevisionDao issueRevisionDao,
            SVNClient svnClient,
            SecurityService securityService,
            TransactionService transactionService,
            ApplicationContext applicationContext,
            JobQueueService jobQueueService) {
        this.applicationContext = applicationContext;
        this.issueRevisionDao = issueRevisionDao;
        this.jobQueueService = jobQueueService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.configurationService = configurationService;
        this.repositoryDao = repositoryDao;
        this.revisionDao = revisionDao;
        this.eventDao = eventDao;
        this.svnClient = svnClient;
        this.securityService = securityService;
        this.transactionService = transactionService;
    }

    /**
     * Indexation from latest
     */
    @Override
    public Ack indexFromLatest(String name) {
        SVNConfiguration configuration = configurationService.getConfiguration(name);
        return jobQueueService.queue(createIndexFromLatestJob(configuration));
    }

    @Override
    public Ack indexRange(String name, IndexationRange range) {
        return jobQueueService.queue(new Job() {

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getGroup() {
                return SVN_INDEXATION_JOB_GROUP;
            }

            @Override
            public String getCategory() {
                return "SVNIndexRange";
            }

            @Override
            public String getId() {
                return name;
            }

            @Override
            public String getDescription() {
                return String.format(
                        "SVN range indexation for %s, from %d to %d",
                        name,
                        range.getFrom(),
                        range.getTo()
                );
            }

            @Override
            public int getInterval() {
                return 0;
            }

            @Override
            public JobTask createTask() {
                SVNRepository repository = getRepositoryByName(name);
                return new RunnableJobTask(info ->
                        indexRange(repository, range.getFrom(), range.getTo(), info)
                );
            }
        });
    }

    @Override
    public Ack reindex(String name) {
        // Reindex job
        Job reindexJob = new Job() {

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getGroup() {
                return SVN_INDEXATION_JOB_GROUP;
            }

            @Override
            public String getCategory() {
                return "SVNReIndex";
            }

            @Override
            public String getId() {
                return name;
            }

            @Override
            public String getDescription() {
                return String.format(
                        "SVN full reindexation for %s",
                        name
                );
            }

            @Override
            public int getInterval() {
                return 0;
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(
                        info -> fullReindexation(name, info)
                );
            }
        };
        // OK, launches a new indexation
        return jobQueueService.queue(reindexJob);
    }

    private void fullReindexation(String name, JobInfoListener infoListener) {
        // Gets the repository if it exists
        Integer repositoryId = repositoryDao.findByName(name);
        // If it exists, delete it
        if (repositoryId != null) {
            repositoryDao.delete(repositoryId);
        }
        // Creates the repository entry
        repositoryId = repositoryDao.create(name);
        // Gets the configuration
        SVNRepository repository = loadRepository(repositoryId, name);
        // Index from latest
        indexFromLatest(repository, infoListener);
    }

    protected SVNRepository getRepositoryByName(String name) {
        // Gets the repository if it exists
        Integer repositoryId = repositoryDao.findByName(name);
        // If it does not exist, creates it
        if (repositoryId == null) {
            repositoryId = repositoryDao.create(name);
        }
        // Gets the configuration
        return loadRepository(repositoryId, name);
    }

    protected SVNRepository loadRepository(int repositoryId, String name) {
        SVNConfiguration configuration = configurationService.getConfiguration(name);
        return SVNRepository.of(
                repositoryId,
                configuration,
                getIssueServiceRegistry().getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
        );
    }

    /**
     * The {@link net.nemerosa.ontrack.extension.issues.IssueServiceRegistry} cannot be inject because
     * this would create a circular dependency in injections.
     */
    private IssueServiceRegistry getIssueServiceRegistry() {
        return applicationContext.getBean(IssueServiceRegistry.class);
    }

    @Override
    public LastRevisionInfo getLastRevisionInfo(String name) {
        try (Transaction ignored = transactionService.start()) {
            SVNRepository repository = getRepositoryByName(name);
            SVNURL url = SVNUtils.toURL(repository.getConfiguration().getUrl());
            long repositoryRevision = svnClient.getRepositoryRevision(repository, url);
            TRevision r = revisionDao.getLastRevision(repository.getId());
            if (r != null) {
                return new LastRevisionInfo(
                        r.getRevision(),
                        r.getMessage(),
                        repositoryRevision
                );
            } else {
                return LastRevisionInfo.none(repositoryRevision);
            }
        }
    }

    private void indexFromLatest(SVNRepository repository, JobInfoListener infoListener) {
        securityService.checkGlobalFunction(GlobalSettings.class);
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
            indexRange(repository, lastScannedRevision + 1, repositoryRevision, infoListener);
        }

    }

    private void indexRange(SVNRepository repository, Long from, Long to, JobInfoListener infoListener) {
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
        // Indexation
        index(repository, min, max, infoListener);
    }

    @Override
    public Collection<Job> getJobs() {
        return getSvnConfigurations().stream()
                .filter(config -> config.getIndexationInterval() > 0)
                .map(this::createIndexFromLatestJob)
                .collect(Collectors.toList());
    }

    protected Job createIndexFromLatestJob(SVNConfiguration configuration) {
        return new Job() {

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getGroup() {
                return SVN_INDEXATION_JOB_GROUP;
            }

            @Override
            public String getCategory() {
                return "SVNIndexLatest";
            }

            @Override
            public String getId() {
                return configuration.getName();
            }

            @Override
            public String getDescription() {
                return String.format(
                        "SVN indexation from latest for %s",
                        configuration.getName()
                );
            }

            @Override
            public int getInterval() {
                return configuration.getIndexationInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(
                        info -> indexFromLatest(
                                getRepositoryByName(configuration.getName()),
                                info
                        )
                );
            }
        };
    }

    @SuppressWarnings("Convert2MethodRef")
    protected List<SVNConfiguration> getSvnConfigurations() {
        return securityService.asAdmin(() -> configurationService.getConfigurations());
    }

    private class IndexationHandler implements ISVNLogEntryHandler {

        private final SVNRepository repository;
        private final Consumer<Long> revisionListener;

        private IndexationHandler(SVNRepository repository, Consumer<Long> revisionListener) {
            this.repository = repository;
            this.revisionListener = revisionListener;
        }

        @Override
        public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
            // Transaction
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    try {
                        revisionListener.accept(logEntry.getRevision());
                        indexInTransaction(repository, logEntry);
                    } catch (Exception ex) {
                        throw new SVNIndexationException(logEntry.getRevision(), logEntry.getMessage(), ex);
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
        String branch = getBranchForRevision(repository, logEntry);
        // Logging
        logger.info(String.format("Indexing revision %d", revision));
        // Inserting or updating the revision
        revisionDao.addRevision(repository.getId(), revision, author, dateTime, message, branch);
        // Merge relationships (using a nested SVN client)
        try (Transaction ignored = transactionService.start(true)) {
            List<Long> mergedRevisions = svnClient.getMergedRevisions(repository, SVNUtils.toURL(repository.getConfiguration().getUrl(), branch), revision);
            revisionDao.addMergedRevisions(repository.getId(), revision, mergedRevisions);
        }
        // Subversion events
        indexSVNEvents(repository, logEntry);
        // Indexes the issues
        indexIssues(repository, logEntry);
    }

    private void indexIssues(SVNRepository repository, SVNLogEntry logEntry) {
        // Is the repository associated with any issue service?
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        if (configuredIssueService != null) {
            IssueServiceExtension issueServiceExtension = configuredIssueService.getIssueServiceExtension();
            IssueServiceConfiguration issueServiceConfiguration = configuredIssueService.getIssueServiceConfiguration();
            // Revision information to scan
            long revision = logEntry.getRevision();
            String message = logEntry.getMessage();
            // Cache for issues
            Set<String> revisionIssues = new HashSet<>();
            // Gets all issues from the message
            Set<String> issues = issueServiceExtension.extractIssueKeysFromMessage(
                    issueServiceConfiguration,
                    message
            );
            // For each issue in the message
            for (String issueKey : issues) {
                // Checks that the issue has not already been associated with this revision
                if (!revisionIssues.contains(issueKey)) {
                    revisionIssues.add(issueKey);
                    logger.info(String.format("     Indexing revision %d <-> %s", revision, issueKey));
                    // Indexes this issue
                    issueRevisionDao.link(repository.getId(), revision, issueKey);
                }
            }
        }
    }

    private void indexSVNEvents(SVNRepository repository, SVNLogEntry logEntry) {
        indexSVNCopyEvents(repository, logEntry);
        indexSVNStopEvents(repository, logEntry);
    }

    private void indexSVNStopEvents(SVNRepository repository, SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        // Looking for copy tags
        @SuppressWarnings("unchecked")
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        // For all changes path
        for (SVNLogEntryPath logEntryPath : changedPaths.values()) {
            String path = logEntryPath.getPath();
            if (logEntryPath.getType() == SVNLogEntryPath.TYPE_DELETED && svnClient.isTagOrBranch(repository, path)) {
                logger.debug(String.format("\tSTOP %s", path));
                // Adds the stop event
                eventDao.createStopEvent(repository.getId(), revision, path);
            }
        }
    }

    private void indexSVNCopyEvents(SVNRepository repository, SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        // Looking for copy tags
        @SuppressWarnings("unchecked")
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        // Copies
        /*
         * Looks through all changed paths and retains only copy operations toward branches or tags
		 */
        for (SVNLogEntryPath logEntryPath : changedPaths.values()) {
            // Gets the copy path
            String copyFromPath = logEntryPath.getCopyPath();
            if (StringUtils.isNotBlank(copyFromPath) && logEntryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
                // Registers the new history
                String copyToPath = logEntryPath.getPath();
                // Retains only branches and tags
                if (svnClient.isTagOrBranch(repository, copyToPath)) {
                    long copyFromRevision = logEntryPath.getCopyRevision();
                    logger.debug(String.format("\tCOPY %s@%d --> %s", copyFromPath, copyFromRevision, copyToPath));
                    // Adds a copy event
                    eventDao.createCopyEvent(repository.getId(), revision, copyFromPath, copyFromRevision, copyToPath);
                }
            }
        }
    }

    private String getBranchForRevision(SVNRepository repository, SVNLogEntry logEntry) {
        // List of paths for this revision
        @SuppressWarnings("unchecked")
        Set<String> paths = logEntry.getChangedPaths().keySet();
        // Finds the common path among all those paths
        String commonPath = null;
        for (String path : paths) {
            if (commonPath == null) {
                commonPath = path;
            } else {
                int diff = StringUtils.indexOfDifference(commonPath, path);
                commonPath = StringUtils.left(commonPath, diff);
            }
        }
        // Gets the branch for this path
        if (commonPath != null) {
            return extractBranch(repository, commonPath);
        } else {
            // No path in the revision: no branch!
            return null;
        }
    }

    private String extractBranch(SVNRepository repository, String path) {
        if (svnClient.isTrunkOrBranch(repository, path)) {
            return path;
        } else {
            String before = StringUtils.substringBeforeLast(path, "/");
            if (StringUtils.isBlank(before)) {
                return null;
            } else {
                return extractBranch(repository, before);
            }
        }
    }

    /**
     * Indexation of a range in a thread for one repository - since it is called by a single thread executor, we can
     * be sure that only one call of this method is running at one time for one given repository.
     */
    protected void index(SVNRepository repository, long from, long to, JobInfoListener infoListener) {
        // Ordering
        if (from > to) {
            long t = from;
            from = to;
            to = t;
        }

        // Range
        long min = from;
        long max = to;

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
            IndexationHandler handler = new IndexationHandler(repository, revision -> infoListener.post(
                    String.format(
                            "Indexation on %s is running (%d to %d - at %d - %d%%)",
                            repository.getConfiguration().getName(),
                            min,
                            max,
                            revision,
                            Math.round(100.0 * (revision - min + 1) / (max - min + 1))
                    )
            ));
            svnClient.log(repository, url, SVNRevision.HEAD, fromRevision, toRevision, true, true, 0, false, handler);
        }
    }
}
