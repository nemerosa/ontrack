package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.BuildResource;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.web.bind.annotation.PathVariable;

// TODO Split in several controllers, one per resource type
// TODO The API interface is not needed
public interface StructureAPI {

    // Projects

    ResourceCollection<Project> getProjectList();

    Resource<Project> newProject(NameDescription nameDescription);

    Form newProjectForm();

    Resource<Project> getProject(ID id);

    Form saveProjectForm(ID id);

    Resource<Project> saveProject(ID id, NameDescription nameDescription);

    // Branches

    ResourceCollection<Branch> getBranchListForProject(ID projectId);

    Form newBranchForm(ID projectId);

    Resource<Branch> newBranch(ID projectId, NameDescription nameDescription);

    Resource<Branch> getBranch(ID branchId);

    // TODO Filter form/id
    BranchBuildView buildView(ID branchId);

    // Builds

    Form newBuildForm(ID branchId);

    Resource<Build> newBuild(ID branchId, NameDescription nameDescription);

    BuildResource getBuild(ID buildId);

    // Promotion levels

    ResourceCollection<PromotionLevel> getPromotionLevelListForBranch(ID branchId);

    Form newPromotionLevelForm(ID branchId);

    Resource<PromotionLevel> newPromotionLevel(ID branchId, NameDescription nameDescription);

    Resource<PromotionLevel> getPromotionLevel(ID promotionLevelId);

    Document getPromotionLevelImage(ID promotionLevelId);

    void setPromotionLevelImage(ID promotionLevelId, Document document);

    // Validation stamps

    ResourceCollection<ValidationStamp> getValidationStampListForBranch(ID branchId);

    Form newValidationStampForm(ID branchId);

    Resource<ValidationStamp> newValidationStamp(ID branchId, NameDescription nameDescription);

    Resource<ValidationStamp> getValidationStamp(ID validationStampId);

    Document getValidationStampImage(ID validationStampId);

    void setValidationStampImage(ID validationStampId, Document document);

    // Promoted runs

    Form newPromotedRun(@PathVariable ID buildId);

}
