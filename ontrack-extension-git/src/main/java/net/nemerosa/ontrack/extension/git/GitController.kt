package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitBranchNotConfiguredException
import net.nemerosa.ontrack.extension.git.model.GitSynchronisationInfo
import net.nemerosa.ontrack.extension.git.model.GitSynchronisationRequest
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.scm.model.SCMDocumentNotFoundException
import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("extension/git")
class GitController(
    feature: GitExtensionFeature,
    private val structureService: StructureService,
    private val gitService: GitService,
    private val configurationService: GitConfigurationService,
) : AbstractExtensionController<GitExtensionFeature>(feature) {

    /**
     * Gets the configurations
     */
    val configurations: List<BasicGitConfiguration>
        @GetMapping("configurations")
        get() = configurationService.configurations

    @GetMapping("")
    override fun getDescription(): ExtensionFeatureDescription {
        return feature.featureDescription
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
    fun updateConfiguration(
        @PathVariable name: String,
        @RequestBody configuration: BasicGitConfiguration
    ): BasicGitConfiguration {
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
