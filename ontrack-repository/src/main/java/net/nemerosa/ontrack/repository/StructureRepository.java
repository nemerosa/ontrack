package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.*;

import java.util.List;
import java.util.Optional;

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

    // Builds

    Build newBuild(Build build);

    Build saveBuild(Build build);

    Build getBuild(ID buildId);

    Optional<Build> getBuildByName(String project, String branch, String build);

    List<Build> builds(Branch branch, BuildFilter buildFilter);

    Build getLastBuildForBranch(Branch branch);

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Optional<PromotionLevel> getPromotionLevelByName(String project, String branch, String promotionLevel);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    // Promotion runs

    PromotionRun newPromotionRun(PromotionRun promotionRun);

    PromotionRun getPromotionRun(ID promotionRunId);

    List<PromotionRun> getLastPromotionRunsForBuild(Build build);

    PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel);

    // Validation stamps

    List<ValidationStamp> getValidationStampListForBranch(ID branchId);

    ValidationStamp newValidationStamp(ValidationStamp validationStamp);

    ValidationStamp getValidationStamp(ID validationStampId);

    Optional<ValidationStamp> getValidationStampByName(String project, String branch, String validationStamp);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    // Validation runs

    ValidationRun newValidationRun(ValidationRun validationRun);

    ValidationRun getValidationRun(ID validationRunId);

    List<ValidationRun> getValidationRunsForBuild(Build build);

    List<ValidationRun> getValidationRunsForValidationStamp(ValidationStamp validationStamp, int offset, int count);

    ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus);
}
