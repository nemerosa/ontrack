package net.nemerosa.ontrack.extension.git.service

import com.google.common.collect.Lists
import net.nemerosa.ontrack.common.FutureUtils
import net.nemerosa.ontrack.common.asOptional
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequestDifferenceProjectException
import net.nemerosa.ontrack.extension.git.model.*
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink
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
import java.util.concurrent.atomic.AtomicReference
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
        private val scmService: SCMUtilsService
) : AbstractSCMChangeLogService<GitConfiguration, GitBuildInfo, GitChangeLogIssue>(structureService, propertyService), GitService, JobOrchestratorSupplier {

    private val logger = LoggerFactory.getLogger(GitService::class.java)

    override fun forEachConfiguredProject(consumer: BiConsumer<Project, GitConfiguration>) {
        structureService.projectList
                .forEach { project ->
                    val configuration = getProjectConfiguration(project)
                    configuration.ifPresent { gitConfiguration -> consumer.accept(project, gitConfiguration) }
                }
    }

    override fun forEachConfiguredBranch(consumer: BiConsumer<Branch, GitBranchConfiguration>) {
        for (project in structureService.projectList) {
            structureService.getBranchesForProject(project.id)
                    .filter { branch -> branch.type != BranchType.TEMPLATE_DEFINITION }
                    .forEach { branch ->
                        val configuration = getBranchConfiguration(branch)
                        configuration.ifPresent { gitBranchConfiguration -> consumer.accept(branch, gitBranchConfiguration) }
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
            if (branchConfiguration.buildTagInterval > 0 && branchConfiguration.buildCommitLink.link is IndexableBuildGitCommitLink<*>) {
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
        return getBranchConfiguration(branch).isPresent
    }

    override fun launchBuildSync(branchId: ID, synchronous: Boolean): Optional<Future<*>> {
        // Gets the branch
        val branch = structureService.getBranch(branchId)
        // Gets its configuration
        val branchConfiguration = getBranchConfiguration(branch)
        // If valid, launches a job
        return if (branchConfiguration.isPresent && branchConfiguration.get().buildCommitLink.link is IndexableBuildGitCommitLink<*>) {
            if (synchronous) {
                buildSync<Any>(branch, branchConfiguration.get(), JobRunListener.logger(logger))
                Optional.empty()
            } else {
                jobScheduler.fireImmediately(getGitBranchSyncJobKey(branch))
            }
        } else {
            Optional.empty()
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
            if (oProjectConfiguration.isPresent) {
                // Forces Git sync before
                var syncError: Boolean
                val gitConfiguration = oProjectConfiguration.get()
                try {
                    syncAndWait(gitConfiguration)
                    syncError = false
                } catch (ex: GitRepositorySyncException) {
                    applicationLogService.log(
                            ApplicationLogEntry.error(
                                    ex,
                                    NameDescription.nd(
                                            "git-sync",
                                            "Git synchronisation issue"
                                    ),
                                    gitConfiguration.remote
                            ).withDetail("project", project.name)
                                    .withDetail("git-name", gitConfiguration.name)
                                    .withDetail("git-remote", gitConfiguration.remote)
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
        return getProjectConfiguration(project)
                .orElseThrow { GitProjectNotConfiguredException(project.id) }
    }

    protected fun getGitRepositoryClient(project: Project): GitRepositoryClient {
        return getProjectConfiguration(project)
                .map { it.gitRepository }
                .map { gitRepositoryClientFactory.getClient(it) }
                .orElseThrow { GitProjectNotConfiguredException(project.id) }
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
                .map { c -> c.buildCommitLink.getCommitFromBuild(build) }
                .orElseThrow { GitBranchNotConfiguredException(build.branch.id) }
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
        val branch = structureService.getBranch(branchId)
        // Configuration
        val branchConfiguration = getRequiredBranchConfiguration(branch)
        val configuration = branchConfiguration.configuration
        // Issue service
        val configuredIssueService = configuration.configuredIssueService.orElse(null)
                ?: throw GitBranchIssueServiceNotConfiguredException(branchId)
        // Gets the details about the issue
        val issue: Issue? = configuredIssueService.getIssue(key)
        return if (issue != null) {
            // Collects commits for this branch
            val commitInfos = collectIssueCommitInfos(branch, branchConfiguration, issue)

            // OK
            OntrackGitIssueInfo(
                    configuredIssueService.issueServiceConfigurationRepresentation,
                    issue,
                    commitInfos
            )
        } else {
            null
        }
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
                val buildAfterCommit = getEarliestBuildAfterCommit<Any>(commitId, branch, branchConfiguration, client)
                branchInfo = scmService.getBranchInfo(buildAfterCommit, branchInfo)
                // Adds the info
                commitInfo!!.add(branchInfo)
            }
        }
        // OK
        return Lists.newArrayList(commitInfos.values)
    }

    override fun lookupCommit(configuration: GitConfiguration, id: String): Optional<GitUICommit> {
        // Gets the client client for this configuration
        val gitClient = gitRepositoryClientFactory.getClient(configuration.gitRepository)
        // Gets the commit
        val optGitCommit = gitClient.getCommitFor(id)
        return if (optGitCommit.isPresent) {
            val commitLink = configuration.commitLink
            val messageAnnotators = getMessageAnnotators(configuration)
            Optional.of(
                    toUICommit(
                            commitLink,
                            messageAnnotators,
                            optGitCommit.get()
                    )
            )
        } else {
            Optional.empty()
        }
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
        return projectConfiguration
                .map { gitConfiguration -> Ack.validate(sync(gitConfiguration, request).isPresent) }
                .orElse(Ack.NOK)
    }

    override fun sync(gitConfiguration: GitConfiguration, request: GitSynchronisationRequest): Optional<Future<*>> {
        // Reset the repository?
        if (request.isReset) {
            gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository).reset()
        }
        // Schedules the job
        return jobScheduler.fireImmediately(getGitIndexationJobKey(gitConfiguration))
    }

    override fun getProjectGitSyncInfo(project: Project): GitSynchronisationInfo {
        securityService.checkProjectFunction(project, ProjectConfig::class.java)
        return getProjectConfiguration(project)
                .map { this.getGitSynchronisationInfo(it) }
                .orElseThrow { GitProjectNotConfiguredException(project.id) }
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

    private fun getOntrackGitCommitInfo(project: Project, commit: String): OntrackGitCommitInfo {
        // Reference data
        val theCommit = AtomicReference<GitCommit>()
        val theConfiguration = AtomicReference<GitConfiguration>()
        // Data to collect
        val buildViews = ArrayList<BuildView>()
        val branchStatusViews = ArrayList<BranchStatusView>()
        // For all configured branches
        forEachConfiguredBranch(BiConsumer { branch, branchConfiguration ->
            val configuration = branchConfiguration.configuration
            // Gets the client client for this branch
            val gitClient = gitRepositoryClientFactory.getClient(configuration.gitRepository)
            // Gets the commit
            val commitFor = gitClient.getCommitFor(commit)
            // If present...
            if (commitFor.isPresent) {
                // Reference
                if (theCommit.get() == null) {
                    theCommit.set(commitFor.get())
                    theConfiguration.set(configuration)
                }
                // Gets the earliest build on this branch that contains this commit
                getEarliestBuildAfterCommit<Any>(commit, branch, branchConfiguration, gitClient)
                        // ... and it present collect its data
                        .ifPresent { build ->
                            // Gets the build view
                            val buildView = structureService.getBuildView(build, true)
                            // Adds it to the list
                            buildViews.add(buildView)
                            // Collects the promotions for the branch
                            branchStatusViews.add(
                                    structureService.getEarliestPromotionsAfterBuild(build)
                            )
                        }
            }
        })

        // OK
        if (theCommit.get() != null) {
            val commitLink = theConfiguration.get().commitLink
            val messageAnnotators = getMessageAnnotators(theConfiguration.get())
            return OntrackGitCommitInfo(
                    toUICommit(
                            commitLink,
                            messageAnnotators,
                            theCommit.get()
                    ),
                    buildViews,
                    branchStatusViews
            )
        } else {
            throw GitCommitNotFoundException(commit)
        }

    }

    protected fun <T> getEarliestBuildAfterCommit(commit: String, branch: Branch, branchConfiguration: GitBranchConfiguration, client: GitRepositoryClient): Optional<Build> {
        @Suppress("UNCHECKED_CAST")
        val configuredBuildGitCommitLink = branchConfiguration.buildCommitLink as ConfiguredBuildGitCommitLink<T>
        // Delegates to the build commit link...
        return configuredBuildGitCommitLink.link
                // ... by getting candidate references
                .getBuildCandidateReferences(commit, branch, client, branchConfiguration, configuredBuildGitCommitLink.data)
                // ... gets the builds
                .map { buildName -> structureService.findBuildByName(branch.project.name, branch.name, buildName) }
                // ... filter on existing builds
                .filter { it.isPresent }.map { it.get() }
                // ... filter the builds using the link
                .filter { build -> configuredBuildGitCommitLink.link.isBuildEligible(build, configuredBuildGitCommitLink.data) }
                // ... sort by decreasing date
                .sorted { o1, o2 -> o1.id() - o2.id() }
                // ... takes the first build
                .findFirst()
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

    override fun getProjectConfiguration(project: Project): Optional<GitConfiguration> {
        return gitConfigurators.stream()
                .map { c -> c.getConfiguration(project) }
                .filter { it.isPresent }
                .map { it.get() }
                .findFirst()
    }

    protected fun getRequiredBranchConfiguration(branch: Branch): GitBranchConfiguration {
        return getBranchConfiguration(branch)
                .orElseThrow { GitBranchNotConfiguredException(branch.id) }
    }

    override fun getBranchConfiguration(branch: Branch): Optional<GitBranchConfiguration> {
        // Get the configuration for the project
        val configuration = getProjectConfiguration(branch.project)
        if (configuration.isPresent) {
            // Gets the configuration for a branch
            val gitBranch: String
            val buildCommitLink: ConfiguredBuildGitCommitLink<*>
            val override: Boolean
            val buildTagInterval: Int
            val branchConfig = propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java)
            if (!branchConfig.isEmpty) {
                gitBranch = branchConfig.value.branch
                buildCommitLink = toConfiguredBuildGitCommitLink<Any>(
                        branchConfig.value.buildCommitLink
                )
                override = branchConfig.value.isOverride
                buildTagInterval = branchConfig.value.buildTagInterval
            } else {
                gitBranch = "master"
                buildCommitLink = TagBuildNameGitCommitLink.DEFAULT
                override = false
                buildTagInterval = 0
            }
            // OK
            return Optional.of(
                    GitBranchConfiguration(
                            configuration.get(),
                            gitBranch,
                            buildCommitLink,
                            override,
                            buildTagInterval
                    )
            )
        } else {
            return Optional.empty()
        }
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
        val link = branchConfiguration.buildCommitLink.link as IndexableBuildGitCommitLink<T>
        @Suppress("UNCHECKED_CAST")
        val linkData = branchConfiguration.buildCommitLink.data as T
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
                .map { gitBranchConfiguration ->
                    SCMPathInfo(
                            "git",
                            gitBranchConfiguration.configuration.remote,
                            gitBranchConfiguration.branch, null
                    )
                }
    }

    companion object {
        private val GIT_JOB_CATEGORY = JobCategory.of("git").withName("Git")
        private val GIT_INDEXATION_JOB = GIT_JOB_CATEGORY.getType("git-indexation").withName("Git indexation")
        private val GIT_BUILD_SYNC_JOB = GIT_JOB_CATEGORY.getType("git-build-sync").withName("Git build synchronisation")
    }
}
