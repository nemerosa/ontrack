package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface StructureService {

    List<ProjectStatusView> getProjectStatusViews();

    List<ProjectStatusView> getProjectStatusViewsForFavourites();

    List<Project> getProjectFavourites();

    // Projects

    Project newProject(Project project);

    List<Project> getProjectList();

    Project getProject(ID projectId);

    void saveProject(Project project);

    Ack deleteProject(ID projectId);

    // Branches

    Branch getBranch(ID branchId);

    List<Branch> getBranchesForProject(ID projectId);

    Branch newBranch(Branch branch);

    List<BranchStatusView> getBranchStatusViews(ID projectId);

    BranchStatusView getBranchStatusView(Branch branch);

    void saveBranch(Branch branch);

    Ack deleteBranch(ID branchId);

    // Builds

    Build newBuild(Build build);

    Build saveBuild(Build build);

    Build getBuild(ID buildId);

    Optional<Build> findBuildByName(String project, String branch, String build);

    BranchStatusView getEarliestPromotionsAfterBuild(Build build);

    /**
     * Finds a build on a branch whose name is the closest. It assumes that build names
     * are in a numeric format.
     */
    Optional<Build> findBuildAfterUsingNumericForm(ID id, String buildName);

    /**
     * Gets an aggregated view of a build, with its promotion runs, validation stamps and decorations.
     */
    BuildView getBuildView(Build build, boolean withDecorations);

    Build getLastBuildForBranch(Branch branch);

    /**
     * Gets the number of builds for a branch.
     */
    int getBuildCount(Branch branch);

    Ack deleteBuild(ID buildId);

    Optional<Build> getPreviousBuild(ID buildId);

    Optional<Build> getNextBuild(ID buildId);

    /**
     * Build links
     */

    void addBuildLink(Build fromBuild, Build toBuild);

    void deleteBuildLink(Build fromBuild, Build toBuild);

    List<Build> getBuildLinksFrom(Build build);

    List<Build> getBuildLinksTo(Build build);

    List<Build> searchBuildsLinkedTo(String projectName, String buildPattern);

    void editBuildLinks(Build build, BuildLinkForm form);

    boolean isLinkedFrom(Build build, String project, String buildPattern);

    boolean isLinkedTo(Build build, String project, String buildPattern);

    /**
     * Looks for the first build which matches a given predicate.
     *
     * @param branchId       Branch to look builds into
     * @param buildPredicate Predicate for a match
     * @param sortDirection  Build search direction
     * @return Build if found, empty otherwise
     */
    Optional<Build> findBuild(ID branchId, Predicate<Build> buildPredicate, BuildSortDirection sortDirection);

    Optional<Build> getLastBuild(ID branchId);

    List<Build> buildSearch(ID projectId, BuildSearchForm form);

    List<ValidationStampRunView> getValidationStampRunViewsForBuild(Build build);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    void savePromotionLevel(PromotionLevel promotionLevel);

    Ack deletePromotionLevel(ID promotionLevelId);

    void reorderPromotionLevels(ID branchId, Reordering reordering);

    PromotionLevel newPromotionLevelFromPredefined(Branch branch, PredefinedPromotionLevel predefinedPromotionLevel);

    PromotionLevel getOrCreatePromotionLevel(Branch branch, Integer promotionLevelId, String promotionLevelName);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    Optional<PromotionLevel> findPromotionLevelByName(String project, String branch, String promotionLevel);

    List<PromotionRun> getPromotionRunsForBuild(ID buildId);

    List<PromotionRun> getLastPromotionRunsForBuild(ID buildId);

    Optional<PromotionRun> getLastPromotionRunForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel);

    List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel);

    PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel);

    PromotionRunView getPromotionRunView(PromotionLevel promotionLevel);

    Ack deletePromotionRun(ID promotionRunId);

    Optional<PromotionRun> getEarliestPromotionRunAfterBuild(PromotionLevel promotionLevel, Build build);

    List<PromotionRun> getPromotionRunsForPromotionLevel(ID promotionLevelId);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Optional<ValidationStamp> findValidationStampByName(String project, String branch, String validationStamp);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    void saveValidationStamp(ValidationStamp validationStamp);

    Ack deleteValidationStamp(ID validationStampId);

    void reorderValidationStamps(ID branchId, Reordering reordering);

    ValidationStamp newValidationStampFromPredefined(Branch branch, PredefinedValidationStamp stamp);

    ValidationStamp getOrCreateValidationStamp(Branch branch, Integer validationStampId, String validationStampName);

    /**
     * Bulk update of all validation stamps in other projects/branches and in predefined validation stamps,
     * following the model designed by the validation stamp ID.
     *
     * @param validationStampId ID of the validation stamp model
     * @return Result of the update
     */
    Ack bulkUpdateValidationStamps(ID validationStampId);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun);

    ValidationRun getValidationRun(ID validationRunId);

    List<ValidationRun> getValidationRunsForBuild(ID buildId);

    List<ValidationRun> getValidationRunsForBuildAndValidationStamp(ID buildId, ID validationStampId);

    List<ValidationRun> getValidationRunsForValidationStamp(ID validationStampId, int offset, int count);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);

    // Entity searches by name

    Optional<Project> findProjectByName(String project);

    Optional<Branch> findBranchByName(String project, String branch);

    default BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader() {
        return (entityType, id) -> entityType.getEntityFn(this).apply(id);
    }
}
