package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.BuildResource;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.PromotionRunCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class BuildController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public BuildController(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    // Builds

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.GET)
    public Form newBuildForm(@PathVariable ID branchId) {
        // Checks the branch does exist
        structureService.getBranch(branchId);
        // Returns the form
        return Build.form();
    }

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.POST)
    public Resource<Build> newBuild(@PathVariable ID branchId, @RequestBody NameDescription nameDescription) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Build signature
        Signature signature = securityService.getCurrentSignature();
        // Creates a new build
        Build build = Build.of(branch, nameDescription, signature);
        // Saves it into the repository
        build = structureService.newBuild(build);
        // OK
        return toBuildResource(build);
    }

    @RequestMapping(value = "builds/{buildId}", method = RequestMethod.GET)
    public BuildResource getBuild(@PathVariable ID buildId) {
        return toBuildResourceWithActions(
                structureService.getBuild(buildId)
        );
    }

    // Resource assemblers

    private BuildResource toBuildResource(Build build) {
        return new BuildResource(
                build,
                uri(on(getClass()).getBuild(build.getId()))
        );
    }

    private BuildResource toBuildResourceWithActions(Build build) {
        BuildResource resource = toBuildResource(build);
        // Creation of a promoted run
        resource.with(
                "promote",
                uri(on(StructureAPIController.class).newPromotedRun(build.getId())),
                securityService.isProjectFunctionGranted(build.getBranch().getProject().id(), PromotionRunCreate.class)
        );
        // TODO Update
        // TODO Delete
        return resource;
    }
}
