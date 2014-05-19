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

    // Promotion levels

    List<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    PromotionLevel newPromotionLevel(PromotionLevel promotionLevel);

    PromotionLevel getPromotionLevel(ID promotionLevelId);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);
}
