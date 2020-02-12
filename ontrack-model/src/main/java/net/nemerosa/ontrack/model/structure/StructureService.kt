package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.security.access.AccessDeniedException
import java.util.*
import java.util.function.BiFunction
import java.util.function.Predicate

interface StructureService {

    val projectStatusViews: List<ProjectStatusView>

    val projectStatusViewsForFavourites: List<ProjectStatusView>

    val projectFavourites: List<Project>

    val projectList: List<Project>

    // Projects

    fun newProject(project: Project): Project

    fun getProject(projectId: ID): Project

    fun saveProject(project: Project)

    fun disableProject(project: Project): Project

    fun enableProject(project: Project): Project

    fun deleteProject(projectId: ID): Ack

    // Branches

    fun getBranch(branchId: ID): Branch

    fun getBranchesForProject(projectId: ID): List<Branch>

    fun newBranch(branch: Branch): Branch

    fun getBranchStatusViews(projectId: ID): List<BranchStatusView>

    fun getBranchStatusView(branch: Branch): BranchStatusView

    fun saveBranch(branch: Branch)

    fun disableBranch(branch: Branch): Branch

    fun enableBranch(branch: Branch): Branch

    fun deleteBranch(branchId: ID): Ack

    // Builds

    fun newBuild(build: Build): Build

    fun saveBuild(build: Build): Build

    fun getBuild(buildId: ID): Build

    // TODO Replace by Build?
    fun findBuildByName(project: String, branch: String, build: String): Optional<Build>

    fun getEarliestPromotionsAfterBuild(build: Build): BranchStatusView

    /**
     * Finds a build on a branch whose name is the closest. It assumes that build names
     * are in a numeric format.
     */
    // TODO Replace by Build?
    fun findBuildAfterUsingNumericForm(id: ID, buildName: String): Optional<Build>

    /**
     * Gets an aggregated view of a build, with its promotion runs, validation stamps and decorations.
     */
    fun getBuildView(build: Build, withDecorations: Boolean): BuildView

    fun getLastBuildForBranch(branch: Branch): Build?

    /**
     * Gets the number of builds for a branch.
     */
    fun getBuildCount(branch: Branch): Int

    fun deleteBuild(buildId: ID): Ack

    // TODO Replace by Build?
    fun getPreviousBuild(buildId: ID): Optional<Build>

    // TODO Replace by Build?
    fun getNextBuild(buildId: ID): Optional<Build>

    /**
     * Build links
     */

    fun addBuildLink(fromBuild: Build, toBuild: Build)

    fun deleteBuildLink(fromBuild: Build, toBuild: Build)

    @Deprecated("Use getBuildsUsedBy instead")
    fun getBuildLinksFrom(build: Build): List<Build>

    /**
     * Gets the builds used by the given one.
     *
     * @param build  Source build
     * @param offset Offset for pagination
     * @param size   Page size for pagination
     * @param filter Optional filter on the builds
     * @return List of builds which are used by the given one
     */
    fun getBuildsUsedBy(build: Build, offset: Int, size: Int, filter: (Build) -> Boolean = { true }): PaginatedList<Build>

    /**
     * Gets the builds which use the given one.
     *
     * @param build  Source build
     * @param offset Offset for pagination
     * @param size   Page size for pagination
     * @param filter Optional filter on the builds
     * @return List of builds which use the given one
     */
    fun getBuildsUsing(build: Build, offset: Int, size: Int, filter: (Build) -> Boolean = { true }): PaginatedList<Build>


    @Deprecated("Use getBuildsUsing instead")
    fun getBuildLinksTo(build: Build): List<Build>

    fun searchBuildsLinkedTo(projectName: String, buildPattern: String): List<Build>

    fun editBuildLinks(build: Build, form: BuildLinkForm)

    fun isLinkedFrom(build: Build, project: String, buildPattern: String): Boolean

    fun isLinkedTo(build: Build, project: String, buildPattern: String): Boolean

    /**
     * Looks for the first build which matches a given predicate.
     *
     * @param branchId       Branch to look builds into
     * @param buildPredicate Predicate for a match
     * @param sortDirection  Build search direction
     * @return Build if found, empty otherwise
     */
    @Deprecated("Must be replaced with the other `findBuild` method.")
    fun findBuild(branchId: ID, buildPredicate: Predicate<Build>, sortDirection: BuildSortDirection): Optional<Build>

    /**
     * Looks for the first build which matches a given predicate.
     *
     * @param branchId       Branch to look builds into
     * @param sortDirection  Build search direction
     * @param buildPredicate Predicate for a match
     * @return Build if found, `null` otherwise
     */
    fun findBuild(branchId: ID, sortDirection: BuildSortDirection, buildPredicate: (Build) -> Boolean): Build?

    /**
     * Loops over all the builds of a branch, stopping when [processor] returns `false`.
     *
     * @param branch Branch to look builds into
     * @param processor Must return `false` when the looping must stop
     * @param sortDirection  Build search direction
     */
    fun forEachBuild(branch: Branch, sortDirection: BuildSortDirection, processor: (Build) -> Boolean)

    // TODO Replace by Build?
    fun getLastBuild(branchId: ID): Optional<Build>

    fun buildSearch(projectId: ID, form: BuildSearchForm): List<Build>

    fun getValidationStampRunViewsForBuild(build: Build): List<ValidationStampRunView>

    // Promotion levels

    fun getPromotionLevelListForBranch(branchId: ID): List<PromotionLevel>

    fun newPromotionLevel(promotionLevel: PromotionLevel): PromotionLevel

    fun getPromotionLevel(promotionLevelId: ID): PromotionLevel

    fun getPromotionLevelImage(promotionLevelId: ID): Document

    fun setPromotionLevelImage(promotionLevelId: ID, document: Document?)

    fun savePromotionLevel(promotionLevel: PromotionLevel)

    fun deletePromotionLevel(promotionLevelId: ID): Ack

    fun reorderPromotionLevels(branchId: ID, reordering: Reordering)

    fun newPromotionLevelFromPredefined(branch: Branch, predefinedPromotionLevel: PredefinedPromotionLevel): PromotionLevel

    fun getOrCreatePromotionLevel(branch: Branch, promotionLevelId: Int?, promotionLevelName: String?): PromotionLevel

    // Promotion runs

    fun newPromotionRun(promotionRun: PromotionRun): PromotionRun

    fun getPromotionRun(promotionRunId: ID): PromotionRun

    // TODO Replace by PromotionLevel?
    fun findPromotionLevelByName(project: String, branch: String, promotionLevel: String): Optional<PromotionLevel>

    fun getPromotionRunsForBuild(buildId: ID): List<PromotionRun>

    fun getLastPromotionRunsForBuild(buildId: ID): List<PromotionRun>

    // TODO Replace by PromotionRun?
    fun getLastPromotionRunForBuildAndPromotionLevel(build: Build, promotionLevel: PromotionLevel): Optional<PromotionRun>

    fun getPromotionRunsForBuildAndPromotionLevel(build: Build, promotionLevel: PromotionLevel): List<PromotionRun>

    fun getLastPromotionRunForPromotionLevel(promotionLevel: PromotionLevel): PromotionRun?

    fun getPromotionRunView(promotionLevel: PromotionLevel): PromotionRunView

    fun deletePromotionRun(promotionRunId: ID): Ack

    // TODO Replace by PromotionRun?
    fun getEarliestPromotionRunAfterBuild(promotionLevel: PromotionLevel, build: Build): Optional<PromotionRun>

    fun getPromotionRunsForPromotionLevel(promotionLevelId: ID): List<PromotionRun>

    /**
     * Bulk update of all promotion levels in other projects/branches and in predefined promotion levels,
     * following the model designed by the promotion level ID.
     *
     * @param promotionLevelId ID of the promotion level model
     * @return Result of the update
     */
    fun bulkUpdatePromotionLevels(promotionLevelId: ID): Ack

    // Validation stamps

    fun getValidationStampListForBranch(branchId: ID): List<ValidationStamp>

    fun newValidationStamp(validationStamp: ValidationStamp): ValidationStamp

    fun getValidationStamp(validationStampId: ID): ValidationStamp

    // TODO Replace by ValidationStamp?
    fun findValidationStampByName(project: String, branch: String, validationStamp: String): Optional<ValidationStamp>

    fun getValidationStampImage(validationStampId: ID): Document

    fun setValidationStampImage(validationStampId: ID, document: Document?)

    fun saveValidationStamp(validationStamp: ValidationStamp)

    fun deleteValidationStamp(validationStampId: ID): Ack

    fun reorderValidationStamps(branchId: ID, reordering: Reordering)

    fun newValidationStampFromPredefined(branch: Branch, stamp: PredefinedValidationStamp): ValidationStamp

    fun getOrCreateValidationStamp(branch: Branch, validationStampName: String): ValidationStamp

    /**
     * Bulk update of all validation stamps in other projects/branches and in predefined validation stamps,
     * following the model designed by the validation stamp ID.
     *
     * @param validationStampId ID of the validation stamp model
     * @return Result of the update
     */
    fun bulkUpdateValidationStamps(validationStampId: ID): Ack

    // Validation runs

    fun newValidationRun(build: Build, validationRunRequest: ValidationRunRequest): ValidationRun

    fun getValidationRun(validationRunId: ID): ValidationRun


    @Deprecated("Use {@link #getValidationRunsForBuild(ID, int, int)} instead.")
    fun getValidationRunsForBuild(buildId: ID): List<ValidationRun>

    /**
     * Gets the list of validation runs for a build.
     *
     * @param buildId ID of the build
     * @param offset  Offset in the list
     * @param count   Maximum number of elements to return
     * @return List of validation runs
     */
    fun getValidationRunsForBuild(buildId: ID, offset: Int, count: Int): List<ValidationRun>

    /**
     * Gets the number of validation runs for a build.
     *
     * @param buildId ID of the build
     * @return Number of validation runs
     */
    fun getValidationRunsCountForBuild(buildId: ID): Int


    @Deprecated("Use {@link #getValidationRunsForBuildAndValidationStamp(ID, ID, int, int)} instead.")
    fun getValidationRunsForBuildAndValidationStamp(buildId: ID, validationStampId: ID): List<ValidationRun>

    /**
     * Gets the list of validation runs for a build and a validation stamp.
     *
     * @param buildId           ID of the build
     * @param validationStampId ID of the validation stamp
     * @param offset            Offset in the list
     * @param count             Maximum number of elemnts to return
     * @return List of validation runs
     */
    fun getValidationRunsForBuildAndValidationStamp(buildId: ID, validationStampId: ID, offset: Int, count: Int): List<ValidationRun>

    /**
     * Gets the list of validation runs for a build and a validation stamp, and a list of accepted statuses
     *
     * @param buildId           ID of the build
     * @param validationStampId ID of the validation stamp
     * @param statuses          List of statuses for the last status of the run
     * @param offset            Offset in the list
     * @param count             Maximum number of elemnts to return
     * @return List of validation runs
     */
    fun getValidationRunsForBuildAndValidationStampAndStatus(
            buildId: ID,
            validationStampId: ID,
            statuses: List<ValidationRunStatusID>,
            offset: Int,
            count: Int
    ): List<ValidationRun>

    fun getValidationRunsForValidationStamp(validationStamp: ValidationStamp, offset: Int, count: Int): List<ValidationRun>

    fun getValidationRunsForValidationStamp(validationStampId: ID, offset: Int, count: Int): List<ValidationRun>

    /**
     * Gets the list of validation runs for a given validation stamp and a list of statuses.
     * @param validationStamp   Validation stamp
     * @param statuses          List of statuses for the last status of the run
     * @param offset            Offset in the list
     * @param count             Maximum number of elemnts to return
     * @return List of validation runs
     */
    fun getValidationRunsForValidationStampAndStatus(
            validationStamp: ValidationStamp,
            statuses: List<ValidationRunStatusID>,
            offset: Int,
            count: Int
    ): List<ValidationRun>

    /**
     * Gets the list of validation runs for a given validation stamp and a list of statuses.
     * @param validationStampId ID of the validation stamp
     * @param statuses          List of statuses for the last status of the run
     * @param offset            Offset in the list
     * @param count             Maximum number of elemnts to return
     * @return List of validation runs
     */
    fun getValidationRunsForValidationStampAndStatus(
            validationStampId: ID,
            statuses: List<ValidationRunStatusID>,
            offset: Int,
            count: Int
    ): List<ValidationRun>

    /**
     * Gets the list of validation runs for a branch and a list of statuses.
     * @param branchId ID of the branch
     * @param statuses          List of statuses for the last status of the run
     * @param offset            Offset in the list
     * @param count             Maximum number of elemnts to return
     * @return List of validation runs
     */
    fun getValidationRunsForStatus(
            branchId: ID,
            statuses: List<ValidationRunStatusID>,
            offset: Int,
            count: Int
    ): List<ValidationRun>

    fun newValidationRunStatus(validationRun: ValidationRun, runStatus: ValidationRunStatus): ValidationRun

    /**
     * Re-exporting all validation run data metrics.
     */
    fun restoreValidationRunDataMetrics(logger: (String) -> Unit = {})

    /**
     * Gets the parent validation run for a given validation run status ID
     *
     * @param validationRunStatusId ID of the validation run status ID
     * @param checkForAccess `false` if the access for view must be checked
     * @return Validation run which contains the status ID or `null` if the current user
     * has no access to it. Never `null` if the [checkForAccess] if `true`.
     */
    fun getParentValidationRun(validationRunStatusId: ID, checkForAccess: Boolean = true): ValidationRun?

    /**
     * Gets a validation run status using its ID.
     *
     * @param id ID of the validation run status
     * @return Validation run status
     */
    fun getValidationRunStatus(id: ID): ValidationRunStatus

    /**
     * Edits a validation run status comment.
     *
     * @param run Parent validation run
     * @param runStatusId ID of the specific run status to edit
     * @param comment New comment
     * @return Updated validation run
     */
    fun saveValidationRunStatusComment(run: ValidationRun, runStatusId: ID, comment: String): ValidationRun

    /**
     * Checks if the validation run status comment is editable by the current user
     */
    fun isValidationRunStatusCommentEditable(validationRunStatus: ID): Boolean

    /**
     * Gets the total number of validation runs for a build and a validation stamp
     *
     * @param buildId           ID of the build
     * @param validationStampId ID of the validation stamp
     * @return Number of validation runs for the validation stamp
     */
    fun getValidationRunsCountForBuildAndValidationStamp(buildId: ID, validationStampId: ID): Int

    /**
     * Gets the total number of validation runs for a validation stamp
     *
     * @param validationStampId ID of the validation stamp
     * @return Number of validation runs for the validation stamp
     */
    fun getValidationRunsCountForValidationStamp(validationStampId: ID): Int

    // Entity searches by name

    // TODO Replace by Project?
    fun findProjectByName(project: String): Optional<Project>

    /**
     * Gets the project by its name if authorized to access it. If it does exist, but the user is
     * not authorized to see it, throws an [AccessDeniedException]
     *
     * @param project Name of the project to look for
     * @return Project if it exists and is authorized, or null if if does not exist
     * @throws AccessDeniedException If the project does exist but the user has no access to it
     */
    fun findProjectByNameIfAuthorized(project: String): Project?

    // TODO Replace by Branch?
    fun findBranchByName(project: String, branch: String): Optional<Branch>

    fun entityLoader(): BiFunction<ProjectEntityType, ID, ProjectEntity>

}
