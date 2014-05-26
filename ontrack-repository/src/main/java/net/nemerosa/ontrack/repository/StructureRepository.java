package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.*;

import java.util.List;

public interface StructureRepository {

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

    List<Build> builds(Branch branch, BuildFilter buildFilter);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    List<PromotionRun> getLastPromotionRunsForBuild(Build build);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);
}
