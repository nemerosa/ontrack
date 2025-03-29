package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.BuildDiffExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Build.Companion.form
import net.nemerosa.ontrack.model.structure.Build.Companion.of
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.stream.Collectors
import jakarta.validation.Valid

@RestController
@RequestMapping("/rest/structure")
class BuildController(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val securityService: SecurityService,
    private val extensionManager: ExtensionManager,
) : AbstractResourceController() {

    @GetMapping("project/{projectId}/builds")
    fun buildSearchForm(@PathVariable projectId: ID): Resource<Form> {
        return Resource.of(
            createBuildSearchForm(),
            uri(MvcUriComponentsBuilder.on(javaClass).buildSearchForm(projectId))
        ).with("_search", uri(MvcUriComponentsBuilder.on(javaClass).buildSearch(projectId, null)))
    }

    private fun createBuildSearchForm(): Form {
        // List of properties for a build
        val properties = propertyService.propertyTypes.stream()
            .filter { type: PropertyType<*> -> type.supportedEntityTypes.contains(ProjectEntityType.BUILD) }
            .map { type: PropertyType<*>? -> PropertyTypeDescriptor.of(type) }
            .collect(Collectors.toList())
        // Form
        return create()
            .intField(BuildSearchForm::maximumCount, null)
            .textField(BuildSearchForm::branchName, null)
            .textField(BuildSearchForm::buildName, null)
            .textField(BuildSearchForm::promotionName, null)
            .textField(BuildSearchForm::validationStampName, null)
            .selectionOfString(
                BuildSearchForm::property,
                properties.map { it.typeName },
                null,
            )
            .textField(BuildSearchForm::propertyValue, null)
    }

    /**
     * Build search
     */
    @GetMapping("project/{projectId}/builds/search")
    fun buildSearch(@PathVariable projectId: ID, form: @Valid BuildSearchForm?): Resources<BuildView> {
        return Resources.of(
            structureService.buildSearch(projectId, form ?: BuildSearchForm())
                .map { build: Build? ->
                    structureService.getBuildView(
                        build!!, true
                    )
                },
            uri(MvcUriComponentsBuilder.on(javaClass).buildSearch(projectId, form))
        )
            .forView(BuildView::class.java)
    }

    /**
     * List of diff actions
     */
    @GetMapping("project/{projectId}/builds/diff")
    fun buildDiffActions(@PathVariable projectId: ID): Resources<Action> {
        return Resources.of(
            extensionManager.getExtensions(BuildDiffExtension::class.java)
                .stream()
                .filter { extension: BuildDiffExtension ->
                    extension.apply(
                        structureService.getProject(projectId)
                    )
                }
                .map { actionExtension: BuildDiffExtension? -> this.resolveExtensionAction(actionExtension) }
                .collect(Collectors.toList()),
            uri(MvcUriComponentsBuilder.on(javaClass).buildDiffActions(projectId))
        )
    }

    @GetMapping("branches/{branchId}/builds/create")
    fun newBuildForm(@PathVariable branchId: ID): Form {
        // Checks the branch does exist
        structureService.getBranch(branchId)
        // Returns the form
        return form()
    }

    @PostMapping("branches/{branchId}/builds/create")
    fun newBuild(@PathVariable branchId: ID, @RequestBody request: @Valid BuildRequest?): Build {
        // Gets the holding branch
        val branch = structureService.getBranch(branchId)
        // Build signature
        val signature = securityService.currentSignature
        // Creates a new build
        var build = of(branch, request!!.asNameDescription(), signature)
        // Saves it into the repository
        build = structureService.newBuild(build)
        // Saves the properties
        for ((propertyTypeName, propertyData) in request.properties) {
            propertyService.editProperty(
                build,
                propertyTypeName,
                propertyData
            )
        }
        // OK
        return build
    }

    /**
     * Looking for a build using its exact name on a branch.
     */
    @GetMapping("branches/{branchId}/builds/{name:[A-Za-z0-9_.-]+}")
    fun getBuildByBranchAndName(@PathVariable branchId: ID, @PathVariable name: String): Build {
        // Gets the holding branch
        val (_, name1, _, _, project) = structureService.getBranch(branchId)
        // Gets the build by name
        return structureService.findBuildByName(project.name, name1, name)
            .orElseThrow { BuildNotFoundException(project.name, name1, name) }
    }

    @GetMapping("builds/{buildId}/update")
    fun updateBuildForm(@PathVariable buildId: ID): Form {
        return structureService.getBuild(buildId).asForm()
    }

    @PutMapping("builds/{buildId}/update")
    fun updateBuild(@PathVariable buildId: ID, @RequestBody nameDescription: @Valid NameDescription): Build {
        // Gets from the repository
        var build = structureService.getBuild(buildId)
        // Updates
        build = build.update(nameDescription)
        // Saves in repository
        structureService.saveBuild(build)
        // As resource
        return build
    }

    /**
     * Update form for the build signature.
     */
    @GetMapping("builds/{buildId}/signature")
    fun updateBuildSignatureForm(@PathVariable buildId: ID): Form {
        return SignatureRequest.of(
            structureService.getBuild(buildId).signature
        ).asForm()
    }

    /**
     * Update the build signature
     */
    @PutMapping("builds/{buildId}/signature")
    fun updateBuildSignature(@PathVariable buildId: ID, @RequestBody request: SignatureRequest): Build {
        // Gets from the repository
        var build = structureService.getBuild(buildId)
        // Updates
        build = build.withSignature(
            request.getSignature(build.signature)
        )
        // Saves in repository
        structureService.saveBuild(build)
        // As resource
        return build
    }

    @DeleteMapping("builds/{buildId}")
    fun deleteBuild(@PathVariable buildId: ID): Ack {
        return structureService.deleteBuild(buildId)
    }

    @GetMapping("builds/{buildId}")
    fun getBuild(@PathVariable buildId: ID): Build {
        return structureService.getBuild(buildId)
    }

    /**
     * Gets the previous build
     */
    @GetMapping("builds/{buildId}/previous")
    fun getPreviousBuild(@PathVariable buildId: ID): ResponseEntity<Build> {
        val previousBuild = structureService.getPreviousBuild(buildId)
        return if (previousBuild != null) {
            ResponseEntity.ok(previousBuild)
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(null)
        }
    }

    /**
     * Gets the next build
     */
    @GetMapping("builds/{buildId}/next")
    fun getNextBuild(@PathVariable buildId: ID): ResponseEntity<Build> {
        val nextBuild = structureService.getNextBuild(buildId)
        return if (nextBuild != null) {
            ResponseEntity.ok(nextBuild)
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(null)
        }
    }

    /**
     * Gets the build links FROM this build.
     *
     * @param buildId Build to get the links from
     * @return List of builds
     */
    @GetMapping("builds/{buildId}/links/from")
    fun getBuildLinksFrom(
        @PathVariable buildId: ID,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Resources<Build> {
        return Resources.of(
            structureService.getQualifiedBuildsUsedBy(
                structureService.getBuild(buildId),
                offset,
                size
            ).pageItems.map { it.build },
            uri(MvcUriComponentsBuilder.on(javaClass).getBuildLinksFrom(buildId, offset, size))
        ).forView(Build::class.java)
    }

    /**
     * Gets the build links TO this build.
     *
     * @param buildId Build to get the links to
     * @return List of builds
     */
    @GetMapping("builds/{buildId}/links/to")
    fun getBuildLinksTo(
        @PathVariable buildId: ID,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Resources<Build> {
        return Resources.of(
            structureService.getBuildsUsing(
                structureService.getBuild(buildId),
                offset,
                size
            ).pageItems,
            uri(MvcUriComponentsBuilder.on(javaClass).getBuildLinksTo(buildId, offset, size))
        ).forView(Build::class.java)
    }

    /**
     * Create a link between a build and another, using a form.
     *
     * @param buildId From this build...
     * @return List of builds
     */
    @PutMapping("builds/{buildId}/links/edit")
    fun createBuildLinkFromForm(@PathVariable buildId: ID, @RequestBody form: BuildLinkForm?): Build {
        val build = structureService.getBuild(buildId)
        structureService.editBuildLinks(build, form ?: BuildLinkForm())
        return build
    }

    /**
     * Creates a link between a build and another
     *
     * @param buildId       From this build...
     * @param targetBuildId ... to this build
     * @return List of builds
     */
    @PutMapping("builds/{buildId}/links/{targetBuildId}")
    fun addBuildLink(@PathVariable buildId: ID, @PathVariable targetBuildId: ID): Build {
        val build = structureService.getBuild(buildId)
        val targetBuild = structureService.getBuild(targetBuildId)
        structureService.createBuildLink(build, targetBuild, BuildLink.DEFAULT)
        return build
    }

    /**
     * Deletes a link between a build and another
     *
     * @param buildId       From this build...
     * @param targetBuildId ... to this build
     * @return List of builds
     */
    @DeleteMapping("builds/{buildId}/links/{targetBuildId}")
    fun deleteBuildLink(
        @PathVariable buildId: ID,
        @PathVariable targetBuildId: ID,
        @RequestParam qualifier: String?
    ): Build {
        val build = structureService.getBuild(buildId)
        val targetBuild = structureService.getBuild(targetBuildId)
        structureService.deleteBuildLink(build, targetBuild, qualifier ?: BuildLink.DEFAULT)
        return build
    }
}
