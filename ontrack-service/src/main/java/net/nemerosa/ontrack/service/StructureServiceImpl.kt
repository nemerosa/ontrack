package net.nemerosa.ontrack.service

import com.google.common.collect.Iterables
import net.nemerosa.ontrack.common.CachedSupplier
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.extension.api.BuildValidationExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.ValidationRunMetricsExtension
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.extension.PromotionLevelPropertyType
import net.nemerosa.ontrack.model.extension.ValidationStampPropertyType
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Entity.isEntityDefined
import net.nemerosa.ontrack.model.structure.Entity.isEntityNew
import net.nemerosa.ontrack.model.support.PropertyServiceHelper
import net.nemerosa.ontrack.repository.StructureRepository
import net.nemerosa.ontrack.service.ImageHelper.checkImage
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiFunction
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.streams.toList

@Service
@Transactional
class StructureServiceImpl(
        private val securityService: SecurityService,
        private val eventPostService: EventPostService,
        private val eventFactory: EventFactory,
        private val validationRunStatusService: ValidationRunStatusService,
        private val validationDataTypeService: ValidationDataTypeService,
        private val structureRepository: StructureRepository,
        private val extensionManager: ExtensionManager,
        private val propertyService: PropertyService,
        private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
        private val predefinedValidationStampService: PredefinedValidationStampService,
        private val decorationService: DecorationService,
        private val projectFavouriteService: ProjectFavouriteService,
        private val promotionRunCheckService: PromotionRunCheckService
) : StructureService {

    private val logger = LoggerFactory.getLogger(StructureService::class.java)

    override val projectStatusViews: List<ProjectStatusView>
        get() = projectList
                .map { project ->
                    ProjectStatusView(
                            project,
                            decorationService.getDecorations(project),
                            getBranchStatusViews(project.id)
                    )
                }

    override val projectStatusViewsForFavourites: List<ProjectStatusView>
        get() = projectFavourites
                .map { project ->
                    ProjectStatusView(
                            project,
                            decorationService.getDecorations(project),
                            getBranchStatusViews(project.id)
                    )
                }

    /**
     * Gets the list of all authorised projects...
     * .. filtered using the preferences
     * .. ok
     */
    override val projectFavourites: List<Project>
        get() = projectList.filter { projectFavouriteService.isProjectFavourite(it) }

    override val projectList: List<Project>
        get() {
            val securitySettings = securityService.securitySettings
            val list = structureRepository.projectList
            return if (securitySettings.isGrantProjectViewToAll || securityService.isGlobalFunctionGranted(ProjectList::class.java)) {
                list
            } else if (securityService.isLogged) {
                list.filter { p -> securityService.isProjectFunctionGranted(p.id(), ProjectView::class.java) }
            } else {
                throw AccessDeniedException("Authentication is required.")
            }
        }

    override fun newProject(project: Project): Project {
        isEntityNew(project, "Project must be defined")
        securityService.checkGlobalFunction(ProjectCreation::class.java)
        val newProject = structureRepository.newProject(project.withSignature(securityService.currentSignature))
        eventPostService.post(eventFactory.newProject(newProject))
        return newProject
    }

    override fun getProject(projectId: ID): Project {
        securityService.checkProjectFunction(projectId.value, ProjectView::class.java)
        return structureRepository.getProject(projectId)
    }

    override fun saveProject(project: Project) {
        isEntityDefined(project, "Project must be defined")
        securityService.checkProjectFunction(project.id(), ProjectEdit::class.java)
        structureRepository.saveProject(project)
        eventPostService.post(eventFactory.updateProject(project))
    }

    override fun disableProject(project: Project): Project {
        val disabledProject = project.withDisabled(true)
        saveProject(disabledProject)
        eventPostService.post(eventFactory.disableProject(project))
        return disabledProject
    }

    override fun enableProject(project: Project): Project {
        val enabledProject = project.withDisabled(false)
        saveProject(enabledProject)
        eventPostService.post(eventFactory.enableProject(project))
        return enabledProject
    }

    override fun deleteProject(projectId: ID): Ack {
        Validate.isTrue(projectId.isSet, "Project ID must be set")
        securityService.checkProjectFunction(projectId.value, ProjectDelete::class.java)
        eventPostService.post(eventFactory.deleteProject(getProject(projectId)))
        return structureRepository.deleteProject(projectId)
    }

    override fun getBranch(branchId: ID): Branch {
        val branch = structureRepository.getBranch(branchId)
        securityService.checkProjectFunction(branch.project.id(), ProjectView::class.java)
        return branch
    }

    override fun getBranchesForProject(projectId: ID): List<Branch> {
        securityService.checkProjectFunction(projectId.value, ProjectView::class.java)
        return structureRepository.getBranchesForProject(projectId)
    }

    override fun newBranch(branch: Branch): Branch {
        // Validation
        isEntityNew(branch, "Branch must be new")
        isEntityDefined(branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(branch.project.id(), BranchCreate::class.java)
        // Creating the branch
        val newBranch = structureRepository.newBranch(branch.withSignature(securityService.currentSignature))
        // Event
        eventPostService.post(eventFactory.newBranch(newBranch))
        // OK
        return newBranch
    }

    override fun getBranchStatusViews(projectId: ID): List<BranchStatusView> {
        return getBranchesForProject(projectId)
                .map { this.getBranchStatusView(it) }
    }

    override fun getBranchStatusView(branch: Branch): BranchStatusView {
        return BranchStatusView(
                branch,
                decorationService.getDecorations(branch),
                getLastBuildForBranch(branch),
                getPromotionLevelListForBranch(branch.id).map { this.toPromotionView(it) }
        )
    }

    override fun saveBranch(branch: Branch) {
        isEntityDefined(branch, "Branch must be defined")
        isEntityDefined(branch.project, "Project must be defined")
        securityService.checkProjectFunction(branch.projectId(), BranchEdit::class.java)
        structureRepository.saveBranch(branch)
        eventPostService.post(eventFactory.updateBranch(branch))
    }

    override fun disableBranch(branch: Branch): Branch {
        val disabledBranch = branch.withDisabled(true)
        saveBranch(disabledBranch)
        eventPostService.post(eventFactory.disableBranch(branch))
        return disabledBranch
    }

    override fun enableBranch(branch: Branch): Branch {
        val disabledBranch = branch.withDisabled(false)
        saveBranch(disabledBranch)
        eventPostService.post(eventFactory.enableBranch(branch))
        return disabledBranch
    }

    override fun deleteBranch(branchId: ID): Ack {
        Validate.isTrue(branchId.isSet, "Branch ID must be set")
        val branch = getBranch(branchId)
        securityService.checkProjectFunction(branch.projectId(), BranchDelete::class.java)
        eventPostService.post(eventFactory.deleteBranch(branch))
        return structureRepository.deleteBranch(branchId)
    }

    protected fun toPromotionView(promotionLevel: PromotionLevel): PromotionView {
        // Gets the last build having this promotion level
        val promotionRun = getLastPromotionRunForPromotionLevel(promotionLevel)
        // OK
        return PromotionView(
                promotionLevel,
                promotionRun
        )
    }

    override fun getLastPromotionRunForPromotionLevel(promotionLevel: PromotionLevel): PromotionRun? {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView::class.java)
        return structureRepository.getLastPromotionRunForPromotionLevel(promotionLevel)
    }

    override fun getPromotionRunView(promotionLevel: PromotionLevel): PromotionRunView {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView::class.java)
        return PromotionRunView(
                promotionLevel,
                structureRepository.getPromotionRunsForPromotionLevel(promotionLevel)
        )
    }

    override fun deletePromotionRun(promotionRunId: ID): Ack {
        Validate.isTrue(promotionRunId.isSet, "Promotion run ID must be set")
        val promotionRun = getPromotionRun(promotionRunId)
        securityService.checkProjectFunction(promotionRun, PromotionRunDelete::class.java)
        eventPostService.post(eventFactory.deletePromotionRun(promotionRun))
        return structureRepository.deletePromotionRun(promotionRunId)
    }

    override fun getEarliestPromotionRunAfterBuild(promotionLevel: PromotionLevel, build: Build): Optional<PromotionRun> {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView::class.java)
        return structureRepository.getEarliestPromotionRunAfterBuild(promotionLevel, build)
    }

    override fun getPromotionRunsForPromotionLevel(promotionLevelId: ID): List<PromotionRun> {
        val promotionLevel = getPromotionLevel(promotionLevelId)
        return structureRepository.getPromotionRunsForPromotionLevel(promotionLevel)
    }

    override fun getLastBuildForBranch(branch: Branch): Build? {
        // Checks the accesses
        securityService.checkProjectFunction(branch.projectId(), ProjectView::class.java)
        // Gets the last build
        return structureRepository.getLastBuildForBranch(branch)
    }

    override fun getBuildCount(branch: Branch): Int {
        return structureRepository.getBuildCount(branch)
    }

    override fun deleteBuild(buildId: ID): Ack {
        Validate.isTrue(buildId.isSet, "Build ID must be set")
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.projectId(), BuildDelete::class.java)
        eventPostService.post(eventFactory.deleteBuild(build))
        return structureRepository.deleteBuild(buildId)
    }

    override fun getPreviousBuild(buildId: ID): Optional<Build> {
        return structureRepository.getPreviousBuild(getBuild(buildId))
    }

    override fun getNextBuild(buildId: ID): Optional<Build> {
        return structureRepository.getNextBuild(getBuild(buildId))
    }

    protected fun validateBuild(build: Build) {
        extensionManager.getExtensions(BuildValidationExtension::class.java).forEach { x -> x.validateBuild(build) }
    }

    override fun newBuild(build: Build): Build {
        // Validation
        isEntityNew(build, "Build must be new")
        isEntityDefined(build.branch, "Branch must be defined")
        isEntityDefined(build.branch.project, "Project must be defined")
        // Branch must not be a template definition
        if (getBranch(build.branch.id).type == BranchType.TEMPLATE_DEFINITION) {
            throw BranchTemplateCannotHaveBuildException(build.branch.name)
        }
        // Security
        securityService.checkProjectFunction(build.branch.project.id(), BuildCreate::class.java)
        // Build validation
        validateBuild(build)
        // Repository
        val newBuild = structureRepository.newBuild(build)
        // Event
        eventPostService.post(eventFactory.newBuild(newBuild))
        // OK
        return newBuild
    }

    override fun saveBuild(build: Build): Build {
        // Validation
        isEntityDefined(build, "Build must be defined")
        isEntityDefined(build.branch, "Branch must be defined")
        isEntityDefined(build.branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(build.branch.project.id(), BuildEdit::class.java)
        // Signature change check
        validationSignatureChange(build)
        // Build validation
        validateBuild(build)
        // Repository
        val savedBuild = structureRepository.saveBuild(build)
        // Event
        eventPostService.post(eventFactory.updateBuild(savedBuild))
        // OK
        return getBuild(build.id)
    }

    private fun validationSignatureChange(build: Build) {
        // Get the original build signature
        val orig = getBuild(build.id)
        // Compares the signatures
        if (orig.signature != build.signature) {
            // Checks the authorisation
            securityService.checkProjectFunction(build, ProjectEdit::class.java)
        }
    }

    override fun getBuild(buildId: ID): Build {
        val build = structureRepository.getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return build
    }

    override fun findBuild(branchId: ID, buildPredicate: Predicate<Build>, sortDirection: BuildSortDirection): Optional<Build> {
        // Gets the branch
        val branch = getBranch(branchId)
        // Build being found
        val ref = AtomicReference<Build>()
        // Loops over the builds
        structureRepository.builds(
                branch,
                { build ->
                    val ok = buildPredicate.test(build)
                    if (ok) {
                        ref.set(build)
                    }
                    !ok // Going on if no match
                },
                sortDirection
        )
        // Result
        return Optional.ofNullable(ref.get())
    }

    override fun getLastBuild(branchId: ID): Optional<Build> {
        return Optional.ofNullable(
                structureRepository.getLastBuildForBranch(
                        getBranch(branchId)
                )
        )
    }

    override fun buildSearch(projectId: ID, form: BuildSearchForm): List<Build> {
        // Gets the project
        val project = getProject(projectId)
        // Collects the builds for this project
        val builds = ArrayList<Build>()
        // Filter for the builds
        val buildPredicate = Predicate<Build> { build ->
            // Build view
            val buildViewSupplier = CachedSupplier.of { getBuildView(build, false) }
            // Branch name
            var accept: Boolean
            accept = !StringUtils.isNotBlank(form.branchName) || Utils.safeRegexMatch(form.branchName, build.getBranch().getName())
            // Build name
            if (accept && StringUtils.isNotBlank(form.buildName)) {
                if (form.isBuildExactMatch) {
                    accept = StringUtils.equals(form.buildName, build.getName())
                } else {
                    accept = Utils.safeRegexMatch(form.buildName, build.getName())
                }
            }
            // Promotion name
            if (accept && StringUtils.isNotBlank(form.promotionName)) {
                val buildView = buildViewSupplier.get()
                accept = buildView.promotionRuns.stream()
                        .anyMatch { run -> form.promotionName == run.promotionLevel.name }
            }
            // Validation stamp name
            if (accept && StringUtils.isNotBlank(form.validationStampName)) {
                val buildView = buildViewSupplier.get()
                accept = buildView.validationStampRunViews.stream()
                        .anyMatch { validationStampRunView -> validationStampRunView.hasValidationStamp(form.validationStampName, ValidationRunStatusID.PASSED) }
            }
            // Property & property value
            if (accept && StringUtils.isNotBlank(form.property)) {
                accept = PropertyServiceHelper.hasProperty(
                        propertyService,
                        build,
                        form.property,
                        form.propertyValue)
            }
            // Linked from
            val linkedFrom = form.linkedFrom
            if (accept && isNotBlank(linkedFrom)) {
                val projectName = StringUtils.substringBefore(linkedFrom, ":")
                val buildPattern = StringUtils.substringAfter(linkedFrom, ":")
                accept = isLinkedFrom(build, projectName, buildPattern)
            }
            // Linked to
            val linkedTo = form.linkedTo
            if (accept && isNotBlank(linkedTo)) {
                val projectName = StringUtils.substringBefore(linkedTo, ":")
                val buildPattern = StringUtils.substringAfter(linkedTo, ":")
                accept = isLinkedTo(build, projectName, buildPattern)
            }
            // Accepting the build into the list?
            if (accept) {
                builds.add(build)
            }
            // Maximum count reached?
            builds.size < form.maximumCount
        }
        // Query
        structureRepository.builds(project, buildPredicate)
        // OK
        return builds
    }

    override fun addBuildLink(fromBuild: Build, toBuild: Build) {
        securityService.checkProjectFunction(fromBuild, BuildConfig::class.java)
        securityService.checkProjectFunction(toBuild, ProjectView::class.java)
        structureRepository.addBuildLink(fromBuild.id, toBuild.id)
    }

    override fun deleteBuildLink(fromBuild: Build, toBuild: Build) {
        securityService.checkProjectFunction(fromBuild, BuildConfig::class.java)
        securityService.checkProjectFunction(toBuild, ProjectView::class.java)
        structureRepository.deleteBuildLink(fromBuild.id, toBuild.id)
    }

    override fun getBuildLinksFrom(build: Build): List<Build> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.getBuildLinksFrom(build.id)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
    }

    override fun getBuildsUsedBy(build: Build, offset: Int, size: Int, filter: (Build) -> Boolean): PaginatedList<Build> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        // Gets the complete list, filtered by ACL
        val list = structureRepository.getBuildsUsedBy(build)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
        // OK
        return PaginatedList.create(list.filter(filter), offset, size)
    }

    override fun getBuildsUsing(build: Build, offset: Int, size: Int, filter: (Build) -> Boolean): PaginatedList<Build> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        // Gets the complete list, filtered by ACL
        val list = structureRepository.getBuildsUsing(build)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
        // OK
        return PaginatedList.create(list.filter(filter), offset, size)
    }

    override fun getBuildLinksTo(build: Build): List<Build> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.getBuildLinksTo(build.id)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
    }

    override fun searchBuildsLinkedTo(projectName: String, buildPattern: String): List<Build> {
        return structureRepository.searchBuildsLinkedTo(projectName, buildPattern)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
    }

    override fun editBuildLinks(build: Build, form: BuildLinkForm) {
        securityService.checkProjectFunction(build, BuildConfig::class.java)
        // Gets the existing links, with authorisations
        val authorisedExistingLinks = getBuildLinksFrom(build).map { it.id }
        // Added links
        val addedLinks = HashSet<ID>()
        // Loops through the new links
        form.links.forEach { item ->
            // Gets the project if possible
            val project = findProjectByName(item.project)
                    .orElseThrow { ProjectNotFoundException(item.project) }
            // Finds the build if possible (exact match - no regex)
            val builds = buildSearch(project.id, BuildSearchForm()
                    .withMaximumCount(1)
                    .withBuildName(item.build)
                    .withBuildExactMatch(true)
            )
            if (!builds.isEmpty()) {
                val target = builds[0]
                // Adds the link
                addBuildLink(build, target)
                addedLinks.add(target.id)
            } else {
                throw BuildNotFoundException(item.project, item.build)
            }
        }
        // Deletes all authorised links which were not added again
        if (!form.isAddOnly) {
            // Other links, not authorised to view, were not subject to edition and are not visible
            val linksToDelete = HashSet(authorisedExistingLinks)
            linksToDelete.removeAll(addedLinks)
            linksToDelete.forEach { id ->
                deleteBuildLink(
                        build,
                        getBuild(id)
                )
            }
        }
    }

    override fun isLinkedFrom(build: Build, project: String, buildPattern: String): Boolean {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.isLinkedFrom(build.id, project, buildPattern)
    }

    override fun isLinkedTo(build: Build, project: String, buildPattern: String): Boolean {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.isLinkedTo(build.id, project, buildPattern)
    }

    override fun getValidationStampRunViewsForBuild(build: Build): List<ValidationStampRunView> {
        // Gets all validation stamps
        val stamps = getValidationStampListForBranch(build.branch.id)
        // Gets all runs for this build
        val runs = getValidationRunsForBuild(build.id)
        // Gets the validation stamp run views
        return stamps.map { stamp -> getValidationStampRunView(runs, stamp) }
    }

    protected fun getValidationStampRunView(runs: List<ValidationRun>, stamp: ValidationStamp): ValidationStampRunView {
        return ValidationStampRunView(
                stamp,
                runs.filter { run -> run.validationStamp.id() == stamp.id() }
        )
    }

    override fun getPromotionLevelListForBranch(branchId: ID): List<PromotionLevel> {
        val branch = getBranch(branchId)
        securityService.checkProjectFunction(branch.project.id(), ProjectView::class.java)
        return structureRepository.getPromotionLevelListForBranch(branchId)
    }

    override fun newPromotionLevel(promotionLevel: PromotionLevel): PromotionLevel {
        // Creation
        val newPromotionLevel = rawNewPromotionLevel(promotionLevel)
        // Checking if there is an associated predefined promotion level
        return securityService.callAsAdmin {
            val predefined: PredefinedPromotionLevel? = securityService.callAsAdmin {
                predefinedPromotionLevelService.findPredefinedPromotionLevelByName(promotionLevel.name)
            }.orElse(null)
            if (predefined != null) {
                // Description
                if (promotionLevel.description.isNullOrBlank()) {
                    savePromotionLevel(newPromotionLevel.withDescription(predefined.description))
                }
                // Image
                if (predefined.image != null && predefined.image) {
                    setPromotionLevelImage(
                            newPromotionLevel.id,
                            predefinedPromotionLevelService.getPredefinedPromotionLevelImage(predefined.id)
                    )
                }
                // Reloading
                getPromotionLevel(newPromotionLevel.id)
            } else {
                newPromotionLevel
            }
        }
    }

    private fun rawNewPromotionLevel(promotionLevel: PromotionLevel): PromotionLevel {
        // Validation
        isEntityNew(promotionLevel, "Promotion level must be new")
        isEntityDefined(promotionLevel.branch, "Branch must be defined")
        isEntityDefined(promotionLevel.branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(promotionLevel.branch.project.id(), PromotionLevelCreate::class.java)
        // Repository
        val newPromotionLevel = structureRepository.newPromotionLevel(
                promotionLevel.withSignature(securityService.currentSignature)
        )
        // Event
        eventPostService.post(eventFactory.newPromotionLevel(newPromotionLevel))
        // OK
        return newPromotionLevel
    }

    override fun getPromotionLevel(promotionLevelId: ID): PromotionLevel {
        val promotionLevel = structureRepository.getPromotionLevel(promotionLevelId)
        securityService.checkProjectFunction(promotionLevel.branch.project.id(), ProjectView::class.java)
        return promotionLevel
    }

    override fun getPromotionLevelImage(promotionLevelId: ID): Document {
        // Checks access
        getPromotionLevel(promotionLevelId)
        // Repository access
        return structureRepository.getPromotionLevelImage(promotionLevelId)
    }

    override fun setPromotionLevelImage(promotionLevelId: ID, document: Document?) {
        checkImage(document)
        // Checks access
        val promotionLevel = getPromotionLevel(promotionLevelId)
        securityService.checkProjectFunction(promotionLevel.branch.project.id(), PromotionLevelEdit::class.java)
        // Repository
        structureRepository.setPromotionLevelImage(promotionLevelId, document)
        // Event
        eventPostService.post(eventFactory.imagePromotionLevel(promotionLevel))
    }

    override fun savePromotionLevel(promotionLevel: PromotionLevel) {
        // Validation
        isEntityDefined(promotionLevel, "Promotion level must be defined")
        isEntityDefined(promotionLevel.branch, "Branch must be defined")
        isEntityDefined(promotionLevel.branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelEdit::class.java)
        // Repository
        structureRepository.savePromotionLevel(promotionLevel)
        // Event
        eventPostService.post(eventFactory.updatePromotionLevel(promotionLevel))
    }

    override fun deletePromotionLevel(promotionLevelId: ID): Ack {
        Validate.isTrue(promotionLevelId.isSet, "Promotion level ID must be set")
        val promotionLevel = getPromotionLevel(promotionLevelId)
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelDelete::class.java)
        eventPostService.post(eventFactory.deletePromotionLevel(promotionLevel))
        return structureRepository.deletePromotionLevel(promotionLevelId)
    }

    override fun reorderPromotionLevels(branchId: ID, reordering: Reordering) {
        // Loads the branch
        val branch = getBranch(branchId)
        // Checks the access rights
        securityService.checkProjectFunction(branch.projectId(), PromotionLevelEdit::class.java)
        // Loads the promotion levels
        val promotionLevels = getPromotionLevelListForBranch(branchId)
        // Checks the size
        if (reordering.ids.size != promotionLevels.size) {
            throw ReorderingSizeException("The reordering request should have the same number of IDs as the number" + " of the promotion levels")
        }
        // Actual reordering
        structureRepository.reorderPromotionLevels(branchId, reordering)
        // Event
        eventPostService.post(eventFactory.reorderPromotionLevels(branch))
    }

    override fun newPromotionLevelFromPredefined(branch: Branch, predefinedPromotionLevel: PredefinedPromotionLevel): PromotionLevel {
        val promotionLevel = rawNewPromotionLevel(
                PromotionLevel.of(
                        branch,
                        NameDescription.nd(predefinedPromotionLevel.name, predefinedPromotionLevel.description)
                )
        )

        // Makes sure the order is the same than for the predefined promotion levels
        val predefinedPromotionLevels = securityService.asAdmin(
                Supplier { predefinedPromotionLevelService.predefinedPromotionLevels }
        )
        val sortedIds = getPromotionLevelListForBranch(branch.id).stream()
                // TODO Kotlin - do not use stream
                .sorted { o1, o2 ->
                    val name1 = o1.name
                    val name2 = o2.name
                    // Looking for the order in the predefined list
                    val order1 = Iterables.indexOf(predefinedPromotionLevels) { pred -> StringUtils.equals(pred?.name, name1) }
                    val order2 = Iterables.indexOf(predefinedPromotionLevels) { pred -> StringUtils.equals(pred?.name, name2) }
                    // Comparing the orders
                    order1 - order2
                }
                .map { it.id() }
                .toList()
        reorderPromotionLevels(branch.id, Reordering(sortedIds))

        // Image?
        if (predefinedPromotionLevel.image != null && predefinedPromotionLevel.image!!) {
            setPromotionLevelImage(
                    promotionLevel.id,
                    predefinedPromotionLevelService.getPredefinedPromotionLevelImage(predefinedPromotionLevel.id)
            )
        }
        // OK
        return promotionLevel
    }

    override fun getOrCreatePromotionLevel(branch: Branch, promotionLevelId: Int?, promotionLevelName: String?): PromotionLevel {
        if (promotionLevelId != null) {
            return getPromotionLevel(ID.of(promotionLevelId))
        } else if (promotionLevelName != null) {
            var oPromotionLevel = findPromotionLevelByName(
                    branch.project.name,
                    branch.name,
                    promotionLevelName
            )
            if (oPromotionLevel.isPresent) {
                return oPromotionLevel.get()
            } else {
                val properties = propertyService.getProperties(branch.project)
                for (property in properties) {
                    val type = property.type
                    if (type is PromotionLevelPropertyType<*> && !property.isEmpty) {
                        oPromotionLevel = getPromotionLevelFromProperty(
                                property,
                                branch,
                                promotionLevelName
                        )
                        if (oPromotionLevel.isPresent) {
                            return oPromotionLevel.get()
                        }
                    }
                }
                throw PromotionLevelNotFoundException(
                        branch.project.name,
                        branch.name,
                        promotionLevelName
                )
            }
        } else {
            throw PromotionRunRequestException()
        }
    }

    protected fun <T> getPromotionLevelFromProperty(
            property: Property<T>,
            branch: Branch,
            promotionLevelName: String): Optional<PromotionLevel> {
        val promotionLevelPropertyType = property.type as PromotionLevelPropertyType<T>
        return promotionLevelPropertyType.getOrCreatePromotionLevel(
                property.value,
                branch,
                promotionLevelName
        )
    }

    override fun newPromotionRun(promotionRun: PromotionRun): PromotionRun {
        // Validation
        isEntityNew(promotionRun, "Promotion run must be new")
        isEntityDefined(promotionRun.build, "Build must be defined")
        isEntityDefined(promotionRun.promotionLevel, "Promotion level must be defined")
        Validate.isTrue(promotionRun.promotionLevel.branch.id() == promotionRun.build.branch.id(),
                "Promotion for a promotion level can be done only on the same branch than the build.")
        // Checks the authorization
        securityService.checkProjectFunction(promotionRun.build.branch.project.id(), PromotionRunCreate::class.java)
        // Checks the preconditions for the creation of the promotion run
        promotionRunCheckService.checkPromotionRunCreation(promotionRun)
        // If the promotion run's time is not defined, takes the current date
        val promotionRunToSave: PromotionRun
        val time = promotionRun.signature.time
        if (time == null) {
            promotionRunToSave = PromotionRun.of(
                    promotionRun.build,
                    promotionRun.promotionLevel,
                    promotionRun.signature.withTime(Time.now()),
                    promotionRun.description
            )
        } else {
            promotionRunToSave = promotionRun
        }
        // Actual creation
        val newPromotionRun = structureRepository.newPromotionRun(promotionRunToSave)
        // Event
        eventPostService.post(eventFactory.newPromotionRun(newPromotionRun))
        // OK
        return newPromotionRun
    }

    override fun getPromotionRun(promotionRunId: ID): PromotionRun {
        val promotionRun = structureRepository.getPromotionRun(promotionRunId)
        securityService.checkProjectFunction(promotionRun.build.branch.project.id(), ProjectView::class.java)
        return promotionRun
    }

    override fun getPromotionRunsForBuild(buildId: ID): List<PromotionRun> {
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getPromotionRunsForBuild(build)
    }

    override fun getLastPromotionRunsForBuild(buildId: ID): List<PromotionRun> {
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getLastPromotionRunsForBuild(build)
    }

    override fun getLastPromotionRunForBuildAndPromotionLevel(build: Build, promotionLevel: PromotionLevel): Optional<PromotionRun> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.getLastPromotionRun(build, promotionLevel)
    }

    override fun getPromotionRunsForBuildAndPromotionLevel(build: Build, promotionLevel: PromotionLevel): List<PromotionRun> {
        securityService.checkProjectFunction(build, ProjectView::class.java)
        return structureRepository.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
    }

    override fun getValidationStampListForBranch(branchId: ID): List<ValidationStamp> {
        val branch = getBranch(branchId)
        securityService.checkProjectFunction(branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationStampListForBranch(branchId)
    }

    override fun newValidationStamp(validationStamp: ValidationStamp): ValidationStamp {
        // Raw creation
        val newValidationStamp = rawNewValidationStamp(validationStamp)
        // Checking if there is an associated predefined validation stamp
        return securityService.callAsAdmin {
            val predefined: PredefinedValidationStamp? = securityService.callAsAdmin {
                predefinedValidationStampService.findPredefinedValidationStampByName(validationStamp.name)
            }.orElse(null)
            if (predefined != null) {
                // Description
                if (validationStamp.description.isNullOrBlank()) {
                    saveValidationStamp(newValidationStamp.withDescription(predefined.description))
                }
                // Image
                if (predefined.image != null && predefined.image) {
                    setValidationStampImage(
                            newValidationStamp.id,
                            predefinedValidationStampService.getPredefinedValidationStampImage(predefined.id)
                    )
                }
                // Reloading
                getValidationStamp(newValidationStamp.id)
            } else {
                newValidationStamp
            }
        }
    }

    private fun rawNewValidationStamp(validationStamp: ValidationStamp): ValidationStamp {
        // Validation
        isEntityNew(validationStamp, "Validation stamp must be new")
        isEntityDefined(validationStamp.branch, "Branch must be defined")
        isEntityDefined(validationStamp.branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(validationStamp.branch.project.id(), ValidationStampCreate::class.java)
        // Repository
        val newValidationStamp = structureRepository.newValidationStamp(
                validationStamp.withSignature(securityService.currentSignature)
        )
        // Event
        eventPostService.post(eventFactory.newValidationStamp(newValidationStamp))
        // OK
        return newValidationStamp
    }

    override fun getValidationStamp(validationStampId: ID): ValidationStamp {
        val validationStamp = structureRepository.getValidationStamp(validationStampId)
        securityService.checkProjectFunction(validationStamp.branch.project.id(), ProjectView::class.java)
        return validationStamp
    }

    override fun findValidationStampByName(project: String, branch: String, validationStamp: String): Optional<ValidationStamp> {
        return structureRepository.getValidationStampByName(project, branch, validationStamp)
                .filter { vs -> securityService.isProjectFunctionGranted(vs, ProjectView::class.java) }
    }

    override fun findBuildByName(project: String, branch: String, build: String): Optional<Build> {
        return structureRepository.getBuildByName(project, branch, build)
                .filter { b -> securityService.isProjectFunctionGranted(b, ProjectView::class.java) }
    }

    override fun getEarliestPromotionsAfterBuild(build: Build): BranchStatusView {
        return BranchStatusView(
                build.branch,
                decorationService.getDecorations(build.branch),
                getLastBuild(build.branch.id).orElse(null),
                getPromotionLevelListForBranch(build.branch.id)
                        .map { promotionLevel ->
                            PromotionView(
                                    promotionLevel,
                                    getEarliestPromotionRunAfterBuild(promotionLevel, build).orElse(null)
                            )
                        }
        )
    }

    override fun findBuildAfterUsingNumericForm(id: ID, buildName: String): Optional<Build> {
        return if (StringUtils.isNumeric(buildName)) {
            structureRepository.findBuildAfterUsingNumericForm(id, buildName)
        } else {
            throw IllegalArgumentException("Build name is expected to be numeric: $buildName")
        }
    }

    override fun getBuildView(build: Build, withDecorations: Boolean): BuildView {
        var view = BuildView.of(build)
                .withPromotionRuns(getLastPromotionRunsForBuild(build.id))
                .withValidationStampRunViews(getValidationStampRunViewsForBuild(build))
        if (withDecorations) {
            view = view.withDecorations(decorationService.getDecorations(build))
        }
        return view
    }

    override fun getValidationStampImage(validationStampId: ID): Document {
        // Checks access
        getValidationStamp(validationStampId)
        // Repository access
        return structureRepository.getValidationStampImage(validationStampId)
    }

    override fun setValidationStampImage(validationStampId: ID, document: Document?) {
        // Checks the image type
        checkImage(document)
        // Checks access
        val validationStamp = getValidationStamp(validationStampId)
        securityService.checkProjectFunction(validationStamp.branch.project.id(), ValidationStampEdit::class.java)
        // Repository
        structureRepository.setValidationStampImage(validationStampId, document)
        // Event
        eventPostService.post(eventFactory.imageValidationStamp(validationStamp))
    }

    override fun saveValidationStamp(validationStamp: ValidationStamp) {
        // Validation
        isEntityDefined(validationStamp, "Validation stamp must be defined")
        isEntityDefined(validationStamp.branch, "Branch must be defined")
        isEntityDefined(validationStamp.branch.project, "Project must be defined")
        // Security
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampEdit::class.java)
        // Repository
        structureRepository.saveValidationStamp(validationStamp)
        // Event
        eventPostService.post(eventFactory.updateValidationStamp(validationStamp))
    }

    override fun deleteValidationStamp(validationStampId: ID): Ack {
        Validate.isTrue(validationStampId.isSet, "Validation stamp ID must be set")
        val validationStamp = getValidationStamp(validationStampId)
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampDelete::class.java)
        eventPostService.post(eventFactory.deleteValidationStamp(validationStamp))
        return structureRepository.deleteValidationStamp(validationStampId)
    }

    override fun reorderValidationStamps(branchId: ID, reordering: Reordering) {
        // Loads the branch
        val branch = getBranch(branchId)
        // Checks the access rights
        securityService.checkProjectFunction(branch.projectId(), ValidationStampEdit::class.java)
        // Loads the validation stamps
        val validationStamps = getValidationStampListForBranch(branchId)
        // Checks the size
        if (reordering.ids.size != validationStamps.size) {
            throw ReorderingSizeException("The reordering request should have the same number of IDs as the number" + " of the validation stamps")
        }
        // Actual reordering
        structureRepository.reorderValidationStamps(branchId, reordering)
        // Event
        eventPostService.post(eventFactory.reorderValidationStamps(branch))
    }

    override fun getOrCreateValidationStamp(branch: Branch, validationStampName: String): ValidationStamp {
        var oValidationStamp = findValidationStampByName(
                branch.project.name,
                branch.name,
                validationStampName
        )
        if (oValidationStamp.isPresent) {
            return oValidationStamp.get()
        } else {
            val properties = propertyService.getProperties(branch.project)
            for (property in properties) {
                val type = property.type
                if (type is ValidationStampPropertyType<*> && !property.isEmpty) {
                    oValidationStamp = getValidationStampFromProperty(
                            property,
                            branch,
                            validationStampName
                    )
                    if (oValidationStamp.isPresent) {
                        return oValidationStamp.get()
                    }
                }
            }
            throw ValidationStampNotFoundException(
                    branch.project.name,
                    branch.name,
                    validationStampName
            )
        }
    }

    override fun bulkUpdateValidationStamps(validationStampId: ID): Ack {
        // Checks access
        securityService.checkGlobalFunction(ValidationStampBulkUpdate::class.java)
        // As admin
        securityService.asAdmin {
            val validationStamp = getValidationStamp(validationStampId)
            // Defining or replacing the predefined validation stamp
            val o = predefinedValidationStampService.findPredefinedValidationStampByName(validationStamp.name)
            if (o.isPresent) {
                // Updating the predefined validation stamp description & data type
                predefinedValidationStampService.savePredefinedValidationStamp(
                        o.get()
                                .withDescription(validationStamp.description)
                                .withDataType(validationStamp.dataType)
                )
                // Sets its image
                val image = getValidationStampImage(validationStampId)
                predefinedValidationStampService.setPredefinedValidationStampImage(
                        o.get().id,
                        image
                )
            } else {
                // Creating the predefined validation stamp
                val predefinedValidationStamp = predefinedValidationStampService.newPredefinedValidationStamp(
                        PredefinedValidationStamp.of(
                                NameDescription.nd(
                                        validationStamp.name,
                                        validationStamp.description
                                )
                        ).withDataType(validationStamp.dataType)
                )
                // Sets its image
                val image = getValidationStampImage(validationStampId)
                if (!image.isEmpty) {
                    predefinedValidationStampService.setPredefinedValidationStampImage(
                            predefinedValidationStamp.id,
                            image
                    )
                }
            }
            // For all validation stamps
            structureRepository.bulkUpdateValidationStamps(validationStampId)
        }
        // OK
        return Ack.OK
    }

    override fun bulkUpdatePromotionLevels(promotionLevelId: ID): Ack {
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // As admin
        securityService.asAdmin {
            val promotionLevel = getPromotionLevel(promotionLevelId)
            // Defining or replacing the predefined promotion level
            val o = predefinedPromotionLevelService.findPredefinedPromotionLevelByName(promotionLevel.name)
            if (o.isPresent) {
                // Updating the predefined promotion level description
                predefinedPromotionLevelService.savePredefinedPromotionLevel(
                        o.get().withDescription(promotionLevel.description)
                )
                // Sets its image
                val image = getPromotionLevelImage(promotionLevelId)
                predefinedPromotionLevelService.setPredefinedPromotionLevelImage(
                        o.get().id,
                        image
                )
            } else {
                // Creating the predefined promotion level
                val predefinedPromotionLevel = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                        PredefinedPromotionLevel.of(
                                NameDescription.nd(
                                        promotionLevel.name,
                                        promotionLevel.description
                                )
                        )
                )
                // Sets its image
                val image = getPromotionLevelImage(promotionLevelId)
                predefinedPromotionLevelService.setPredefinedPromotionLevelImage(
                        predefinedPromotionLevel.id,
                        image
                )
            }
            // For all promotion levels
            structureRepository.bulkUpdatePromotionLevels(promotionLevelId)
        }
        // OK
        return Ack.OK
    }

    protected fun <T> getValidationStampFromProperty(
            property: Property<T>,
            branch: Branch,
            validationStampName: String?): Optional<ValidationStamp> {
        val validationStampPropertyType = property.type as ValidationStampPropertyType<T>
        return validationStampPropertyType.getOrCreateValidationStamp(
                property.value,
                branch,
                validationStampName
        )
    }

    override fun newValidationStampFromPredefined(branch: Branch, stamp: PredefinedValidationStamp): ValidationStamp {
        val validationStamp = rawNewValidationStamp(
                ValidationStamp.of(
                        branch,
                        NameDescription.nd(stamp.name, stamp.description)
                ).withDataType(stamp.dataType)
        )
        // Image?
        if (stamp.image != null && stamp.image!!) {
            setValidationStampImage(
                    validationStamp.id,
                    predefinedValidationStampService.getPredefinedValidationStampImage(stamp.id)
            )
        }
        // OK
        return validationStamp
    }

    override fun newValidationRun(build: Build, validationRunRequest: ValidationRunRequest): ValidationRun {
        // Gets the validation stamp
        val validationStamp = getOrCreateValidationStamp(
                build.branch,
                validationRunRequest.validationStampName)
        // Validation run data
        val rawDataTypeId: String? = validationRunRequest.dataTypeId
                ?: validationStamp.dataType?.descriptor?.id
        val rawData: Any? = validationRunRequest.data
        // Gets the data type
        val rawDataType: ValidationDataType<Any, Any>? = rawDataTypeId?.run {
            validationDataTypeService.getValidationDataType(this)
                    ?: throw ValidationRunDataTypeNotFoundException(this)
        }
        // Type descriptor + data
        val rawRunData: ValidationRunData<Any>? = if (rawDataType != null && rawData != null) {
            ValidationRunData(
                    rawDataType.descriptor,
                    rawData
            )
        } else {
            null
        }
        // Validation of the run data
        val status: ValidationRunDataWithStatus<Any> = validationDataTypeService.validateData(
                rawRunData,
                validationStamp.dataType,
                validationRunRequest.validationRunStatusId
        )
        // Validation run to create
        val validationRun = ValidationRun.of(
                build,
                validationStamp,
                0,
                securityService.currentSignature,
                status.runStatusID,
                validationRunRequest.description
        ).withData(status.runData)
        // Validation
        isEntityNew(validationRun, "Validation run must be new")
        isEntityDefined(validationRun.build, "Build must be defined")
        isEntityDefined(validationRun.validationStamp, "Validation stamp must be defined")
        Validate.isTrue(validationRun.validationStamp.branch.id() == validationRun.build.branch.id(),
                "Validation run for a validation stamp can be done only on the same branch than the build.")
        // Checks the authorization
        securityService.checkProjectFunction(validationRun.build.branch.project.id(), ValidationRunCreate::class.java)
        // Actual creation
        val newValidationRun = structureRepository.newValidationRun(validationRun) { validationRunStatusService.getValidationRunStatus(it) }
        // Event
        eventPostService.post(eventFactory.newValidationRun(newValidationRun))
        // Metrics
        publishValidationRunMetrics(newValidationRun)
        // Saves the properties
        for ((propertyTypeName, propertyData) in validationRunRequest.properties) {
            propertyService.editProperty(
                    newValidationRun,
                    propertyTypeName,
                    propertyData
            )
        }
        // OK
        return newValidationRun
    }

    private fun publishValidationRunMetrics(validationRun: ValidationRun) {
        try {
            extensionManager.getExtensions(ValidationRunMetricsExtension::class.java).forEach { it.onValidationRun(validationRun) }
        } catch (ex: Exception) {
            logger.error("Cannot publish metrics for ${validationRun.entityDisplayName}", ex)
        }
    }

    override fun getValidationRun(validationRunId: ID): ValidationRun {
        val validationRun = structureRepository.getValidationRun(validationRunId) { validationRunStatusService.getValidationRunStatus(it) }
        securityService.checkProjectFunction(validationRun.build.branch.project.id(), ProjectView::class.java)
        return validationRun
    }

    override fun getValidationRunsForBuild(buildId: ID): List<ValidationRun> {
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsForBuild(build) { validationRunStatusService.getValidationRunStatus(it) }
    }

    override fun getValidationRunsForBuild(buildId: ID, offset: Int, count: Int): List<ValidationRun> {
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsForBuild(build, offset, count) { validationRunStatusService.getValidationRunStatus(it) }
    }

    override fun getValidationRunsCountForBuild(buildId: ID): Int {
        val build = getBuild(buildId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsCountForBuild(build)
    }

    override fun getValidationRunsForBuildAndValidationStamp(buildId: ID, validationStampId: ID): List<ValidationRun> {
        val build = getBuild(buildId)
        val validationStamp = getValidationStamp(validationStampId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsForBuildAndValidationStamp(build, validationStamp) { validationRunStatusService.getValidationRunStatus(it) }
    }

    override fun getValidationRunsForBuildAndValidationStamp(buildId: ID, validationStampId: ID, offset: Int, count: Int): List<ValidationRun> {
        val build = getBuild(buildId)
        val validationStamp = getValidationStamp(validationStampId)
        securityService.checkProjectFunction(build.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsForBuildAndValidationStamp(
                build,
                validationStamp,
                offset,
                count
        ) { validationRunStatusService.getValidationRunStatus(it) }
    }

    override fun getValidationRunsForValidationStamp(validationStampId: ID, offset: Int, count: Int): List<ValidationRun> {
        val validationStamp = getValidationStamp(validationStampId)
        securityService.checkProjectFunction(validationStamp.branch.project.id(), ProjectView::class.java)
        return structureRepository.getValidationRunsForValidationStamp(validationStamp, offset, count) { validationRunStatusService.getValidationRunStatus(it) }
    }

    override fun newValidationRunStatus(validationRun: ValidationRun, runStatus: ValidationRunStatus): ValidationRun {
        // Entity check
        Entity.isEntityDefined(validationRun, "Validation run must be defined")
        // Security check
        securityService.checkProjectFunction(validationRun.build.branch.project.id(), ValidationRunStatusChange::class.java)
        // Transition check
        validationRunStatusService.checkTransition(validationRun.lastStatus.statusID, runStatus.statusID)
        // Creation
        val newValidationRun = structureRepository.newValidationRunStatus(validationRun, runStatus)
        // Event
        eventPostService.post(eventFactory.newValidationRunStatus(newValidationRun))
        // OK
        return newValidationRun
    }

    override fun getValidationRunsCountForBuildAndValidationStamp(buildId: ID, validationStampId: ID): Int {
        return structureRepository.getValidationRunsCountForBuildAndValidationStamp(buildId, validationStampId)
    }

    override fun getValidationRunsCountForValidationStamp(validationStampId: ID): Int {
        return structureRepository.getValidationRunsCountForValidationStamp(validationStampId)
    }

    override fun findProjectByName(project: String): Optional<Project> {
        return structureRepository.getProjectByName(project)
                .filter { p -> securityService.isGlobalFunctionGranted(ProjectList::class.java) || securityService.isProjectFunctionGranted(p.id(), ProjectView::class.java) }
    }

    @Throws(AccessDeniedException::class)
    override fun findProjectByNameIfAuthorized(project: String): Project? {
        // Looks for the project as admin
        val o = securityService.asAdmin<Optional<Project>> { findProjectByName(project) }
        // If it exists
        if (o.isPresent) {
            val p = o.get()
            // If it is authorized
            return if (securityService.isProjectFunctionGranted(p, ProjectView::class.java)) {
                p
            } else {
                throw AccessDeniedException("Project access not granted.")
            }
        } else {
            return null
        }// If it does not exist
    }

    override fun findBranchByName(project: String, branch: String): Optional<Branch> {
        return structureRepository.getBranchByName(project, branch)
                .filter { b -> securityService.isProjectFunctionGranted(b.projectId(), ProjectView::class.java) }
    }

    override fun findPromotionLevelByName(project: String, branch: String, promotionLevel: String): Optional<PromotionLevel> {
        return structureRepository.getPromotionLevelByName(project, branch, promotionLevel)
                .filter { pl -> securityService.isProjectFunctionGranted(pl.projectId(), ProjectView::class.java) }
    }

    override fun entityLoader(): BiFunction<ProjectEntityType, ID, ProjectEntity> {
        return BiFunction { projectEntityType, id -> projectEntityType.getEntityFn(this@StructureServiceImpl).apply(id) }
    }
}
