package net.nemerosa.ontrack.extension.svn.service;

import com.google.common.base.*;
import com.google.common.collect.Maps;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Time;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SVNChangeLogServiceImpl extends AbstractSCMChangeLogService implements SVNChangeLogService {

    private final PropertyService propertyService;
    private final SVNConfigurationService configurationService;
    private final SVNRepositoryDao repositoryDao;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNClient svnClient;
    private final TransactionService transactionService;
    private final SecurityService securityService;

    @Autowired
    public SVNChangeLogServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            SVNConfigurationService configurationService, SVNRepositoryDao repositoryDao,
            IssueServiceRegistry issueServiceRegistry,
            SVNClient svnClient, TransactionService transactionService, SecurityService securityService) {
        super(structureService);
        this.propertyService = propertyService;
        this.configurationService = configurationService;
        this.repositoryDao = repositoryDao;
        this.issueServiceRegistry = issueServiceRegistry;
        this.svnClient = svnClient;
        this.transactionService = transactionService;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public SVNChangeLog changeLog(BuildDiffRequest request) {
        try (Transaction ignored = transactionService.start()) {
            Branch branch = structureService.getBranch(request.getBranch());
            SVNRepository svnRepository = getSVNRepository(branch);
            return new SVNChangeLog(
                    UUID.randomUUID().toString(),
                    branch,
                    svnRepository,
                    getSCMBuildView(svnRepository, request.getFrom()),
                    getSCMBuildView(svnRepository, request.getTo())
            );
        }
    }

    @Override
    public SVNChangeLogRevisions getChangeLogRevisions(SVNChangeLog changeLog) {

        // Reference
        Collection<SVNChangeLogReference> references = getChangeLogReferences(changeLog);

        // No difference?
        if (references.isEmpty()) {
            return SVNChangeLogRevisions.none();
        }

        // SVN transaction
        try (Transaction ignored = transactionService.start()) {
            List<SVNChangeLogRevision> revisions = new ArrayList<>();
            for (SVNChangeLogReference reference : references) {
                if (!reference.isNone()) {
                    SVNRepository repository = changeLog.getScmBranch();
                    // List of log entries
                    SVNLogEntryCollector logEntryCollector = new SVNLogEntryCollector();
                    // SVN change log
                    svnClient.log(
                            repository,
                            SVNUtils.toURL(repository.getUrl(reference.getPath())),
                            SVNRevision.create(reference.getEnd()),
                            SVNRevision.create(reference.getStart()),
                            SVNRevision.create(reference.getEnd()),
                            true, // Stops on copy
                            false, // No path discovering (yet)
                            0L, // no limit
                            true, // Includes merged revisions
                            logEntryCollector
                    );
                    // Loops through all SVN log entries, taking the merged revisions into account
                    int level = 0;
                    for (SVNLogEntry svnEntry : logEntryCollector.getEntries()) {
                        long revision = svnEntry.getRevision();
                        if (SVNRevision.isValidRevisionNumber(revision)) {
                            // Conversion
                            SVNChangeLogRevision entry = createChangeLogRevision(repository, reference.getPath(), level, svnEntry);
                            // Adds it to the list
                            revisions.add(entry);
                            // New parent?
                            if (svnEntry.hasChildren()) {
                                level++;
                            }
                        } else {
                            level--;
                        }
                    }
                }
            }
            // OK
            return new SVNChangeLogRevisions(revisions);
        }
    }

    private SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, SVNLogEntry svnEntry) {
        return createChangeLogRevision(
                repository,
                path,
                level,
                svnEntry.getRevision(),
                svnEntry.getMessage(),
                svnEntry.getAuthor(),
                Time.from(svnEntry.getDate(), null)
        );
    }

    private SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, long revision, String message, String author, LocalDateTime revisionDate) {
        // Issue service
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        // Formatted message
        String formattedMessage;
        if (configuredIssueService != null) {
            formattedMessage = configuredIssueService.formatIssuesInMessage(message);
        } else {
            formattedMessage = message;
        }
        // Revision URL
        String revisionUrl = repository.getRevisionBrowsingURL(revision);
        // OK
        return new SVNChangeLogRevision(
                path,
                level,
                revision,
                author,
                revisionDate,
                message,
                revisionUrl,
                formattedMessage);
    }

    private Collection<SVNChangeLogReference> getChangeLogReferences(SVNChangeLog changeLog) {

        // Gets the two histories
        SVNHistory historyFrom = changeLog.getScmBuildFrom().getScm();
        SVNHistory historyTo = changeLog.getScmBuildTo().getScm();

        // Sort them from->to with 'to' having the highest revision
        {
            long fromRevision = historyFrom.getReferences().get(0).getRevision();
            long toRevision = historyTo.getReferences().get(0).getRevision();
            if (toRevision < fromRevision) {
                SVNHistory tmp = historyTo;
                historyTo = historyFrom;
                historyFrom = tmp;
            }
        }

        // Indexation of the 'from' history using the paths
        Map<String, SVNReference> historyFromIndex = Maps.uniqueIndex(
                historyFrom.getReferences(),
                SVNReference::getPath
        );

        // List of ranges to collect
        List<SVNChangeLogReference> references = new ArrayList<>();

        // For each reference on the 'to' history
        for (SVNReference toReference : historyTo.getReferences()) {
            // Collects a range of revisions
            long toRevision = toReference.getRevision();
            long fromRevision = 0;
            // Gets any 'from' reference
            SVNReference fromReference = historyFromIndex.get(toReference.getPath());
            if (fromReference != null) {
                fromRevision = fromReference.getRevision();
                if (fromRevision > toRevision) {
                    long t = toRevision;
                    toRevision = fromRevision;
                    fromRevision = t;
                }
            }
            // Adds this reference
            references.add(new SVNChangeLogReference(
                    toReference.getPath(),
                    fromRevision,
                    toRevision
            ));
        }

        // OK
        return references;
    }

    protected SCMBuildView<SVNHistory> getSCMBuildView(SVNRepository svnRepository, ID buildId) {
        // Gets the build view
        BuildView buildView = getBuildView(buildId);
        // Gets the history for the build
        SVNHistory history = getBuildSVNHistory(svnRepository, buildView.getBuild());
        // OK
        return new SCMBuildView<>(buildView, history);
    }

    protected SVNHistory getBuildSVNHistory(SVNRepository svnRepository, Build build) {
        // Gets the build path for the branch
        String svnBuildPath = getSVNBuildPath(build);
        // Gets the history from the SVN client
        return svnClient.getHistory(svnRepository, svnBuildPath);
    }

    protected String getSVNBuildPath(Build build) {
        // Gets the build path property value
        Property<SVNBranchConfigurationProperty> branchConfiguration = propertyService.getProperty(
                build.getBranch(),
                SVNBranchConfigurationPropertyType.class
        );
        if (branchConfiguration.isEmpty()) {
            throw new MissingSVNBranchConfigurationException(build.getBranch().getName());
        } else {
            // Gets the build path definition
            String buildPathDefinition = branchConfiguration.getValue().getBuildPath();
            // Expands the build path
            return expandBuildPath(buildPathDefinition, build);
        }
    }

    protected String expandBuildPath(String buildPathDefinition, Build build) {
        // Pattern
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(buildPathDefinition);
        StringBuffer path = new StringBuffer();
        while (matcher.find()) {
            String replacement = expandBuildPathExpression(matcher.group(1), build);
            matcher.appendReplacement(path, replacement);
        }
        matcher.appendTail(path);
        // TODO Property expansion
        // OK
        return path.toString();
    }

    protected String expandBuildPathExpression(String expression, Build build) {
        if ("build".equals(expression)) {
            return build.getName();
        } else {
            throw new UnknownBuildPathExpression(expression);
        }
    }

    protected SVNRepository getSVNRepository(Branch branch) {
        // Gets the SVN project configuration property
        Property<SVNProjectConfigurationProperty> projectConfiguration = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (projectConfiguration.isEmpty()) {
            throw new MissingSVNProjectConfigurationException(branch.getProject().getName());
        } else {
            SVNConfiguration configuration = projectConfiguration.getValue().getConfiguration();
            return securityService.asAdmin(() -> SVNRepository.of(
                    repositoryDao.getOrCreateByName(configuration.getName()),
                    // The configuration contained in the property's configuration is obfuscated
                    // and the original one must be loaded
                    configurationService.getConfiguration(configuration.getName()),
                    issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
            ));
        }
    }

}
