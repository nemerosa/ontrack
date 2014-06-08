package net.nemerosa.ontrack.model.structure;

import java.util.List;

public interface StructureService {

    // Projects

    Project newProject(Project project);

    List<Project> getProjectList();

    Project getProject(ID projectId);

    void saveProject(Project project);

    // Branches

    Branch getBranch(ID branchId);

    List<Branch> getBranchesForProject(ID projectId);

    Branch newBranch(Branch branch);

    // Builds

    Build newBuild(Build build);

    Build saveBuild(Build build);

    Build getBuild(ID buildId);

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

    List<PromotionRun> getLastPromotionRunsForBuild(ID buildId);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun);

    ValidationRun getValidationRun(ID validationRunId);

    List<ValidationRun> getValidationRunsForBuild(ID buildId);

    List<ValidationRun> getValidationRunsForValidationStamp(ID validationStampId, int offset, int count);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);
}
