package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface StructureRepository {

    // Projects

    Project newProject(Project project);

    List<Project> getProjectList();

    Project getProject(ID projectId);

    Optional<Project> getProjectByName(String project);

    void saveProject(Project project);

    Ack deleteProject(ID projectId);

    // Branches

    Branch getBranch(ID branchId);

    Optional<Branch> getBranchByName(String project, String branch);

    List<Branch> getBranchesForProject(ID projectId);

    Branch newBranch(Branch branch);

    void saveBranch(Branch branch);

    Ack deleteBranch(ID branchId);

    // Builds

    Build newBuild(Build build);

    Build saveBuild(Build build);

    Build getBuild(ID buildId);

    Optional<Build> getBuildByName(String project, String branch, String build);

    Optional<Build> findBuildAfterUsingNumericForm(ID branchId, String buildName);

    int getBuildCount(Branch branch);

    Optional<Build> getPreviousBuild(Build build);

    Optional<Build> getNextBuild(Build build);

    /**
     * Iterates over the builds of the branch, from the newest to the oldest, until
     * the <code>buildPredicate</code> returns <code>false</code>.
     */
    default void builds(Branch branch, Predicate<Build> buildPredicate) {
        builds(branch, buildPredicate, BuildSortDirection.FROM_NEWEST);
    }

    /**
     * Iterates over the builds of the branch.
     */
    void builds(Branch branch, Predicate<Build> buildPredicate, BuildSortDirection sortDirection);

    /**
     * Iterates over the builds of the project, from the newest to the oldest, until
     * the <code>buildPredicate</code> returns <code>false</code>.
     */
    void builds(Project project, Predicate<Build> buildPredicate);

    Build getLastBuildForBranch(Branch branch);

    Ack deleteBuild(ID buildId);

    /**
     * Build links
     */

    void addBuildLink(ID fromBuildId, ID toBuildId);

    void deleteBuildLink(ID fromBuildId, ID toBuildId);

    @Deprecated
    List<Build> getBuildLinksFrom(ID buildId);

    /**
     * Gets the builds used by the given one.
     *
     * @param build Source build
     * @return List of builds used by the given one
     */
    List<Build> getBuildsUsedBy(Build build);

    /**
     * Gets the builds which use the given one.
     *
     * @param build Source build
     * @return List of builds which use the given one
     */
    List<Build> getBuildsUsing(Build build);

    /**
     * @deprecated Use {@link #getBuildsUsing(Build)} instead.
     */
    @Deprecated
    List<Build> getBuildLinksTo(ID buildId);

    List<Build> searchBuildsLinkedTo(String projectName, String buildPattern);

    boolean isLinkedFrom(ID id, String project, String buildPattern);

    boolean isLinkedTo(ID id, String project, String buildPattern);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Optional<PromotionLevel> getPromotionLevelByName(String project, String branch, String promotionLevel);

    Optional<PromotionLevel> getPromotionLevelByName(Branch branch, String promotionLevel);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    void savePromotionLevel(PromotionLevel promotionLevel);

    Ack deletePromotionLevel(ID promotionLevelId);

    void reorderPromotionLevels(ID branchId, Reordering reordering);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    Ack deletePromotionRun(ID promotionRunId);

    List<PromotionRun> getPromotionRunsForBuild(Build build);

    List<PromotionRun> getLastPromotionRunsForBuild(Build build);

    PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel);

    Optional<PromotionRun> getLastPromotionRun(Build build, PromotionLevel promotionLevel);

    List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel);

    List<PromotionRun> getPromotionRunsForPromotionLevel(PromotionLevel promotionLevel);

    Optional<PromotionRun> getEarliestPromotionRunAfterBuild(PromotionLevel promotionLevel, Build build);

    /**
     * Updates all promotion levels having the same name than the model, on all branches. The description
     * and the image are copied from the model.
     *
     * @param promotionLevelId ID of the model
     */
    void bulkUpdatePromotionLevels(ID promotionLevelId);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Optional<ValidationStamp> getValidationStampByName(String project, String branch, String validationStamp);

    Optional<ValidationStamp> getValidationStampByName(Branch branch, String validationStamp);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    void bulkUpdateValidationStamps(ID validationStampId);

    void saveValidationStamp(ValidationStamp validationStamp);

    Ack deleteValidationStamp(ID validationStampId);

    void reorderValidationStamps(ID branchId, Reordering reordering);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun, Function<String, ValidationRunStatusID> validationRunStatusService);

    ValidationRun getValidationRun(ID validationRunId, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * @deprecated Use {@link #getValidationRunsForBuild(Build, int, int, Function)} instead.
     */
    @Deprecated
    List<ValidationRun> getValidationRunsForBuild(Build build, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * Gets the list of validation runs for a build.
     *
     * @param build                      Build to get the validation runs for
     * @param offset                     Offset in the list
     * @param count                      Maximum number of elements to return
     * @param validationRunStatusService Run status mapping function (provided by caller)
     * @return List of validation runs
     */
    List<ValidationRun> getValidationRunsForBuild(Build build, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * Gets the number of validation runs for a build.
     *
     * @param build Build to get the validation runs for
     * @return Number of validation runs
     */
    int getValidationRunsCountForBuild(Build build);

    @Deprecated
    List<ValidationRun> getValidationRunsForBuildAndValidationStamp(Build build, ValidationStamp validationStamp, Function<String, ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForBuildAndValidationStamp(Build build, ValidationStamp validationStamp, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForBuildAndValidationStampAndStatus(Build build, ValidationStamp validationStamp, List<ValidationRunStatusID> statuses, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForValidationStamp(ValidationStamp validationStamp, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForValidationStampAndStatus(ValidationStamp validationStamp, List<ValidationRunStatusID> statuses, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * Gets the total number of validation runs for a build and a validation stamp
     *
     * @param buildId           ID of the build
     * @param validationStampId ID of the validation stamp
     * @return Number of validation runs for the validation stamp
     */
    int getValidationRunsCountForBuildAndValidationStamp(ID buildId, ID validationStampId);

    /**
     * Gets the total number of validation runs for a validation stamp
     *
     * @param validationStampId ID of the validation stamp
     * @return Number of validation runs for the validation stamp
     */
    int getValidationRunsCountForValidationStamp(ID validationStampId);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);


    /**
     * Gets the parent validation run for a given validation run status ID
     */
    @NotNull
    ValidationRun getParentValidationRun(@NotNull ID validationRunStatusId, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * Loads a validation run status
     */
    @Nullable
    ValidationRunStatus getValidationRunStatus(ID id, Function<String, ValidationRunStatusID> validationRunStatusService);

    /**
     * Saves a comment for a run status
     */
    void saveValidationRunStatusComment(@NotNull ValidationRunStatus runStatus, @NotNull String comment);
}
