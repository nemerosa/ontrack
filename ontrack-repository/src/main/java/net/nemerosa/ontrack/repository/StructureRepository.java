package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.*;

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

    /**
     * Iterates over the builds of the branch, from the newest to the oldest, until
     * the <code>buildPredicate</code> returns <code>false</code>.
     */
    void builds(Branch branch, Predicate<Build> buildPredicate);

    Build getLastBuildForBranch(Branch branch);

    Ack deleteBuild(ID buildId);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Optional<PromotionLevel> getPromotionLevelByName(String project, String branch, String promotionLevel);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    void savePromotionLevel(PromotionLevel promotionLevel);

    Ack deletePromotionLevel(ID promotionLevelId);

    void reorderPromotionLevels(ID branchId, Reordering reordering);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    Ack deletePromotionRun(ID promotionRunId);

    List<PromotionRun> getLastPromotionRunsForBuild(Build build);

    PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel);

    Optional<PromotionRun> getLastPromotionRun(Build build, PromotionLevel promotionLevel);

    List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel);

    List<PromotionRun> getPromotionRunsForPromotionLevel(PromotionLevel promotionLevel);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Optional<ValidationStamp> getValidationStampByName(String project, String branch, String validationStamp);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    void saveValidationStamp(ValidationStamp validationStamp);

    Ack deleteValidationStamp(ID validationStampId);

    void reorderValidationStamps(ID branchId, Reordering reordering);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun, Function<String,ValidationRunStatusID> validationRunStatusService);

    ValidationRun getValidationRun(ID validationRunId, Function<String,ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForBuild(Build build, Function<String,ValidationRunStatusID> validationRunStatusService);

    List<ValidationRun> getValidationRunsForValidationStamp(ValidationStamp validationStamp, int offset, int count, Function<String,ValidationRunStatusID> validationRunStatusService);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);
}
