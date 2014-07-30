package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.client.*;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceNotConfiguredException;
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;
import net.nemerosa.ontrack.extension.scm.service.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class GitServiceImpl extends AbstractSCMChangeLogService<GitConfiguration, GitBuildInfo, GitChangeLogIssue> implements GitService, JobProvider {

    private final Collection<GitConfigurator> configurators;
    private final GitClientFactory gitClientFactory;
    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final JobQueueService jobQueueService;
    private final SecurityService securityService;
    private final TransactionService transactionService;

    @Autowired
    public GitServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            Collection<GitConfigurator> configurators,
            GitClientFactory gitClientFactory,
            IssueServiceRegistry issueServiceRegistry,
            JobQueueService jobQueueService,
            SecurityService securityService,
            TransactionService transactionService) {
        super(structureService, propertyService);
        this.configurators = configurators;
        this.gitClientFactory = gitClientFactory;
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.jobQueueService = jobQueueService;
        this.securityService = securityService;
        this.transactionService = transactionService;
    }

    @Override
    public Collection<Job> getJobs() {
        Collection<Job> jobs = new ArrayList<>();
        for (Project project : structureService.getProjectList()) {
            for (Branch branch : structureService.getBranchesForProject(project.getId())) {
                GitConfiguration configuration = getBranchConfiguration(branch);
                if (configuration.isValid()) {
                    // Indexation job
                    if (configuration.getIndexationInterval() > 0) {
                        jobs.add(createIndexationJob(configuration));
                    }
                    // Build/tag sync job
                    Property<GitBranchConfigurationProperty> branchConfigurationProperty = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
                    if (!branchConfigurationProperty.isEmpty() && branchConfigurationProperty.getValue().getBuildTagInterval() > 0) {
                        jobs.add(createBuildSyncJob(branch, configuration));
                    }
                }
            }
        }
        return jobs;
    }

    @Override
    public boolean isBranchConfiguredForGit(Branch branch) {
        return getBranchConfiguration(branch).isValid();
    }

    @Override
    public Ack launchBuildSync(ID branchId) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Gets its configuration
        GitConfiguration configuration = getBranchConfiguration(branch);
        // If valid, launches a job
        if (configuration.isValid()) {
            return jobQueueService.queue(createBuildSyncJob(branch, configuration));
        }
        // Else, nothing has happened
        else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    public GitChangeLog changeLog(BuildDiffRequest request) {
        try (Transaction ignored = transactionService.start()) {
            Branch branch = structureService.getBranch(request.getBranch());
            GitConfiguration configuration = getBranchConfiguration(branch);
            return new GitChangeLog(
                    UUID.randomUUID().toString(),
                    branch,
                    configuration,
                    getSCMBuildView(request.getFrom()),
                    getSCMBuildView(request.getTo())
            );
        }
    }

    @Override
    public GitChangeLogCommits getChangeLogCommits(GitChangeLog changeLog) {
        // Gets the client client for this branch
        GitClient gitClient = gitClientFactory.getClient(changeLog.getScmBranch());
        // Gets the configuration
        GitConfiguration gitConfiguration = changeLog.getScmBranch();
        // Gets the tag boundaries
        String tagFrom = changeLog.getFrom().getBuild().getName();
        String tagTo = changeLog.getTo().getBuild().getName();
        // Tag pattern
        String tagPattern = gitConfiguration.getTagPattern();
        if (StringUtils.isNotBlank(tagPattern)) {
            tagFrom = StringUtils.replace(tagPattern, "*", tagFrom);
            tagTo = StringUtils.replace(tagPattern, "*", tagTo);
        }
        // Gets the commits
        GitLog log = gitClient.log(tagFrom, tagTo);
        List<GitCommit> commits = log.getCommits();
        List<GitUICommit> uiCommits = toUICommits(gitConfiguration, commits);
        return new GitChangeLogCommits(
                new GitUILog(
                        log.getPlot(),
                        uiCommits
                )
        );
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
            GitConfiguration configuration = changeLog.getScmBranch();
            // Issue service
            ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier());
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
                        existingIssue = GitChangeLogIssue.of(issue, gitUICommit);
                        issues.put(key, existingIssue);
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
        // Gets the client client for this branch
        GitClient gitClient = gitClientFactory.getClient(changeLog.getScmBranch());
        // Gets the configuration
        GitConfiguration gitConfiguration = gitClient.getConfiguration();
        // Gets the tag boundaries
        String tagFrom = changeLog.getFrom().getBuild().getName();
        String tagTo = changeLog.getTo().getBuild().getName();
        // Tag pattern
        String tagPattern = gitConfiguration.getTagPattern();
        if (StringUtils.isNotBlank(tagPattern)) {
            tagFrom = StringUtils.replace(tagPattern, "*", tagFrom);
            tagTo = StringUtils.replace(tagPattern, "*", tagTo);
        }
        // Diff
        final GitDiff diff = gitClient.diff(tagFrom, tagTo);
        // File change links
        String fileChangeLinkFormat = gitConfiguration.getFileAtCommitLink();
        // OK
        return new GitChangeLogFiles(
                diff.getEntries().stream()
                        .map(entry -> toChangeLogFile(entry).withUrl(
                                fileChangeLinkFormat
                                        .replace("{commit}", entry.getReferenceId(diff.getFrom(), diff.getTo()))
                                        .replace("{path}", entry.getReferencePath())
                        ))
                        .collect(Collectors.toList())
        );
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
        List<? extends MessageAnnotator> messageAnnotators;
        String issueServiceConfigurationIdentifier = gitConfiguration.getIssueServiceConfigurationIdentifier();
        if (StringUtils.isNotBlank(issueServiceConfigurationIdentifier)) {
            ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(issueServiceConfigurationIdentifier);
            if (configuredIssueService != null) {
                // Gets the message annotator
                Optional<MessageAnnotator> messageAnnotator = configuredIssueService.getMessageAnnotator();
                // If present annotate the messages
                if (messageAnnotator.isPresent()) {
                    messageAnnotators = Collections.singletonList(messageAnnotator.get());
                } else {
                    messageAnnotators = Collections.emptyList();
                }
            } else {
                messageAnnotators = Collections.emptyList();
            }
        } else {
            messageAnnotators = Collections.emptyList();
        }
        // OK
        return commits.stream()
                .map(commit -> new GitUICommit(
                        commit,
                        MessageAnnotationUtils.annotate(commit.getShortMessage(), messageAnnotators),
                        MessageAnnotationUtils.annotate(commit.getFullMessage(), messageAnnotators),
                        StringUtils.replace(commitLink, "{commit}", commit.getId())
                ))
                .collect(Collectors.toList());
    }

    private SCMBuildView<GitBuildInfo> getSCMBuildView(ID buildId) {
        return new SCMBuildView<>(getBuildView(buildId), new GitBuildInfo());
    }

    private GitConfiguration getBranchConfiguration(Branch branch) {
        // Empty configuration
        GitConfiguration configuration = GitConfiguration.empty();
        // Configurators{
        for (GitConfigurator configurator : configurators) {
            configuration = configurator.configure(configuration, branch);
        }
        // Unique name
        if (StringUtils.isNotBlank(configuration.getRemote())) {
            configuration = configuration.withName(
                    format(
                            "%s/%s/%s",
                            branch.getProject().getName(),
                            branch.getName(),
                            configuration.getRemote()
                    )
            );
        }
        // OK
        return configuration;
    }

    private Job createBuildSyncJob(Branch branch, GitConfiguration configuration) {
        return new Job() {
            @Override
            public String getCategory() {
                return "GitBuildTagSync";
            }

            @Override
            public String getId() {
                return String.valueOf(branch.getId());
            }

            @Override
            public String getDescription() {
                return format(
                        "Git build/tag synchro for branch %s/%s",
                        branch.getProject().getName(),
                        branch.getName()
                );
            }

            @Override
            public int getInterval() {
                return configuration.getIndexationInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(info -> buildSync(branch, configuration, info));
            }
        };
    }

    private Job createIndexationJob(GitConfiguration config) {
        return new Job() {
            @Override
            public String getCategory() {
                return "GitIndexation";
            }

            @Override
            public String getId() {
                return config.getName();
            }

            @Override
            public String getDescription() {
                return format(
                        "Git indexation for %s",
                        config.getName()
                );
            }

            @Override
            public int getInterval() {
                return config.getIndexationInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(
                        info -> index(config, info)
                );
            }
        };
    }

    private void buildSync(Branch branch, GitConfiguration configuration, JobInfoListener info) {
        info.post(format("Git build/tag sync for %s/%s", branch.getProject().getName(), branch.getName()));
        // Gets the branch Git client
        GitClient gitClient = gitClientFactory.getClient(configuration);
        // Configuration for the sync
        boolean override = false;
        Property<GitBranchConfigurationProperty> confProperty = propertyService.getProperty(branch, GitBranchConfigurationPropertyType.class);
        if (!confProperty.isEmpty()) {
            override = confProperty.getValue().isOverride();
        }
        // Makes sure of synchronization
        info.post("Synchronizing before importing");
        gitClient.sync(info::post);
        // Gets the list of tags
        info.post("Getting list of tags");
        Collection<GitTag> tags = gitClient.getTags();
        // Pattern for the tags
        String tagExpression = "(.*)";
        if (StringUtils.isNotBlank(configuration.getTagPattern())) {
            tagExpression = configuration.getTagPattern().replace("*", "(.*)");
        }
        final Pattern tagPattern = Pattern.compile(tagExpression);
        // Creates the builds
        info.post("Creating builds from tags");
        for (GitTag tag : tags) {
            String tagName = tag.getName();
            // Filters the tags according to the branch tag pattern
            Matcher matcher = tagPattern.matcher(tagName);
            if (matcher.matches()) {
                // Build name
                info.post(format("Creating build for tag %s", tagName));
                String buildName = matcher.group(1);
                info.post(format("Build %s from tag %s", buildName, tagName));
                // Existing build?
                boolean createBuild;
                Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
                if (build.isPresent()) {
                    if (override) {
                        // Deletes the build
                        info.post(format("Deleting existing build %s", buildName));
                        structureService.deleteBuild(build.get().getId());
                        createBuild = true;
                    } else {
                        // Keeps the build
                        info.post(format("Build %s already exists", buildName));
                        createBuild = false;
                    }
                } else {
                    createBuild = true;
                }
                // Actual creation
                if (createBuild) {
                    info.post(format("Creating build %s from tag %s", buildName, tagName));
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
            }
        }
    }

    private void index(GitConfiguration config, JobInfoListener info) {
        info.post(format("Git sync for %s", config.getName()));
        // Gets the client for this configuration
        GitClient client = gitClientFactory.getClient(config);
        // Launches the synchronisation
        client.sync(info::post);
    }

}
