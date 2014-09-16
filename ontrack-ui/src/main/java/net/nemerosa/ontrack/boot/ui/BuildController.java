package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/structure")
public class BuildController extends AbstractResourceController {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public BuildController(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.GET)
    public Form newBuildForm(@PathVariable ID branchId) {
        // Checks the branch does exist
        structureService.getBranch(branchId);
        // Returns the form
        return Build.form();
    }

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.POST)
    public Build newBuild(@PathVariable ID branchId, @RequestBody @Valid BuildRequest request) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Build signature
        Signature signature = securityService.getCurrentSignature();
        // Creates a new build
        Build build = Build.of(branch, request.asNameDescription(), signature);
        // Saves it into the repository
        build = structureService.newBuild(build);
        // Saves the properties
        for (PropertyCreationRequest propertyCreationRequest : request.getProperties()) {
            propertyService.editProperty(
                    build,
                    propertyCreationRequest.getPropertyTypeName(),
                    propertyCreationRequest.getPropertyData()
            );
        }
        // OK
        return build;
    }

    @RequestMapping(value = "builds/{buildId}/update", method = RequestMethod.GET)
    public Form updateBuildForm(@PathVariable ID buildId) {
        return structureService.getBuild(buildId).asForm();
    }

    @RequestMapping(value = "builds/{buildId}/update", method = RequestMethod.PUT)
    public Build updateBuild(@PathVariable ID buildId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets from the repository
        Build build = structureService.getBuild(buildId);
        // Updates
        build = build.update(nameDescription);
        // Saves in repository
        structureService.saveBuild(build);
        // As resource
        return build;
    }

    @RequestMapping(value = "builds/{buildId}", method = RequestMethod.DELETE)
    public Ack deleteBuild(@PathVariable ID buildId) {
        return structureService.deleteBuild(buildId);
    }

    @RequestMapping(value = "builds/{buildId}", method = RequestMethod.GET)
    public Build getBuild(@PathVariable ID buildId) {
        return structureService.getBuild(buildId);
    }

}
