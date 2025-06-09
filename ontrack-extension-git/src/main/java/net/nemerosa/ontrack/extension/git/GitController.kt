package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.api.model.FileDiffChangeLogRequest
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.model.*
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.extension.scm.model.SCMDocumentNotFoundException
import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.buildfilter.BuildDiff
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("extension/git")
class GitController(
        feature: GitExtensionFeature,
        private val structureService: StructureService,
        private val gitService: GitService,
        private val configurationService: GitConfigurationService,
        private val securityService: SecurityService,
        private val gitChangeLogCache: GitChangeLogCache,
) : AbstractExtensionController<GitExtensionFeature>(feature) {

    /**
     * Gets the configurations
     */
    val configurations: Resources<BasicGitConfiguration>
        @GetMapping("configurations")
        get() = Resources.of(
                configurationService.configurations,
                uri(on(javaClass).configurations)
        )
                .with("_test", uri(on(javaClass).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings::class.java))

    /**
     * Gets the configuration descriptors
     */
    @Suppress("unused")
    val configurationsDescriptors: Resources<ConfigurationDescriptor>
        @GetMapping("configurations/descriptors")
        get() = Resources.of(
                configurationService.configurationDescriptors,
                uri(on(javaClass).configurationsDescriptors)
        )

    @GetMapping("")
    override fun getDescription(): Resource<ExtensionFeatureDescription> {
        @Suppress("RecursivePropertyAccessor")
        return Resource.of(
                feature.featureDescription,
                uri(on(javaClass).description)
        )
                .with("configurations", uri(on(javaClass).configurations), securityService.isGlobalFunctionGranted(GlobalSettings::class.java))
    }

    /**
     * Test for a configuration
     */
    @PostMapping("configurations/test")
    fun testConfiguration(@RequestBody configuration: BasicGitConfiguration?): ConnectionResult {
        return configurationService.test(configuration)
    }

    /**
     * Creating a configuration
     */
    @PostMapping("configurations/create")
    fun newConfiguration(@RequestBody configuration: BasicGitConfiguration): BasicGitConfiguration {
        return configurationService.newConfiguration(configuration)
    }

    /**
     * Gets one configuration
     */
    @GetMapping("configurations/{name:.*}")
    fun getConfiguration(@PathVariable name: String): BasicGitConfiguration {
        return configurationService.getConfiguration(name)
    }

    /**
     * Deleting one configuration
     */
    @DeleteMapping("configurations/{name:.*}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteConfiguration(@PathVariable name: String): Ack {
        configurationService.deleteConfiguration(name)
        return Ack.OK
    }

    /**
     * Updating one configuration
     */
    @PutMapping("configurations/{name:.*}/update")
    fun updateConfiguration(@PathVariable name: String, @RequestBody configuration: BasicGitConfiguration): BasicGitConfiguration {
        configurationService.updateConfiguration(name, configuration)
        return getConfiguration(name)
    }

    /**
     * Launches the build synchronisation for a branch.
     */
    @PostMapping("sync/{branchId}")
    fun launchBuildSync(@PathVariable branchId: ID): Ack {
        return Ack.validate(gitService.launchBuildSync(branchId, false) != null)
    }

    /**
     * Change log entry point
     */
    @GetMapping("changelog")
    @Deprecated("Will be removed in V5. Use the scmChangeLog GraphQL query instead.")
    fun changeLog(request: BuildDiffRequest): BuildDiff {
        val changeLog = gitService.changeLog(request)
        // Stores in cache
        gitChangeLogCache.put(changeLog)
        // OK
        return changeLog
    }

    /**
     * Change log export, list of formats
     */
    @GetMapping("changelog/export/{projectId}/formats")
    @Deprecated("Will be removed in V5. Use the templating service instead.")
    fun changeLogExportFormats(@PathVariable projectId: ID): Resources<ExportFormat> {
        val project = structureService.getProject(projectId)
        val formats = gitService.getIssueExportFormats(project)
        return Resources.of(
            formats,
            uri(on(GitController::class.java).changeLogExportFormats(projectId))
        )
    }

    /**
     * Change log export
     */
    @GetMapping("changelog/export")
    @Deprecated("Will be removed in V5. Use the templating service instead.")
    fun changeLog(request: IssueChangeLogExportRequest): ResponseEntity<String> {
        // Gets the change log
        val changeLog = gitService.changeLog(request)
        // Gets the associated project
        val project = changeLog.project
        // Gets the configuration for the project
        val gitConfiguration = gitService.getProjectConfiguration(project)
                ?: throw GitProjectNotConfiguredException(project.id)
        // Gets the issue service
        val configuredIssueService = gitConfiguration.configuredIssueService
            ?: return ResponseEntity(
                    "The branch is not configured for issues",
                    HttpStatus.NO_CONTENT
            )
        // Gets the issue change log
        val changeLogIssues = gitService.getChangeLogIssues(changeLog)
        // List of issues
        val issues = changeLogIssues.list.map { it.issue }
        // Exports the change log using the given format
        val exportedChangeLogIssues = configuredIssueService.issueServiceExtension
                .exportIssues(
                        configuredIssueService.issueServiceConfiguration,
                        issues,
                        request
                )
        // Content type
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", exportedChangeLogIssues.format + "; charset=utf-8")
        // Body and headers
        return ResponseEntity(exportedChangeLogIssues.content, responseHeaders, HttpStatus.OK)
    }

    private fun getChangeLog(uuid: String): GitChangeLog = gitChangeLogCache.getRequired(uuid)

    /**
     * File diff change log
     */
    @GetMapping("changelog/diff")
    fun diff(request: FileDiffChangeLogRequest?): ResponseEntity<String> {
        // Null proof
        val nonNullRequest = request ?: FileDiffChangeLogRequest()
        // Gets the change log
        val changeLog = gitService.changeLog(nonNullRequest)
        // Diff export
        val diff = gitService.diff(
                changeLog,
                nonNullRequest.patterns
        )
        // Content type
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", "text/plain")
        // Body and headers
        return ResponseEntity(diff, responseHeaders, HttpStatus.OK)
    }

    /**
     * Change log commits
     */
    @GetMapping("changelog/{uuid}/commits")
    @Deprecated("Will be removed in V5. Use the scmChangeLog GraphQL query instead.")
    fun changeLogCommits(
        @PathVariable uuid: String
    ): GitChangeLogCommits {
        // Gets the change log
        val changeLog = getChangeLog(uuid)
        // Cached?
        val commits = changeLog.commits
        if (commits != null) {
            return commits
        }
        // Loads the commits
        val loadedCommits = changeLog.loadCommits {
            gitService.getChangeLogCommits(it)
        }
        // Stores in cache
        gitChangeLogCache.put(changeLog)
        // OK
        return loadedCommits
    }

    /**
     * Change log issues
     */
    @GetMapping("changelog/{uuid}/issues")
    @Deprecated("Will be removed in V5. Use the scmChangeLog GraphQL query instead.")
    fun changeLogIssues(@PathVariable uuid: String): GitChangeLogIssues {
        // Gets the change log
        val changeLog = getChangeLog(uuid)
        // Cached?
        var issues = changeLog.issues
        if (issues != null) {
            return issues
        }
        // Loads the issues
        issues = gitService.getChangeLogIssues(changeLog)
        // Stores in cache
        gitChangeLogCache.put(changeLog.withIssues(issues))
        // OK
        return issues
    }

    /**
     * Change log issues Ids
     */
    @GetMapping("changelog/{uuid}/issuesIds")
    fun changeLogIssuesIds(@PathVariable uuid: String): List<String> {
        // Gets the change log
        val changeLog = getChangeLog(uuid)
        // Gets the issues IDs
        return gitService.getChangeLogIssuesIds(changeLog)
    }

    /**
     * Change log files
     */
    @GetMapping("changelog/{uuid}/files")
    @Deprecated("Will be removed in V5. Use the scmChangeLog GraphQL query instead.")
    fun changeLogFiles(@PathVariable uuid: String): GitChangeLogFiles {
        // Gets the change log
        val changeLog = getChangeLog(uuid)
        // Cached?
        var files = changeLog.files
        if (files != null) {
            return files
        }
        // Loads the files
        files = gitService.getChangeLogFiles(changeLog)
        // Stores in cache
        gitChangeLogCache.put(changeLog.withFiles(files))
        // OK
        return files
    }

    /**
     * Commit information in a project
     */
    @GetMapping("{projectId}/commit-info/{commit}")
    fun commitProjectInfo(@PathVariable projectId: ID, @PathVariable commit: String): Resource<OntrackGitCommitInfo> {
        return Resource.of(
                gitService.getCommitProjectInfo(projectId, commit),
                uri(on(javaClass).commitProjectInfo(projectId, commit))
        ).withView(Build::class.java)
    }

    /**
     * Issue information in a project
     */
    @GetMapping("{projectId}/issue-info/{issue}")
    fun issueProjectInfo(@PathVariable projectId: ID, @PathVariable issue: String): Resource<OntrackGitIssueInfo> {
        return Resource.of<OntrackGitIssueInfo>(
                gitService.getIssueProjectInfo(projectId, issue),
                uri(on(javaClass).issueProjectInfo(projectId, issue))
        ).withView(Build::class.java)
    }

    /**
     * Download a path for a branch
     *
     * @param branchId ID to download a document from
     */
    @GetMapping("download/{branchId}")
    fun download(@PathVariable branchId: ID, path: String): ResponseEntity<String> {
        val branch = structureService.getBranch(branchId)
        val config = gitService.getBranchConfiguration(branch) ?: throw GitBranchNotConfiguredException(branchId)
        return gitService.download(branch.project, config.branch, path)
                ?.let { ResponseEntity.ok(it) }
                ?: throw SCMDocumentNotFoundException(path)
    }

    /**
     * Gets the Git synchronisation information.
     *
     * @param projectId ID of the project
     * @return Synchronisation information
     */
    @GetMapping("project-sync/{projectId}")
    fun getProjectGitSyncInfo(@PathVariable projectId: ID): GitSynchronisationInfo {
        val project = structureService.getProject(projectId)
        return gitService.getProjectGitSyncInfo(project)
    }

    /**
     * Launching the synchronisation
     */
    @PostMapping("project-sync/{projectId}")
    fun projectGitSync(@PathVariable projectId: ID, @RequestBody request: GitSynchronisationRequest): Ack {
        val project = structureService.getProject(projectId)
        return gitService.projectSync(project, request)
    }

}
