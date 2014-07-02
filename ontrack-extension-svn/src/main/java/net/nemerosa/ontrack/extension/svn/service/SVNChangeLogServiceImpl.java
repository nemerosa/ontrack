package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNIssueRevisionDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.extension.svn.support.SVNUtils.expandBuildPath;

@Service
public class SVNChangeLogServiceImpl extends AbstractSCMChangeLogService implements SVNChangeLogService {

    private final PropertyService propertyService;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNService svnService;
    private final SVNClient svnClient;
    private final TransactionService transactionService;
    private final SecurityService securityService;

    @Autowired
    public SVNChangeLogServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            SVNIssueRevisionDao issueRevisionDao,
            SVNService svnService,
            SVNClient svnClient,
            TransactionService transactionService,
            SecurityService securityService) {
        super(structureService);
        this.propertyService = propertyService;
        this.issueRevisionDao = issueRevisionDao;
        this.svnService = svnService;
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
    @Transactional
    public SVNChangeLogRevisions getChangeLogRevisions(SVNChangeLog changeLog) {

        // Reference
        Collection<SVNChangeLogReference> references = changeLog.getChangeLogReferences();

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

    @Override
    @Transactional
    public SVNChangeLogIssues getChangeLogIssues(SVNChangeLog changeLog) {
        // Revisions must have been loaded first
        if (changeLog.getRevisions() == null) {
            changeLog.withRevisions(getChangeLogRevisions(changeLog));
        }
        // In a transaction
        try (Transaction ignored = transactionService.start()) {
            // Repository
            SVNRepository repository = changeLog.getScmBranch();
            // Index of issues, sorted by keys
            Map<String, SVNChangeLogIssue> issues = new TreeMap<>();
            // For all revisions in this revision log
            for (SVNChangeLogRevision changeLogRevision : changeLog.getRevisions().getList()) {
                long revision = changeLogRevision.getRevision();
                collectIssuesForRevision(repository, issues, revision);
            }
            // List of issues
            List<SVNChangeLogIssue> issuesList = new ArrayList<>(issues.values());
            // Issues link
            IssueServiceConfigurationRepresentation issueServiceConfiguration = null;
            String allIssuesLink = "";
            ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
            if (configuredIssueService != null) {
                issueServiceConfiguration = configuredIssueService.getIssueServiceConfigurationRepresentation();
                allIssuesLink = configuredIssueService.getLinkForAllIssues(
                        issuesList.stream().map(SVNChangeLogIssue::getIssue).collect(Collectors.toList())
                );
            }
            // OK
            return new SVNChangeLogIssues(allIssuesLink, issueServiceConfiguration, issuesList);

        }
    }

    @Override
    @Transactional
    public SVNChangeLogFiles getChangeLogFiles(SVNChangeLog changeLog) {
        // Revisions must have been loaded first
        if (changeLog.getRevisions() == null) {
            changeLog.withRevisions(getChangeLogRevisions(changeLog));
        }
        // In a transaction
        try (Transaction ignored = transactionService.start()) {
            // Index of files, indexed by path
            Map<String, SVNChangeLogFile> files = new TreeMap<>();
            // For each revision
            for (SVNChangeLogRevision changeLogRevision : changeLog.getRevisions().getList()) {
                // Takes into account only the unmerged revisions
                if (changeLogRevision.getLevel() == 0) {
                    long revision = changeLogRevision.getRevision();
                    collectFilesForRevision(changeLog.getScmBranch(), files, revision);
                }
            }
            // List of files
            return new SVNChangeLogFiles(new ArrayList<>(files.values()));
        }
    }

    private void collectFilesForRevision(SVNRepository repository, Map<String, SVNChangeLogFile> files, long revision) {
        SVNRevisionPaths revisionPaths = svnService.getRevisionPaths(repository, revision);
        for (SVNRevisionPath revisionPath : revisionPaths.getPaths()) {
            String path = revisionPath.getPath();
            // Existing file entry?
            SVNChangeLogFile changeLogFile = files.get(path);
            if (changeLogFile == null) {
                changeLogFile = new SVNChangeLogFile(path, repository.getPathBrowsingURL(path));
                files.put(path, changeLogFile);
            }
            // Adds the revision and the type
            SVNChangeLogFileChange change = new SVNChangeLogFileChange(
                    revisionPaths.getInfo(),
                    revisionPath.getChangeType(),
                    repository.getFileChangeBrowsingURL(path, revisionPaths.getInfo().getRevision())
            );
            changeLogFile.addChange(change);
        }
    }

    private void collectIssuesForRevision(SVNRepository repository, Map<String, SVNChangeLogIssue> issues, long revision) {
        // Gets all issues attached to this revision
        List<String> issueKeys = issueRevisionDao.findIssuesByRevision(repository.getId(), revision);
        // For each issue
        for (String issueKey : issueKeys) {
            // Gets its details if not indexed yet
            SVNChangeLogIssue changeLogIssue = issues.get(issueKey);
            if (changeLogIssue == null) {
                changeLogIssue = getChangeLogIssue(repository, issueKey);
            }
            // Existing issue?
            if (changeLogIssue != null) {
                // Attaches the revision to this issue
                SVNRevisionInfo issueRevision = svnService.getRevisionInfo(repository, revision);
                changeLogIssue = changeLogIssue.addRevision(issueRevision);
                // Puts back into the cache
                issues.put(issueKey, changeLogIssue);
            }
        }
    }

    private SVNChangeLogIssue getChangeLogIssue(SVNRepository repository, String issueKey) {
        // Issue service
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        // Gets the details about the issue
        if (configuredIssueService != null) {
            Issue issue = configuredIssueService.getIssue(issueKey);
            if (issue == null || StringUtils.isBlank(issue.getKey())) {
                return null;
            }
            // Creates the issue details for the change logs
            return new SVNChangeLogIssue(issue);
        } else {
            return null;
        }
    }

    private SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, SVNLogEntry svnEntry) {
        return SVNServiceUtils.createChangeLogRevision(
                repository,
                path,
                level,
                svnEntry.getRevision(),
                svnEntry.getMessage(),
                svnEntry.getAuthor(),
                Time.from(svnEntry.getDate(), null)
        );
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
            return securityService.asAdmin(() -> svnService.getRepository(configuration.getName()));
        }
    }

}
