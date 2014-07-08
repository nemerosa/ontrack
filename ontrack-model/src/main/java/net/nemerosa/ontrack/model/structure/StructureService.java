package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.Ack;

import java.util.List;
import java.util.Optional;

public interface StructureService {

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

    // Builds

    Build newBuild(Build build);

    Build saveBuild(Build build);

    Build getBuild(ID buildId);

    Optional<Build> findBuildByName(String project, String branch, String build);

    /**
     * Finds a build on a branch whose name is the closest. It assumes that build names
     * are in a numeric format.
     */
    Optional<Build> findBuildAfterUsingNumericForm(ID id, String buildName);

    BuildView getBuildView(Build build);

    Build getLastBuildForBranch(Branch branch);

    Ack deleteBuild(ID buildId);

    /**
     * Branch builds
     */
    // TODO Filter on builds
    List<Build> getFilteredBuilds(ID branchId);

    List<ValidationStampRunView> getValidationStampRunViewsForBuild(Build build);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    Optional<PromotionLevel> findPromotionLevelByName(String project, String branch, String promotionLevel);

    List<PromotionRun> getLastPromotionRunsForBuild(ID buildId);

    PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Optional<ValidationStamp> findValidationStampByName(String project, String branch, String validationStamp);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun);

    ValidationRun getValidationRun(ID validationRunId);

    List<ValidationRun> getValidationRunsForBuild(ID buildId);

    List<ValidationRun> getValidationRunsForValidationStamp(ID validationStampId, int offset, int count);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);

    // Entity searches by name

    Optional<Project> findProjectByName(String project);

    Optional<Branch> findBranchByName(String project, String branch);
}
