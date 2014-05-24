package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class BranchController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public BranchController(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "projects/{projectId}/branches", method = RequestMethod.GET)
    public ResourceCollection<Branch> getBranchListForProject(@PathVariable ID projectId) {
        return ResourceCollection.of(
                structureService.getBranchesForProject(projectId).stream().map(this::toBranchResource),
                uri(on(BranchController.class).getBranchListForProject(projectId))
        )
                // Create
                .with(
                        Link.CREATE,
                        uri(on(BranchController.class).newBranch(projectId, null)),
                        securityService.isProjectFunctionGranted(projectId.getValue(), BranchCreate.class)
                )
                ;
    }

    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.GET)
    public Form newBranchForm(@PathVariable ID projectId) {
        // Checks the project exists
        structureService.getProject(projectId);
        // Returns the form
        return Branch.form();
    }

    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.POST)
    public Resource<Branch> newBranch(@PathVariable ID projectId, @RequestBody NameDescription nameDescription) {
        // Gets the project
        Project project = structureService.getProject(projectId);
        // Creates a new branch instance
        Branch branch = Branch.of(project, nameDescription);
        // Saves it into the repository
        branch = structureService.newBranch(branch);
        // OK
        return toBranchResource(branch);
    }

    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.GET)
    public Resource<Branch> getBranch(@PathVariable ID branchId) {
        return toBranchResourceWithActions(
                structureService.getBranch(branchId)
        );
    }

    @RequestMapping(value = "branches/{branchId}/view", method = RequestMethod.GET)
    // TODO Filter
    public BranchBuildView buildView(@PathVariable ID branchId) {
        // TODO Defines the filter for the service
        return structureService.getBranchBuildView(branchId);
    }

    // Resource assemblers

    private Resource<Branch> toBranchResourceWithActions(Branch branch) {
        return toBranchResource(branch)
                // TODO Update link (with authorisation)
                // TODO Delete link
                // TODO View link
                // TODO Builds link
                // Build creation
                .with(
                        "createBuild",
                        uri(on(BuildController.class).newBuild(branch.getId(), null)),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), BuildCreate.class)
                )
                        // Promotion level creation
                .with(
                        "createPromotionLevel",
                        uri(on(PromotionLevelController.class).newPromotionLevelForm(branch.getId())),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), PromotionLevelCreate.class)
                )
                        // Promotion level list
                .with(
                        "promotionLevels",
                        uri(on(PromotionLevelController.class).getPromotionLevelListForBranch(branch.getId()))
                )
                        // Validation stamp creation
                .with(
                        "createValidationStamp",
                        uri(on(ValidationStampController.class).newValidationStampForm(branch.getId())),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), ValidationStampCreate.class)
                )
                        // Validation stamp list
                .with(
                        "validationStamps",
                        uri(on(ValidationStampController.class).getValidationStampListForBranch(branch.getId()))
                )
                ;
    }

    private Resource<Branch> toBranchResource(Branch branch) {
        return Resource.of(
                branch,
                uri(on(BranchController.class).getBranch(branch.getId()))
        )
                // Branch's project
                .with("projectLink", uri(on(ProjectController.class).getProject(branch.getProject().getId())))
                ;
    }
}
