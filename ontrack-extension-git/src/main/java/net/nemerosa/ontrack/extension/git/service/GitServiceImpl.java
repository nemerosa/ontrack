package net.nemerosa.ontrack.extension.git.service;

import com.google.common.collect.Lists;
import net.nemerosa.ontrack.common.FutureUtils;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequestDifferenceProjectException;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceNotConfiguredException;
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;
import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo;
import net.nemerosa.ontrack.extension.scm.service.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.service.SCMUtilsService;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
import net.nemerosa.ontrack.git.exceptions.GitRepositorySyncException;
import net.nemerosa.ontrack.git.model.*;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.*;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
@Transactional
public class GitServiceImpl extends AbstractSCMChangeLogService<GitConfiguration, GitBuildInfo, GitChangeLogIssue> implements GitService, JobOrchestratorSupplier {

    private static final JobCategory GIT_JOB_CATEGORY = JobCategory.of("git").withName("Git");

    private static final JobType GIT_INDEXATION_JOB = GIT_JOB_CATEGORY.getType("git-indexation").withName("Git indexation");
    private static final JobType GIT_BUILD_SYNC_JOB = GIT_JOB_CATEGORY.getType("git-build-sync").withName("Git build synchronisation");

    private final Logger logger = LoggerFactory.getLogger(GitService.class);

    private final PropertyService propertyService;
    private final JobScheduler jobScheduler;
    private final SecurityService securityService;
    private final TransactionService transactionService;
    private final ApplicationLogService applicationLogService;
    private final GitRepositoryClientFactory gitRepositoryClientFactory;
    private final BuildGitCommitLinkService buildGitCommitLinkService;
    private final Collection<GitConfigurator> gitConfigurators;
    private final SCMUtilsService scmService;

    @Autowired
    public GitServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            JobScheduler jobScheduler,
            SecurityService securityService,
            TransactionService transactionService,
            ApplicationLogService applicationLogService,
            GitRepositoryClientFactory gitRepositoryClientFactory,
            BuildGitCommitLinkService buildGitCommitLinkService,
            Collection<GitConfigurator> gitConfigurators,
            SCMUtilsService scmService) {
        super(structureService, propertyService);
        this.propertyService = propertyService;
        this.jobScheduler = jobScheduler;
        this.securityService = securityService;
        this.transactionService = transactionService;
        this.applicationLogService = applicationLogService;
        this.gitRepositoryClientFactory = gitRepositoryClientFactory;
        this.buildGitCommitLinkService = buildGitCommitLinkService;
        this.gitConfigurators = gitConfigurators;
        this.scmService = scmService;
    }

    @Override
    public void forEachConfiguredProject(BiConsumer<Project, GitConfiguration> consumer) {
        structureService.getProjectList()
                .forEach(project -> {
                    Optional<GitConfiguration> configuration = getProjectConfiguration(project);
                    configuration.ifPresent(gitConfiguration -> consumer.accept(project, gitConfiguration));
                });
    }

    @Override
    public void forEachConfiguredBranch(BiConsumer<Branch, GitBranchConfiguration> consumer) {
        for (Project project : structureService.getProjectList()) {
            structureService.getBranchesForProject(project.getId()).stream()
                    .filter(branch -> branch.getType() != BranchType.TEMPLATE_DEFINITION)
                    .forEach(branch -> {
                        Optional<GitBranchConfiguration> configuration = getBranchConfiguration(branch);
                        configuration.ifPresent(gitBranchConfiguration -> consumer.accept(branch, gitBranchConfiguration));
                    });
        }
    }

    @Override
    public Stream<JobRegistration> collectJobRegistrations() {
        List<JobRegistration> jobs = new ArrayList<>();
        // Indexation of repositories, based on projects actually linked
        forEachConfiguredProject((project, configuration) -> jobs.add(getGitIndexationJobRegistration(configuration)));
        // Synchronisation of branch builds with tags when applicable
        forEachConfiguredBranch((branch, branchConfiguration) -> {
            // Build/tag sync job
            if (branchConfiguration.getBuildTagInterval() > 0
                    && branchConfiguration.getBuildCommitLink().getLink() instanceof IndexableBuildGitCommitLink) {
                jobs.add(
                        JobRegistration.of(createBuildSyncJob(branch))
                                .everyMinutes(branchConfiguration.getBuildTagInterval())
                );
            }
        });
        // OK
        return jobs.stream();
    }

    @Override
    public boolean isBranchConfiguredForGit(Branch branch) {
        return getBranchConfiguration(branch).isPresent();
    }

    @Override
    public Optional<Future<?>> launchBuildSync(ID branchId, boolean synchronous) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Gets its configuration
        Optional<GitBranchConfiguration> branchConfiguration = getBranchConfiguration(branch);
        // If valid, launches a job
        if (branchConfiguration.isPresent() && branchConfiguration.get().getBuildCommitLink().getLink() instanceof IndexableBuildGitCommitLink) {
            if (synchronous) {
                buildSync(branch, branchConfiguration.get(), JobRunListener.logger(logger));
                return Optional.empty();
            } else {
                return jobScheduler.fireImmediately(getGitBranchSyncJobKey(branch));
            }
        }
        // Else, nothing has happened
        else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public GitChangeLog changeLog(BuildDiffRequest request) {
        try (Transaction ignored = transactionService.start()) {
            // Gets the two builds
            Build buildFrom = structureService.getBuild(request.getFrom());
            Build buildTo = structureService.getBuild(request.getTo());
            // Ordering of builds
            if (buildFrom.id() > buildTo.id()) {
                Build t = buildFrom;
                buildFrom = buildTo;
                buildTo = t;
            }
            // Gets the two associated projects
            Project project = buildFrom.getBranch().getProject();
            Project otherProject = buildTo.getBranch().getProject();
            // Checks the project
            if (project.id() != otherProject.id()) {
                throw new BuildDiffRequestDifferenceProjectException();
            }
            // Project Git configuration
            Optional<GitConfiguration> oProjectConfiguration = getProjectConfiguration(project);
            if (oProjectConfiguration.isPresent()) {
                // Forces Git sync before
                boolean syncError;
                GitConfiguration gitConfiguration = oProjectConfiguration.get();
                try {
                    syncAndWait(gitConfiguration);
                    syncError = false;
                } catch (GitRepositorySyncException ex) {
                    applicationLogService.log(
                            ApplicationLogEntry.error(
                                    ex,
                                    NameDescription.nd(
                                            "git-sync",
                                            "Git synchronisation issue"
                                    ),
                                    gitConfiguration.getRemote()
                            ).withDetail("project", project.getName())
                                    .withDetail("git-name", gitConfiguration.getName())
                                    .withDetail("git-remote", gitConfiguration.getRemote())
                    );
                    syncError = true;
                }
                // Change log computation
                return new GitChangeLog(
                        UUID.randomUUID().toString(),
                        project,
                        getSCMBuildView(buildFrom.getId()),
                        getSCMBuildView(buildTo.getId()),
                        syncError
                );
            } else {
                throw new GitProjectNotConfiguredException(project.getId());
            }
        }
    }

    protected Object syncAndWait(GitConfiguration gitConfiguration) {
        return FutureUtils.wait("Synchronisation for " + gitConfiguration.getName(), sync(gitConfiguration, GitSynchronisationRequest.SYNC));
    }

    protected GitConfiguration getRequiredProjectConfiguration(Project project) {
        return getProjectConfiguration(project)
                .orElseThrow(() -> new GitProjectNotConfiguredException(project.getId()));
    }

    protected GitRepositoryClient getGitRepositoryClient(Project project) {
        return getProjectConfiguration(project)
                .map(GitConfiguration::getGitRepository)
                .map(gitRepositoryClientFactory::getClient)
                .orElseThrow(() -> new GitProjectNotConfiguredException(project.getId()));
    }

    @Override
    public GitChangeLogCommits getChangeLogCommits(GitChangeLog changeLog) {
        // Gets the client
        GitRepositoryClient client = getGitRepositoryClient(changeLog.getProject());
        // Gets the build boundaries
        Build buildFrom = changeLog.getFrom().getBuild();
        Build buildTo = changeLog.getTo().getBuild();
        // Commit boundaries
        String commitFrom = getCommitFromBuild(buildFrom);
        String commitTo = getCommitFromBuild(buildTo);
        // Gets the commits
        GitLog log = client.graph(commitFrom, commitTo);
        // If log empty, inverts the boundaries
        if (log.getCommits().isEmpty()) {
            String t = commitFrom;
            commitFrom = commitTo;
            commitTo = t;
            log = client.graph(commitFrom, commitTo);
        }
        // Consolidation to UI
        List<GitCommit> commits = log.getCommits();
        List<GitUICommit> uiCommits = toUICommits(getRequiredProjectConfiguration(changeLog.getProject()), commits);
        return new GitChangeLogCommits(
                new GitUILog(
                        log.getPlot(),
                        uiCommits
                )
        );
    }

    protected String getCommitFromBuild(Build build) {
        return getBranchConfiguration(build.getBranch())
                .map(c -> c.getBuildCommitLink().getCommitFromBuild(build))
                .orElseThrow(() -> new GitBranchNotConfiguredException(build.getBranch().getId()));
    }

    @Override
    public GitChangeLogIssues getChangeLogIssues(GitChangeLog changeLog) {
        // Commits must have been loaded first
        if (changeLog.getCommits() == null) {
            changeLog.withCommits(getChangeLogCommits(changeLog));
        }
        // In a transaction
        try (Transaction ignored = transactionService.start()) {
            // Configuration
            GitConfiguration configuration = getRequiredProjectConfiguration(changeLog.getProject());
            // Issue service
            ConfiguredIssueService configuredIssueService = configuration.getConfiguredIssueService().orElse(null);
            if (configuredIssueService == null) {
                throw new IssueServiceNotConfiguredException();
            }
            // Index of issues, sorted by keys
            Map<String, GitChangeLogIssue> issues = new TreeMap<>();
            // For all commits in this commit log
            for (GitUICommit gitUICommit : changeLog.getCommits().getLog().getCommits()) {
                Set<String> keys = configuredIssueService.extractIssueKeysFromMessage(gitUICommit.getCommit().getFullMessage());
                for (String key : keys) {
                    GitChangeLogIssue existingIssue = issues.get(key);
                    if (existingIssue != null) {
                        existingIssue.add(gitUICommit);
                    } else {
                        Issue issue = configuredIssueService.getIssue(key);
                        if (issue != null) {
                            existingIssue = GitChangeLogIssue.of(issue, gitUICommit);
                            issues.put(key, existingIssue);
                        }
                    }
                }
            }
            // List of issues
            List<GitChangeLogIssue> issuesList = new ArrayList<>(issues.values());
            // Issues link
            IssueServiceConfigurationRepresentation issueServiceConfiguration = configuredIssueService.getIssueServiceConfigurationRepresentation();
            // OK
            return new GitChangeLogIssues(issueServiceConfiguration, issuesList);

        }
    }

    @Override
    public GitChangeLogFiles getChangeLogFiles(GitChangeLog changeLog) {
        // Gets the configuration
        GitConfiguration configuration = getRequiredProjectConfiguration(changeLog.getProject());
        // Gets the client for this project
        GitRepositoryClient client = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
        // Gets the build boundaries
        Build buildFrom = changeLog.getFrom().getBuild();
        Build buildTo = changeLog.getTo().getBuild();
        // Commit boundaries
        String commitFrom = getCommitFromBuild(buildFrom);
        String commitTo = getCommitFromBuild(buildTo);
        // Diff
        final GitDiff diff = client.diff(commitFrom, commitTo);
        // File change links
        String fileChangeLinkFormat = configuration.getFileAtCommitLink();
        // OK
        return new GitChangeLogFiles(
                diff.getEntries().stream()
                        .map(entry -> toChangeLogFile(entry).withUrl(
                                getDiffUrl(diff, entry, fileChangeLinkFormat)
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean isPatternFound(GitBranchConfiguration branchConfiguration, String token) {
        // Gets the client
        GitRepositoryClient client = gitRepositoryClientFactory.getClient(branchConfiguration.getConfiguration().getGitRepository());
        // Scanning
        return client.isPatternFound(token);
    }

    @Override
    public OntrackGitIssueInfo getIssueInfo(ID branchId, String key) {
        Branch branch = structureService.getBranch(branchId);
        // Configuration
        GitBranchConfiguration branchConfiguration = getRequiredBranchConfiguration(branch);
        GitConfiguration configuration = branchConfiguration.getConfiguration();
        // Issue service
        ConfiguredIssueService configuredIssueService = configuration.getConfiguredIssueService().orElse(null);
        if (configuredIssueService == null) {
            throw new GitBranchIssueServiceNotConfiguredException(branchId);
        }
        // Gets the details about the issue
        Issue issue = configuredIssueService.getIssue(key);

        // Collects commits per branches
        List<OntrackGitIssueCommitInfo> commitInfos = collectIssueCommitInfos(branch.getProject(), issue);

        // OK
        return new OntrackGitIssueInfo(
                configuredIssueService.getIssueServiceConfigurationRepresentation(),
                issue,
                commitInfos
        );
    }

    private List<OntrackGitIssueCommitInfo> collectIssueCommitInfos(Project project, Issue issue) {
        // Index of commit infos
        Map<String, OntrackGitIssueCommitInfo> commitInfos = new LinkedHashMap<>();
        // For all configured branches
        forEachConfiguredBranch((branch, branchConfiguration) -> {
            // Filter per project
            if (branch.projectId() != project.id()) {
                return;
            }
            // Gets the branch configuration
            GitConfiguration configuration = branchConfiguration.getConfiguration();
            // Gets the Git client for this project
            GitRepositoryClient client = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
            // Issue service
            ConfiguredIssueService configuredIssueService = configuration.getConfiguredIssueService().orElse(null);
            if (configuredIssueService != null) {
                // List of commits for this branch
                List<RevCommit> revCommits = new ArrayList<>();
                // Scanning this branch's repository for the commit
                client.scanCommits(branchConfiguration.getBranch(), revCommit -> {
                    String message = revCommit.getFullMessage();
                    Set<String> keys = configuredIssueService.extractIssueKeysFromMessage(message);
                    // Gets all linked issues
                    boolean matching = configuredIssueService.getLinkedIssues(branch.getProject(), issue).stream()
                            .map(Issue::getKey)
                            .anyMatch(key -> configuredIssueService.containsIssueKey(key, keys));
                    if (matching) {
                        // We have a commit for this branch!
                        revCommits.add(revCommit);
                    }
                    return false; // Scanning all commits
                });
                // If at least one commit
                if (revCommits.size() > 0) {
                    // Gets the last commit (which is the first in the list)
                    RevCommit revCommit = revCommits.get(0);
                    // Commit explained (independent from the branch)
                    GitCommit commit = client.toCommit(revCommit);
                    String commitId = commit.getId();
                    // Gets any existing commit info
                    OntrackGitIssueCommitInfo commitInfo = commitInfos.get(commitId);
                    // If not defined, creates an entry
                    if (commitInfo == null) {
                        // UI commit (independent from the branch)
                        GitUICommit uiCommit = toUICommit(
                                configuration.getCommitLink(),
                                getMessageAnnotators(configuration),
                                commit
                        );
                        // Commit info
                        commitInfo = OntrackGitIssueCommitInfo.of(uiCommit);
                        // Indexation
                        commitInfos.put(commitId, commitInfo);
                    }
                    // Collects branch info
                    SCMIssueCommitBranchInfo branchInfo = SCMIssueCommitBranchInfo.of(branch);
                    // Gets the last build for this branch
                    Optional<Build> buildAfterCommit = getEarliestBuildAfterCommit(commitId, branch, branchConfiguration, client);
                    branchInfo = scmService.getBranchInfo(buildAfterCommit, branchInfo);
                    // Adds the info
                    commitInfo.add(branchInfo);
                }
            }
        });
        // OK
        return Lists.newArrayList(commitInfos.values());
    }

    @Override
    public Optional<GitUICommit> lookupCommit(GitConfiguration configuration, String id) {
        // Gets the client client for this configuration
        GitRepositoryClient gitClient = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
        // Gets the commit
        Optional<GitCommit> optGitCommit = gitClient.getCommitFor(id);
        if (optGitCommit.isPresent()) {
            String commitLink = configuration.getCommitLink();
            List<? extends MessageAnnotator> messageAnnotators = getMessageAnnotators(configuration);
            return Optional.of(
                    toUICommit(
                            commitLink,
                            messageAnnotators,
                            optGitCommit.get()
                    )
            );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public OntrackGitCommitInfo getCommitInfo(ID branchId, String commit) {
        return getOntrackGitCommitInfo(commit);
    }

    @Override
    public List<String> getRemoteBranches(GitConfiguration configuration) {
        GitRepositoryClient gitClient = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
        return gitClient.getRemoteBranches();
    }

    @Override
    public String diff(GitChangeLog changeLog, List<String> patterns) {
        // Gets the client client for this configuration`
        GitRepositoryClient gitClient = getGitRepositoryClient(changeLog.getProject());
        // Path predicate
        Predicate<String> pathFilter = scmService.getPathFilter(patterns);
        // Gets the build boundaries
        Build buildFrom = changeLog.getFrom().getBuild();
        Build buildTo = changeLog.getTo().getBuild();
        // Commit boundaries
        String commitFrom = getCommitFromBuild(buildFrom);
        String commitTo = getCommitFromBuild(buildTo);
        // Gets the diff
        return gitClient.unifiedDiff(
                commitFrom,
                commitTo,
                pathFilter
        );
    }

    @Override
    public Optional<String> download(Branch branch, String path) {
        securityService.checkProjectFunction(branch, ProjectConfig.class);
        return transactionService.doInTransaction(() -> {
            GitBranchConfiguration branchConfiguration = getRequiredBranchConfiguration(branch);
            GitRepositoryClient client = gitRepositoryClientFactory.getClient(
                    branchConfiguration.getConfiguration().getGitRepository()
            );
            return client.download(branchConfiguration.getBranch(), path);
        });
    }

    @Override
    public Ack projectSync(Project project, GitSynchronisationRequest request) {
        securityService.checkProjectFunction(project, ProjectConfig.class);
        Optional<GitConfiguration> projectConfiguration = getProjectConfiguration(project);
        return projectConfiguration
                .map(gitConfiguration -> Ack.validate(sync(gitConfiguration, request).isPresent()))
                .orElse(Ack.NOK);
    }

    @Override
    public Optional<Future<?>> sync(GitConfiguration gitConfiguration, GitSynchronisationRequest request) {
        // Reset the repository?
        if (request.isReset()) {
            gitRepositoryClientFactory.getClient(gitConfiguration.getGitRepository()).reset();
        }
        // Schedules the job
        return jobScheduler.fireImmediately(getGitIndexationJobKey(gitConfiguration));
    }

    @Override
    public GitSynchronisationInfo getProjectGitSyncInfo(Project project) {
        securityService.checkProjectFunction(project, ProjectConfig.class);
        return getProjectConfiguration(project)
                .map(this::getGitSynchronisationInfo)
                .orElseThrow(() -> new GitProjectNotConfiguredException(project.getId()));
    }

    private GitSynchronisationInfo getGitSynchronisationInfo(GitConfiguration gitConfiguration) {
        // Gets the client for this configuration
        GitRepositoryClient client = gitRepositoryClientFactory.getClient(gitConfiguration.getGitRepository());
        // Gets the status
        GitSynchronisationStatus status = client.getSynchronisationStatus();
        // Collects the branch info
        List<GitBranchInfo> branches;
        if (status == GitSynchronisationStatus.IDLE) {
            branches = client.getBranches().getBranches();
        } else {
            branches = Collections.emptyList();
        }
        // OK
        return new GitSynchronisationInfo(
                gitConfiguration.getType(),
                gitConfiguration.getName(),
                gitConfiguration.getRemote(),
                gitConfiguration.getIndexationInterval(),
                status,
                branches
        );
    }

    private OntrackGitCommitInfo getOntrackGitCommitInfo(String commit) {
        // Reference data
        AtomicReference<GitCommit> theCommit = new AtomicReference<>();
        AtomicReference<GitConfiguration> theConfiguration = new AtomicReference<>();
        // Data to collect
        Collection<BuildView> buildViews = new ArrayList<>();
        Collection<BranchStatusView> branchStatusViews = new ArrayList<>();
        // For all configured branches
        forEachConfiguredBranch((branch, branchConfiguration) -> {
            GitConfiguration configuration = branchConfiguration.getConfiguration();
            // Gets the client client for this branch
            GitRepositoryClient gitClient = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
            // Gets the commit
            Optional<GitCommit> commitFor = gitClient.getCommitFor(commit);
            // If present...
            if (commitFor.isPresent()) {
                // Reference
                if (theCommit.get() == null) {
                    theCommit.set(commitFor.get());
                    theConfiguration.set(configuration);
                }
                // Gets the earliest build on this branch that contains this commit
                getEarliestBuildAfterCommit(commit, branch, branchConfiguration, gitClient)
                        // ... and it present collect its data
                        .ifPresent(build -> {
                            // Gets the build view
                            BuildView buildView = structureService.getBuildView(build, true);
                            // Adds it to the list
                            buildViews.add(buildView);
                            // Collects the promotions for the branch
                            branchStatusViews.add(
                                    structureService.getEarliestPromotionsAfterBuild(build)
                            );
                        });
            }
        });

        // OK
        if (theCommit.get() != null) {
            String commitLink = theConfiguration.get().getCommitLink();
            List<? extends MessageAnnotator> messageAnnotators = getMessageAnnotators(theConfiguration.get());
            return new OntrackGitCommitInfo(
                    toUICommit(
                            commitLink,
                            messageAnnotators,
                            theCommit.get()
                    ),
                    buildViews,
                    branchStatusViews
            );
        } else {
            throw new GitCommitNotFoundException(commit);
        }

    }

    protected <T> Optional<Build> getEarliestBuildAfterCommit(String commit, Branch branch, GitBranchConfiguration branchConfiguration, GitRepositoryClient client) {
        @SuppressWarnings("unchecked")
        ConfiguredBuildGitCommitLink<T> configuredBuildGitCommitLink = (ConfiguredBuildGitCommitLink<T>) branchConfiguration.getBuildCommitLink();
        // Delegates to the build commit link...
        return configuredBuildGitCommitLink.getLink()
                // ... by getting candidate references
                .getBuildCandidateReferences(commit, branch, client, branchConfiguration, configuredBuildGitCommitLink.getData())
                // ... gets the builds
                .map(buildName -> structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName))
                // ... filter on existing builds
                .filter(Optional::isPresent).map(Optional::get)
                // ... filter the builds using the link
                .filter(build -> configuredBuildGitCommitLink.getLink().isBuildEligible(build, configuredBuildGitCommitLink.getData()))
                // ... sort by decreasing date
                .sorted((o1, o2) -> (o1.id() - o2.id()))
                // ... takes the first build
                .findFirst();
    }

    private String getDiffUrl(GitDiff diff, GitDiffEntry entry, String fileChangeLinkFormat) {
        if (StringUtils.isNotBlank(fileChangeLinkFormat)) {
            return fileChangeLinkFormat
                    .replace("{commit}", entry.getReferenceId(diff.getFrom(), diff.getTo()))
                    .replace("{path}", entry.getReferencePath());
        } else {
            return "";
        }
    }

    private GitChangeLogFile toChangeLogFile(GitDiffEntry entry) {
        switch (entry.getChangeType()) {
            case ADD:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.ADDED, entry.getNewPath());
            case COPY:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.COPIED, entry.getOldPath(), entry.getNewPath());
            case DELETE:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.DELETED, entry.getOldPath());
            case MODIFY:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.MODIFIED, entry.getOldPath());
            case RENAME:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.RENAMED, entry.getOldPath(), entry.getNewPath());
            default:
                return GitChangeLogFile.of(SCMChangeLogFileChangeType.UNDEFINED, entry.getOldPath(), entry.getNewPath());
        }
    }

    private List<GitUICommit> toUICommits(GitConfiguration gitConfiguration, List<GitCommit> commits) {
        // Link?
        String commitLink = gitConfiguration.getCommitLink();
        // Issue-based annotations
        List<? extends MessageAnnotator> messageAnnotators = getMessageAnnotators(gitConfiguration);
        // OK
        return commits.stream()
                .map(commit -> toUICommit(commitLink, messageAnnotators, commit))
                .collect(Collectors.toList());
    }

    private GitUICommit toUICommit(String commitLink, List<? extends MessageAnnotator> messageAnnotators, GitCommit commit) {
        return new GitUICommit(
                commit,
                MessageAnnotationUtils.annotate(commit.getShortMessage(), messageAnnotators),
                MessageAnnotationUtils.annotate(commit.getFullMessage(), messageAnnotators),
                StringUtils.replace(commitLink, "{commit}", commit.getId())
        );
    }

    private List<? extends MessageAnnotator> getMessageAnnotators(GitConfiguration gitConfiguration) {
        List<? extends MessageAnnotator> messageAnnotators;
        ConfiguredIssueService configuredIssueService = gitConfiguration.getConfiguredIssueService().orElse(null);
        if (configuredIssueService != null) {
            // Gets the message annotator
            Optional<MessageAnnotator> messageAnnotator = configuredIssueService.getMessageAnnotator();
            // If present annotate the messages
            messageAnnotators = messageAnnotator.map(Collections::singletonList).orElseGet(Collections::emptyList);
        } else {
            messageAnnotators = Collections.emptyList();
        }
        return messageAnnotators;
    }

    private SCMBuildView<GitBuildInfo> getSCMBuildView(ID buildId) {
        return new SCMBuildView<>(getBuildView(buildId), new GitBuildInfo());
    }

    @Override
    public Optional<GitConfiguration> getProjectConfiguration(Project project) {
        return gitConfigurators.stream()
                .map(c -> c.getConfiguration(project))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    protected GitBranchConfiguration getRequiredBranchConfiguration(Branch branch) {
        return getBranchConfiguration(branch)
                .orElseThrow(() -> new GitBranchNotConfiguredException(branch.getId()));
    }

    @Override
    public Optional<GitBranchConfiguration> getBranchConfiguration(Branch branch) {
        // Get the configuration for the project
        Optional<GitConfiguration> configuration = getProjectConfiguration(branch.getProject());
        if (configuration.isPresent()) {
            // Gets the configuration for a branch
            String gitBranch;
            ConfiguredBuildGitCommitLink<?> buildCommitLink;
            boolean override;
            int buildTagInterval;
            Property<GitBranchConfigurationProperty> branchConfig = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
            if (!branchConfig.isEmpty()) {
                gitBranch = branchConfig.getValue().getBranch();
                buildCommitLink = toConfiguredBuildGitCommitLink(
                        branchConfig.getValue().getBuildCommitLink()
                );
                override = branchConfig.getValue().isOverride();
                buildTagInterval = branchConfig.getValue().getBuildTagInterval();
            } else {
                gitBranch = "master";
                buildCommitLink = TagBuildNameGitCommitLink.DEFAULT;
                override = false;
                buildTagInterval = 0;
            }
            // OK
            return Optional.of(
                    new GitBranchConfiguration(
                            configuration.get(),
                            gitBranch,
                            buildCommitLink,
                            override,
                            buildTagInterval
                    )
            );
        } else {
            return Optional.empty();
        }
    }

    private <T> ConfiguredBuildGitCommitLink<T> toConfiguredBuildGitCommitLink(ServiceConfiguration serviceConfiguration) {
        @SuppressWarnings("unchecked")
        BuildGitCommitLink<T> link = (BuildGitCommitLink<T>) buildGitCommitLinkService.getLink(serviceConfiguration.getId());
        T linkData = link.parseData(serviceConfiguration.getData());
        return new ConfiguredBuildGitCommitLink<>(
                link,
                linkData
        );
    }

    private Job createBuildSyncJob(Branch branch) {
        GitBranchConfiguration configuration = getRequiredBranchConfiguration(branch);
        return new AbstractBranchJob(structureService, branch) {

            @Override
            public JobKey getKey() {
                return getGitBranchSyncJobKey(branch);
            }

            @Override
            public JobRun getTask() {
                return listener -> buildSync(branch, configuration, listener);
            }

            @Override
            public String getDescription() {
                return format(
                        "Branch %s @ %s",
                        branch.getName(),
                        branch.getProject().getName()
                );
            }

            @Override
            public boolean isDisabled() {
                return super.isDisabled() &&
                        isBranchConfiguredForGit(branch);
            }
        };
    }

    protected JobKey getGitBranchSyncJobKey(Branch branch) {
        return GIT_BUILD_SYNC_JOB.getKey(String.valueOf(branch.getId()));
    }

    private JobKey getGitIndexationJobKey(GitConfiguration config) {
        return GIT_INDEXATION_JOB.getKey(config.getGitRepository().getId());
    }

    private Job createIndexationJob(GitConfiguration config) {
        return new Job() {
            @Override
            public JobKey getKey() {
                return getGitIndexationJobKey(config);
            }

            @Override
            public JobRun getTask() {
                return (runListener -> index(config, runListener));
            }

            @Override
            public String getDescription() {
                return format(
                        "%s (%s @ %s)",
                        config.getRemote(),
                        config.getName(),
                        config.getType()
                );
            }

            @Override
            public boolean isDisabled() {
                return false;
            }
        };
    }

    protected <T> void buildSync(Branch branch, GitBranchConfiguration branchConfiguration, JobRunListener listener) {
        listener.message("Git build/tag sync for %s/%s", branch.getProject().getName(), branch.getName());
        GitConfiguration configuration = branchConfiguration.getConfiguration();
        // Gets the branch Git client
        GitRepositoryClient gitClient = gitRepositoryClientFactory.getClient(configuration.getGitRepository());
        // Link
        @SuppressWarnings("unchecked")
        IndexableBuildGitCommitLink<T> link = (IndexableBuildGitCommitLink<T>) branchConfiguration.getBuildCommitLink().getLink();
        @SuppressWarnings("unchecked")
        T linkData = (T) branchConfiguration.getBuildCommitLink().getData();
        // Configuration for the sync
        Property<GitBranchConfigurationProperty> confProperty = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
        boolean override = !confProperty.isEmpty() && confProperty.getValue().isOverride();
        // Makes sure of synchronization
        listener.message("Synchronizing before importing");
        syncAndWait(configuration);
        // Gets the list of tags
        listener.message("Getting list of tags");
        Collection<GitTag> tags = gitClient.getTags();
        // Creates the builds
        listener.message("Creating builds from tags");
        for (GitTag tag : tags) {
            String tagName = tag.getName();
            // Filters the tags according to the branch tag pattern
            link.getBuildNameFromTagName(tagName, linkData).ifPresent(buildNameCandidate -> {
                String buildName = NameDescription.escapeName(buildNameCandidate);
                listener.message(format("Build %s from tag %s", buildName, tagName));
                // Existing build?
                boolean createBuild;
                Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
                if (build.isPresent()) {
                    if (override) {
                        // Deletes the build
                        listener.message("Deleting existing build %s", buildName);
                        structureService.deleteBuild(build.get().getId());
                        createBuild = true;
                    } else {
                        // Keeps the build
                        listener.message("Build %s already exists", buildName);
                        createBuild = false;
                    }
                } else {
                    createBuild = true;
                }
                // Actual creation
                if (createBuild) {
                    listener.message("Creating build %s from tag %s", buildName, tagName);
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    new NameDescription(
                                            buildName,
                                            "Imported from Git tag " + tagName
                                    ),
                                    securityService.getCurrentSignature().withTime(
                                            tag.getTime()
                                    )
                            )
                    );
                }
            });
        }
    }

    private void index(GitConfiguration config, JobRunListener listener) {
        listener.message("Git sync for %s", config.getName());
        // Gets the client for this configuration
        GitRepositoryClient client = gitRepositoryClientFactory.getClient(config.getGitRepository());
        // Launches the synchronisation
        client.sync(listener.logger());
    }

    private JobRegistration getGitIndexationJobRegistration(GitConfiguration configuration) {
        return JobRegistration
                .of(createIndexationJob(configuration))
                .everyMinutes(configuration.getIndexationInterval());
    }

    @Override
    public void scheduleGitBuildSync(Branch branch, GitBranchConfigurationProperty property) {
        if (property.getBuildTagInterval() > 0) {
            jobScheduler.schedule(
                    createBuildSyncJob(branch),
                    Schedule.everyMinutes(property.getBuildTagInterval())
            );
        } else {
            unscheduleGitBuildSync(branch, property);
        }
    }

    @Override
    public void unscheduleGitBuildSync(Branch branch, GitBranchConfigurationProperty property) {
        jobScheduler.unschedule(getGitBranchSyncJobKey(branch));
    }

    @Override
    public Optional<SCMPathInfo> getSCMPathInfo(Branch branch) {
        return getBranchConfiguration(branch)
                .map(gitBranchConfiguration ->
                        new SCMPathInfo(
                                "git",
                                gitBranchConfiguration.getConfiguration().getRemote(),
                                gitBranchConfiguration.getBranch(),
                                null
                        )
                );
    }
}
