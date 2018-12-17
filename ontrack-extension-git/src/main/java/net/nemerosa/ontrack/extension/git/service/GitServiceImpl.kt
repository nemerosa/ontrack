package net.nemerosa.ontrack.extension.git.service

import com.google.common.collect.Lists
import net.nemerosa.ontrack.common.FutureUtils
import net.nemerosa.ontrack.common.asOptional
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequestDifferenceProjectException
import net.nemerosa.ontrack.extension.git.branching.BranchingModelService
import net.nemerosa.ontrack.extension.git.model.*
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.repository.GitRepositoryHelper
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceNotConfiguredException
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo
import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo
import net.nemerosa.ontrack.extension.scm.service.AbstractSCMChangeLogService
import net.nemerosa.ontrack.extension.scm.service.SCMUtilsService
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.git.exceptions.GitRepositorySyncException
import net.nemerosa.ontrack.git.model.*
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.*
import net.nemerosa.ontrack.tx.TransactionService
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.String.format
import java.util.*
import java.util.concurrent.Future
import java.util.function.BiConsumer
import java.util.stream.Stream

@Service
@Transactional
class GitServiceImpl(
        structureService: StructureService,
        propertyService: PropertyService,
        private val jobScheduler: JobScheduler,
        private val securityService: SecurityService,
        private val transactionService: TransactionService,
        private val applicationLogService: ApplicationLogService,
        private val gitRepositoryClientFactory: GitRepositoryClientFactory,
        private val buildGitCommitLinkService: BuildGitCommitLinkService,
        private val gitConfigurators: Collection<GitConfigurator>,
        private val scmService: SCMUtilsService,
        private val gitRepositoryHelper: GitRepositoryHelper,
        private val branchingModelService: BranchingModelService
) : AbstractSCMChangeLogService<GitConfiguration, GitBuildInfo, GitChangeLogIssue>(structureService, propertyService), GitService, JobOrchestratorSupplier {

    private val logger = LoggerFactory.getLogger(GitService::class.java)

    override fun forEachConfiguredProject(consumer: BiConsumer<Project, GitConfiguration>) {
        structureService.projectList
                .forEach { project ->
                    val configuration = getProjectConfiguration(project)
                    if (configuration != null) {
                        consumer.accept(project, configuration)
                    }
                }
    }

    override fun forEachConfiguredBranch(consumer: BiConsumer<Branch, GitBranchConfiguration>) {
        for (project in structureService.projectList) {
            structureService.getBranchesForProject(project.id)
                    .filter { branch -> branch.type != BranchType.TEMPLATE_DEFINITION }
                    .forEach { branch ->
                        val configuration = getBranchConfiguration(branch)
                        if (configuration != null) {
                            consumer.accept(branch, configuration)
                        }
                    }
        }
    }

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        val jobs = ArrayList<JobRegistration>()
        // Indexation of repositories, based on projects actually linked
        forEachConfiguredProject(BiConsumer { _, configuration -> jobs.add(getGitIndexationJobRegistration(configuration)) })
        // Synchronisation of branch builds with tags when applicable
        forEachConfiguredBranch(BiConsumer { branch, branchConfiguration ->
            // Build/tag sync job
            if (branchConfiguration.buildTagInterval > 0 && branchConfiguration.buildCommitLink?.link is IndexableBuildGitCommitLink<*>) {
                jobs.add(
                        JobRegistration.of(createBuildSyncJob(branch))
                                .everyMinutes(branchConfiguration.buildTagInterval.toLong())
                )
            }
        })
        // OK
        return jobs.stream()
    }

    override fun isBranchConfiguredForGit(branch: Branch): Boolean {
        return getBranchConfiguration(branch) != null
    }

    override fun launchBuildSync(branchId: ID, synchronous: Boolean): Future<*>? {
        // Gets the branch
        val branch = structureService.getBranch(branchId)
        // Gets its configuration
        val branchConfiguration = getBranchConfiguration(branch)
        // If valid, launches a job
        return if (branchConfiguration != null && branchConfiguration.buildCommitLink?.link is IndexableBuildGitCommitLink<*>) {
            if (synchronous) {
                buildSync<Any>(branch, branchConfiguration, JobRunListener.logger(logger))
                null
            } else {
                jobScheduler.fireImmediately(getGitBranchSyncJobKey(branch)).orElse(null)
            }
        } else {
            null
        }
    }

    @Transactional
    override fun changeLog(request: BuildDiffRequest): GitChangeLog {
        transactionService.start().use { ignored ->
            // Gets the two builds
            var buildFrom = structureService.getBuild(request.from)
            var buildTo = structureService.getBuild(request.to)
            // Ordering of builds
            if (buildFrom.id() > buildTo.id()) {
                val t = buildFrom
                buildFrom = buildTo
                buildTo = t
            }
            // Gets the two associated projects
            val project = buildFrom.branch.project
            val otherProject = buildTo.branch.project
            // Checks the project
            if (project.id() != otherProject.id()) {
                throw BuildDiffRequestDifferenceProjectException()
            }
            // Project Git configuration
            val oProjectConfiguration = getProjectConfiguration(project)
            if (oProjectConfiguration != null) {
                // Forces Git sync before
                var syncError: Boolean
                try {
                    syncAndWait(oProjectConfiguration)
                    syncError = false
                } catch (ex: GitRepositorySyncException) {
                    applicationLogService.log(
                            ApplicationLogEntry.error(
                                    ex,
                                    NameDescription.nd(
                                            "git-sync",
                                            "Git synchronisation issue"
                                    ),
                                    oProjectConfiguration.remote
                            ).withDetail("project", project.name)
                                    .withDetail("git-name", oProjectConfiguration.name)
                                    .withDetail("git-remote", oProjectConfiguration.remote)
                    )
                    syncError = true
                }

                // Change log computation
                return GitChangeLog(
                        UUID.randomUUID().toString(),
                        project,
                        getSCMBuildView(buildFrom.id),
                        getSCMBuildView(buildTo.id),
                        syncError
                )
            } else {
                throw GitProjectNotConfiguredException(project.id)
            }
        }
    }

    protected fun syncAndWait(gitConfiguration: GitConfiguration): Any? {
        return FutureUtils.wait("Synchronisation for " + gitConfiguration.name, sync(gitConfiguration, GitSynchronisationRequest.SYNC))
    }

    protected fun getRequiredProjectConfiguration(project: Project): GitConfiguration {
        return getProjectConfiguration(project) ?: throw GitProjectNotConfiguredException(project.id)
    }

    protected fun getGitRepositoryClient(project: Project): GitRepositoryClient {
        return getProjectConfiguration(project)?.gitRepository
                ?.let { gitRepositoryClientFactory.getClient(it) }
                ?: throw GitProjectNotConfiguredException(project.id)
    }

    override fun getChangeLogCommits(changeLog: GitChangeLog): GitChangeLogCommits {
        // Gets the client
        val client = getGitRepositoryClient(changeLog.project)
        // Gets the build boundaries
        val buildFrom = changeLog.from.build
        val buildTo = changeLog.to.build
        // Commit boundaries
        var commitFrom = getCommitFromBuild(buildFrom)
        var commitTo = getCommitFromBuild(buildTo)
        // Gets the commits
        var log = client.graph(commitFrom, commitTo)
        // If log empty, inverts the boundaries
        if (log.commits.isEmpty()) {
            val t = commitFrom
            commitFrom = commitTo
            commitTo = t
            log = client.graph(commitFrom, commitTo)
        }
        // Consolidation to UI
        val commits = log.commits
        val uiCommits = toUICommits(getRequiredProjectConfiguration(changeLog.project), commits)
        return GitChangeLogCommits(
                GitUILog(
                        log.plot,
                        uiCommits
                )
        )
    }

    protected fun getCommitFromBuild(build: Build): String {
        return getBranchConfiguration(build.branch)
                ?.buildCommitLink
                ?.getCommitFromBuild(build)
                ?: throw GitBranchNotConfiguredException(build.branch.id)
    }

    override fun getChangeLogIssues(changeLog: GitChangeLog): GitChangeLogIssues {
        // Commits must have been loaded first
        val commits: GitChangeLogCommits = changeLog.loadCommits {
            getChangeLogCommits(it)
        }
        // In a transaction
        transactionService.start().use { _ ->
            // Configuration
            val configuration = getRequiredProjectConfiguration(changeLog.project)
            // Issue service
            val configuredIssueService = configuration.configuredIssueService.orElse(null)
                    ?: throw IssueServiceNotConfiguredException()
            // Index of issues, sorted by keys
            val issues = TreeMap<String, GitChangeLogIssue>()
            // For all commits in this commit log
            for (gitUICommit in commits.log.commits) {
                val keys = configuredIssueService.extractIssueKeysFromMessage(gitUICommit.commit.fullMessage)
                for (key in keys) {
                    var existingIssue: GitChangeLogIssue? = issues[key]
                    if (existingIssue != null) {
                        existingIssue.add(gitUICommit)
                    } else {
                        val issue = configuredIssueService.getIssue(key)
                        if (issue != null) {
                            existingIssue = GitChangeLogIssue.of(issue, gitUICommit)
                            issues[key] = existingIssue
                        }
                    }
                }
            }
            // List of issues
            val issuesList = ArrayList(issues.values)
            // Issues link
            val issueServiceConfiguration = configuredIssueService.issueServiceConfigurationRepresentation
            // OK
            return GitChangeLogIssues(issueServiceConfiguration, issuesList)

        }
    }

    override fun getChangeLogFiles(changeLog: GitChangeLog): GitChangeLogFiles {
        // Gets the configuration
        val configuration = getRequiredProjectConfiguration(changeLog.project)
        // Gets the client for this project
        val client = gitRepositoryClientFactory.getClient(configuration.gitRepository)
        // Gets the build boundaries
        val buildFrom = changeLog.from.build
        val buildTo = changeLog.to.build
        // Commit boundaries
        val commitFrom = getCommitFromBuild(buildFrom)
        val commitTo = getCommitFromBuild(buildTo)
        // Diff
        val diff = client.diff(commitFrom, commitTo)
        // File change links
        val fileChangeLinkFormat = configuration.fileAtCommitLink
        // OK
        return GitChangeLogFiles(
                diff.entries.map { entry ->
                    toChangeLogFile(entry).withUrl(
                            getDiffUrl(diff, entry, fileChangeLinkFormat)
                    )
                }
        )
    }

    override fun isPatternFound(branchConfiguration: GitBranchConfiguration, token: String): Boolean {
        // Gets the client
        val client = gitRepositoryClientFactory.getClient(branchConfiguration.configuration.gitRepository)
        // Scanning
        return client.isPatternFound(token)
    }

    override fun getIssueInfo(branchId: ID, key: String): OntrackGitIssueInfo? {
        TODO("This method must be removed")
    }

    private fun collectIssueCommitInfos(branch: Branch, branchConfiguration: GitBranchConfiguration, issue: Issue): List<OntrackGitIssueCommitInfo> {
        // Index of commit infos
        val commitInfos = LinkedHashMap<String, OntrackGitIssueCommitInfo>()
        // Gets the branch configuration
        val configuration = branchConfiguration.configuration
        // Gets the Git client for this project
        val client = gitRepositoryClientFactory.getClient(configuration.gitRepository)
        // Issue service
        val configuredIssueService = configuration.configuredIssueService.orElse(null)
        if (configuredIssueService != null) {
            // Regular expression to identify the issue and all its linked issues
            val allKeys = configuredIssueService.getLinkedIssues(branch.project, issue)
                    .map { it.displayKey }
                    .toMutableSet()
            allKeys.add(issue.displayKey)
            val regex = allKeys.joinToString("|")
            // Gets the first commit whose message contains this issue and its linked issues
            val revCommit = client.findCommitForRegex(branchConfiguration.branch, regex)
            // If at least one commit
            if (revCommit != null) {
                // Commit explained (independent from the branch)
                val commit = client.toCommit(revCommit)
                val commitId = commit.id
                // Gets any existing commit info
                var commitInfo: OntrackGitIssueCommitInfo? = commitInfos[commitId]
                // If not defined, creates an entry
                if (commitInfo == null) {
                    // UI commit (independent from the branch)
                    val uiCommit = toUICommit(
                            configuration.commitLink,
                            getMessageAnnotators(configuration),
                            commit
                    )
                    // Commit info
                    commitInfo = OntrackGitIssueCommitInfo.of(uiCommit)
                    // Indexation
                    commitInfos[commitId] = commitInfo
                }
                // Collects branch info
                var branchInfo = SCMIssueCommitBranchInfo.of(branch)
                // Gets the last build for this branch
                val buildAfterCommit = getEarliestBuildAfterCommit(commitId, branch, branchConfiguration, client)
                branchInfo = scmService.getBranchInfo(buildAfterCommit, branchInfo)
                // Adds the info
                commitInfo!!.add(branchInfo)
            }
        }
        // OK
        return Lists.newArrayList(commitInfos.values)
    }

    override fun lookupCommit(configuration: GitConfiguration, id: String): GitCommit? {
        // Gets the client client for this configuration
        val gitClient = gitRepositoryClientFactory.getClient(configuration.gitRepository)
        // Gets the commit
        return gitClient.getCommitFor(id)
    }

    override fun getCommitProjectInfo(projectId: ID, commit: String): OntrackGitCommitInfo {
        return getOntrackGitCommitInfo(structureService.getProject(projectId), commit)
    }

    override fun getRemoteBranches(gitConfiguration: GitConfiguration): List<String> {
        val gitClient = gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository)
        return gitClient.remoteBranches
    }

    override fun diff(changeLog: GitChangeLog, patterns: List<String>): String {
        // Gets the client client for this configuration`
        val gitClient = getGitRepositoryClient(changeLog.project)
        // Path predicate
        val pathFilter = scmService.getPathFilter(patterns)
        // Gets the build boundaries
        val buildFrom = changeLog.from.build
        val buildTo = changeLog.to.build
        // Commit boundaries
        val commitFrom = getCommitFromBuild(buildFrom)
        val commitTo = getCommitFromBuild(buildTo)
        // Gets the diff
        return gitClient.unifiedDiff(
                commitFrom,
                commitTo,
                pathFilter
        )
    }

    override fun download(branch: Branch, path: String): Optional<String> {
        securityService.checkProjectFunction(branch, ProjectConfig::class.java)
        return transactionService.doInTransaction {
            val branchConfiguration = getRequiredBranchConfiguration(branch)
            val client = gitRepositoryClientFactory.getClient(
                    branchConfiguration.configuration.gitRepository
            )
            client.download(branchConfiguration.branch, path).asOptional()
        }
    }

    override fun projectSync(project: Project, request: GitSynchronisationRequest): Ack {
        securityService.checkProjectFunction(project, ProjectConfig::class.java)
        val projectConfiguration = getProjectConfiguration(project)
        if (projectConfiguration != null) {
            val sync = sync(projectConfiguration, request)
            return Ack.validate(sync != null)
        } else {
            return Ack.NOK
        }
    }

    override fun sync(gitConfiguration: GitConfiguration, request: GitSynchronisationRequest): Future<*>? {
        // Reset the repository?
        if (request.isReset) {
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).reset()
        }
        // Schedules the job
        return jobScheduler.fireImmediately(getGitIndexationJobKey(gitConfiguration)).orElse(null)
    }

    override fun getProjectGitSyncInfo(project: Project): GitSynchronisationInfo {
        securityService.checkProjectFunction(project, ProjectConfig::class.java)
        return getProjectConfiguration(project)
                ?.let { getGitSynchronisationInfo(it) }
                ?: throw GitProjectNotConfiguredException(project.id)
    }

    private fun getGitSynchronisationInfo(gitConfiguration: GitConfiguration): GitSynchronisationInfo {
        // Gets the client for this configuration
        val client = gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository)
        // Gets the status
        val status = client.synchronisationStatus
        // Collects the branch info
        val branches: List<GitBranchInfo> = if (status == GitSynchronisationStatus.IDLE) {
            client.branches.branches
        } else {
            emptyList()
        }
        // OK
        return GitSynchronisationInfo(
                gitConfiguration.type,
                gitConfiguration.name,
                gitConfiguration.remote,
                gitConfiguration.indexationInterval,
                status,
                branches
        )
    }

    override fun getIssueProjectInfo(projectId: ID, key: String): OntrackGitIssueInfo? {
        // Gets the project
        val project = structureService.getProject(projectId)
        // Gets the project configuration
        val projectConfiguration = getRequiredProjectConfiguration(project)
        // Issue service
        val configuredIssueService: ConfiguredIssueService? = projectConfiguration.configuredIssueService.orElse(null)
        // Gets the details about the issue
        val issue: Issue? = configuredIssueService?.getIssue(key)
        // If no issue, no info
        if (issue == null) {
            return null
        } else {
            // Gets a client for this project
            val repositoryClient: GitRepositoryClient = gitRepositoryClientFactory.getClient(projectConfiguration.gitRepository)
            // Regular expression for the issue
            val regex = configuredIssueService.getMessageRegex(issue)
            // Now, get the last commit for this issue
            val commit = repositoryClient.getLastCommitForExpression(regex)
            // If commit is found, we collect the commit info
            return if (commit != null) {
                val commitInfo = getOntrackGitCommitInfo(project, commit)
                // We now return the commit info together with the issue
                OntrackGitIssueInfo(
                        configuredIssueService.issueServiceConfigurationRepresentation,
                        issue,
                        commitInfo
                )
            }
            // If not found, no commit info
            else {
                OntrackGitIssueInfo(
                        configuredIssueService.issueServiceConfigurationRepresentation,
                        issue,
                        null
                )
            }
        }
    }

    private fun getOntrackGitCommitInfo(project: Project, commit: String): OntrackGitCommitInfo {
        // Gets the project configuration
        val projectConfiguration = getRequiredProjectConfiguration(project)
        // Gets a client for this configuration
        val repositoryClient = gitRepositoryClientFactory.getClient(projectConfiguration.gitRepository)

        // Gets the commit
        val commitObject = repositoryClient.getCommitFor(commit) ?: throw GitCommitNotFoundException(commit)
        // Gets the annotated commit
        val messageAnnotators = getMessageAnnotators(projectConfiguration)
        val uiCommit = toUICommit(
                projectConfiguration.commitLink,
                messageAnnotators,
                commitObject
        )

        // Looks for all Git branches for this commit
        val gitBranches = repositoryClient.getBranchesForCommit(commit)
        // Converts to Ontrack branches (non templates)
        val branches = gitBranches.mapNotNull { findBranchWithGitBranch(project, it) }

        // Index of first build per branch
        // Used to avoid its recomputation when looking for indexed branch information
        val firstBuilds = mutableMapOf<Int, Build>()

        // Getting the very first build in all the branches (promotion of not)
        // The branch it belongs to might very well be disabled
        val firstBuild = branches.mapNotNull { branch ->
            // Gets its Git configuration
            val branchConfiguration = getRequiredBranchConfiguration(branch)
            // Gets the earliest build on this branch that contains this commit
            val build = getEarliestBuildAfterCommit(commit, branch, branchConfiguration, repositoryClient)
            // Indexation
            if (build != null) {
                firstBuilds[branch.id()] = build
            }
            // OK
            build
        }.sortedBy { it.id() }.firstOrNull()

        // Sorts the branches according to the branching model
        val indexedBranches = branchingModelService.getBranchingModel(project)
                .groupBranches(branches) { getBranchConfiguration(it)?.branch }

        // For every indexation group of branches
        val branchInfos = indexedBranches.mapValues { (_, branches) ->
            branches.map { branch ->
                // Gets the first build for this branch (using index)
                val firstBuildOnThisBranch = firstBuilds[branch.id()]
                // Promotions
                val promotions: List<PromotionRun> = firstBuildOnThisBranch?.let { build ->
                    structureService.getPromotionLevelListForBranch(branch.id)
                            .mapNotNull { promotionLevel ->
                                structureService.getEarliestPromotionRunAfterBuild(promotionLevel, build).orElse(null)
                            }
                } ?: emptyList()
                // Complete branch info
                BranchInfo(
                        branch,
                        firstBuildOnThisBranch,
                        promotions
                )
            }
        }.mapValues { (_, infos) ->
            infos.filter { !it.isEmpty }
        }.filterValues {
            !it.isEmpty()
        }
        // Result
        return OntrackGitCommitInfo(
                uiCommit,
                firstBuild,
                branchInfos
        )
    }

    internal fun getEarliestBuildAfterCommit(commit: String, branch: Branch, branchConfiguration: GitBranchConfiguration, client: GitRepositoryClient): Build? {
        val configuredBuildGitCommitLink: ConfiguredBuildGitCommitLink<*>? = branchConfiguration.buildCommitLink
        return configuredBuildGitCommitLink?.let {
            getEarliestBuildAfterCommit(commit, branch, branchConfiguration, client, it)
        }
    }

    private fun <T> getEarliestBuildAfterCommit(commit: String, branch: Branch, branchConfiguration: GitBranchConfiguration, client: GitRepositoryClient, configuredBuildGitCommitLink: ConfiguredBuildGitCommitLink<T>): Build? {
        // Delegates to the build commit link...
        val buildId = configuredBuildGitCommitLink.link.getEarliestBuildAfterCommit(
                branch,
                client,
                branchConfiguration,
                configuredBuildGitCommitLink.data,
                commit
        )
        // Loading
        return buildId?.let { structureService.getBuild(ID.of(it)) }
    }

    private fun getDiffUrl(diff: GitDiff, entry: GitDiffEntry, fileChangeLinkFormat: String): String {
        return if (StringUtils.isNotBlank(fileChangeLinkFormat)) {
            fileChangeLinkFormat
                    .replace("{commit}", entry.getReferenceId(diff.from, diff.to))
                    .replace("{path}", entry.referencePath)
        } else {
            ""
        }
    }

    private fun toChangeLogFile(entry: GitDiffEntry): GitChangeLogFile {
        return when (entry.changeType) {
            GitChangeType.ADD -> GitChangeLogFile.of(SCMChangeLogFileChangeType.ADDED, entry.newPath)
            GitChangeType.COPY -> GitChangeLogFile.of(SCMChangeLogFileChangeType.COPIED, entry.oldPath, entry.newPath)
            GitChangeType.DELETE -> GitChangeLogFile.of(SCMChangeLogFileChangeType.DELETED, entry.oldPath)
            GitChangeType.MODIFY -> GitChangeLogFile.of(SCMChangeLogFileChangeType.MODIFIED, entry.oldPath)
            GitChangeType.RENAME -> GitChangeLogFile.of(SCMChangeLogFileChangeType.RENAMED, entry.oldPath, entry.newPath)
            else -> GitChangeLogFile.of(SCMChangeLogFileChangeType.UNDEFINED, entry.oldPath, entry.newPath)
        }
    }

    private fun toUICommits(gitConfiguration: GitConfiguration, commits: List<GitCommit>): List<GitUICommit> {
        // Link?
        val commitLink = gitConfiguration.commitLink
        // Issue-based annotations
        val messageAnnotators = getMessageAnnotators(gitConfiguration)
        // OK
        return commits.map { commit -> toUICommit(commitLink, messageAnnotators, commit) }
    }

    private fun toUICommit(commitLink: String, messageAnnotators: List<MessageAnnotator>, commit: GitCommit): GitUICommit {
        return GitUICommit(
                commit,
                MessageAnnotationUtils.annotate(commit.shortMessage, messageAnnotators),
                MessageAnnotationUtils.annotate(commit.fullMessage, messageAnnotators),
                StringUtils.replace(commitLink, "{commit}", commit.id)
        )
    }

    private fun getMessageAnnotators(gitConfiguration: GitConfiguration): List<MessageAnnotator> {
        val configuredIssueService = gitConfiguration.configuredIssueService.orElse(null)
        return if (configuredIssueService != null) {
            // Gets the message annotator
            val messageAnnotator = configuredIssueService.messageAnnotator
            // If present annotate the messages
            messageAnnotator.map { listOf(it) }.orElseGet { emptyList<MessageAnnotator?>() }
        } else {
            emptyList()
        }
    }

    private fun getSCMBuildView(buildId: ID): SCMBuildView<GitBuildInfo> {
        return SCMBuildView(getBuildView(buildId), GitBuildInfo())
    }

    override fun getProjectConfiguration(project: Project): GitConfiguration? {
        return gitConfigurators
                .map { c -> c.getConfiguration(project) }
                .filter { it.isPresent }
                .map { it.get() }
                .firstOrNull()
    }

    protected fun getRequiredBranchConfiguration(branch: Branch): GitBranchConfiguration {
        return getBranchConfiguration(branch)
                ?: throw GitBranchNotConfiguredException(branch.id)
    }

    override fun getBranchConfiguration(branch: Branch): GitBranchConfiguration? {
        // Get the configuration for the project
        val configuration = getProjectConfiguration(branch.project)
        if (configuration != null) {
            // Gets the configuration for a branch
            val gitBranch: String
            val buildCommitLink: ConfiguredBuildGitCommitLink<*>?
            val override: Boolean
            val buildTagInterval: Int
            val branchConfig = propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java)
            if (!branchConfig.isEmpty) {
                gitBranch = branchConfig.value.branch
                buildCommitLink = branchConfig.value.buildCommitLink?.let {
                    toConfiguredBuildGitCommitLink<Any>(it)
                }
                override = branchConfig.value.isOverride
                buildTagInterval = branchConfig.value.buildTagInterval
            } else {
                return null
            }
            // OK
            return GitBranchConfiguration(
                    configuration,
                    gitBranch,
                    buildCommitLink,
                    override,
                    buildTagInterval
            )
        } else {
            return null
        }
    }

    override fun findBranchWithGitBranch(project: Project, branchName: String): Branch? {
        return gitRepositoryHelper.findBranchWithProjectAndGitBranch(project, branchName)
                ?.let { structureService.getBranch(ID.of(it)) }
                ?.takeIf { it.type != BranchType.TEMPLATE_DEFINITION }
    }

    private fun <T> toConfiguredBuildGitCommitLink(serviceConfiguration: ServiceConfiguration): ConfiguredBuildGitCommitLink<T> {
        @Suppress("UNCHECKED_CAST")
        val link = buildGitCommitLinkService.getLink(serviceConfiguration.id) as BuildGitCommitLink<T>
        val linkData = link.parseData(serviceConfiguration.data)
        return ConfiguredBuildGitCommitLink(
                link,
                linkData
        )
    }

    private fun createBuildSyncJob(branch: Branch): Job {
        val configuration = getRequiredBranchConfiguration(branch)
        return object : AbstractBranchJob(structureService, branch) {

            override fun getKey(): JobKey {
                return getGitBranchSyncJobKey(branch)
            }

            override fun getTask(): JobRun {
                return JobRun { listener -> buildSync<Any>(branch, configuration, listener) }
            }

            override fun getDescription(): String {
                return format(
                        "Branch %s @ %s",
                        branch.name,
                        branch.project.name
                )
            }

            override fun isDisabled(): Boolean {
                return super.isDisabled() && isBranchConfiguredForGit(branch)
            }
        }
    }

    protected fun getGitBranchSyncJobKey(branch: Branch): JobKey {
        return GIT_BUILD_SYNC_JOB.getKey(branch.id.toString())
    }

    private fun getGitIndexationJobKey(config: GitConfiguration): JobKey {
        return GIT_INDEXATION_JOB.getKey(config.gitRepository.id)
    }

    private fun createIndexationJob(config: GitConfiguration): Job {
        return object : Job {
            override fun getKey(): JobKey {
                return getGitIndexationJobKey(config)
            }

            override fun getTask(): JobRun {
                return JobRun { runListener -> index(config, runListener) }
            }

            override fun getDescription(): String {
                return format(
                        "%s (%s @ %s)",
                        config.remote,
                        config.name,
                        config.type
                )
            }

            override fun isDisabled(): Boolean {
                return false
            }
        }
    }

    protected fun <T> buildSync(branch: Branch, branchConfiguration: GitBranchConfiguration, listener: JobRunListener) {
        listener.message("Git build/tag sync for %s/%s", branch.project.name, branch.name)
        val configuration = branchConfiguration.configuration
        // Gets the branch Git client
        val gitClient = gitRepositoryClientFactory.getClient(configuration.gitRepository)
        // Link
        @Suppress("UNCHECKED_CAST")
        val link = branchConfiguration.buildCommitLink?.link as IndexableBuildGitCommitLink<T>?
        @Suppress("UNCHECKED_CAST")
        val linkData = branchConfiguration.buildCommitLink?.data as T?
        // Check for configuration
        if (link == null || linkData == null) {
            listener.message("No commit link configuration on the branch - no synchronization.")
            return
        }
        // Configuration for the sync
        val confProperty = propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java)
        val override = !confProperty.isEmpty && confProperty.value.isOverride
        // Makes sure of synchronization
        listener.message("Synchronizing before importing")
        syncAndWait(configuration)
        // Gets the list of tags
        listener.message("Getting list of tags")
        val tags = gitClient.tags
        // Creates the builds
        listener.message("Creating builds from tags")
        for (tag in tags) {
            val tagName = tag.name
            // Filters the tags according to the branch tag pattern
            link.getBuildNameFromTagName(tagName, linkData).ifPresent { buildNameCandidate ->
                val buildName = NameDescription.escapeName(buildNameCandidate)
                listener.message(format("Build %s from tag %s", buildName, tagName))
                // Existing build?
                val build = structureService.findBuildByName(branch.project.name, branch.name, buildName)
                val createBuild: Boolean = if (build.isPresent) {
                    if (override) {
                        // Deletes the build
                        listener.message("Deleting existing build %s", buildName)
                        structureService.deleteBuild(build.get().id)
                        true
                    } else {
                        // Keeps the build
                        listener.message("Build %s already exists", buildName)
                        false
                    }
                } else {
                    true
                }
                // Actual creation
                if (createBuild) {
                    listener.message("Creating build %s from tag %s", buildName, tagName)
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    NameDescription(
                                            buildName,
                                            "Imported from Git tag $tagName"
                                    ),
                                    securityService.currentSignature.withTime(
                                            tag.time
                                    )
                            )
                    )
                }
            }
        }
    }

    private fun index(config: GitConfiguration, listener: JobRunListener) {
        listener.message("Git sync for %s", config.name)
        // Gets the client for this configuration
        val client = gitRepositoryClientFactory.getClient(config.gitRepository)
        // Launches the synchronisation
        client.sync(listener.logger())
    }

    private fun getGitIndexationJobRegistration(configuration: GitConfiguration): JobRegistration {
        return JobRegistration
                .of(createIndexationJob(configuration))
                .everyMinutes(configuration.indexationInterval.toLong())
    }

    override fun scheduleGitBuildSync(branch: Branch, property: GitBranchConfigurationProperty) {
        if (property.buildTagInterval > 0) {
            jobScheduler.schedule(
                    createBuildSyncJob(branch),
                    Schedule.everyMinutes(property.buildTagInterval.toLong())
            )
        } else {
            unscheduleGitBuildSync(branch, property)
        }
    }

    override fun unscheduleGitBuildSync(branch: Branch, property: GitBranchConfigurationProperty) {
        jobScheduler.unschedule(getGitBranchSyncJobKey(branch))
    }

    override fun getSCMPathInfo(branch: Branch): Optional<SCMPathInfo> {
        return getBranchConfiguration(branch)
                ?.let {
                    SCMPathInfo(
                            "git",
                            it.configuration.remote,
                            it.branch, null
                    )
                }
                .asOptional()
    }

    companion object {
        private val GIT_JOB_CATEGORY = JobCategory.of("git").withName("Git")
        private val GIT_INDEXATION_JOB = GIT_JOB_CATEGORY.getType("git-indexation").withName("Git indexation")
        private val GIT_BUILD_SYNC_JOB = GIT_JOB_CATEGORY.getType("git-build-sync").withName("Git build synchronisation")
    }
}
