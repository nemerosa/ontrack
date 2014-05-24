package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.DateTime;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.security.ValidationStampCreate;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class StructureAPIController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public StructureAPIController(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    // Validation stamps

    @RequestMapping(value = "branches/{branchId}/validationStamps", method = RequestMethod.GET)
    public ResourceCollection<ValidationStamp> getValidationStampListForBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        return ResourceCollection.of(
                structureService.getValidationStampListForBranch(branchId).stream().map(this::toValidationStampResource),
                uri(on(StructureAPIController.class).getValidationStampListForBranch(branchId))
        )
                // Create
                .with(
                        Link.CREATE,
                        uri(on(StructureAPIController.class).newValidationStampForm(branchId)),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), ValidationStampCreate.class)
                )
                ;
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/create", method = RequestMethod.GET)
    public Form newValidationStampForm(@PathVariable ID branchId) {
        structureService.getBranch(branchId);
        return ValidationStamp.form();
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/create", method = RequestMethod.POST)
    public Resource<ValidationStamp> newValidationStamp(@PathVariable ID branchId, @RequestBody NameDescription nameDescription) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Creates a new promotion level
        ValidationStamp validationStamp = ValidationStamp.of(branch, nameDescription);
        // Saves it into the repository
        validationStamp = structureService.newValidationStamp(validationStamp);
        // OK
        return toValidationStampResource(validationStamp);
    }

    @RequestMapping(value = "validationStamps/{validationStampId}", method = RequestMethod.GET)
    public Resource<ValidationStamp> getValidationStamp(@PathVariable ID validationStampId) {
        return toValidationStampResourceWithActions(
                structureService.getValidationStamp(validationStampId)
        );
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getValidationStampImage_(@PathVariable ID validationStampId) {
        // Gets the file
        Document file = structureService.getValidationStampImage(validationStampId);
        if (file == null) {
            return new ResponseEntity<>(new byte[0], HttpStatus.NO_CONTENT);
        } else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentLength(file.getContent().length);
            responseHeaders.setContentType(MediaType.parseMediaType(file.getType()));
            return new ResponseEntity<>(file.getContent(), responseHeaders, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/image", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setValidationStampImage(@PathVariable ID validationStampId, @RequestParam MultipartFile file) throws IOException {
        structureService.setValidationStampImage(validationStampId, new Document(
                file.getContentType(),
                file.getBytes()
        ));
    }

    // Promoted runs

    @RequestMapping(value = "builds/{buildId}/promotedRun/create", method = RequestMethod.GET)
    public Form newPromotedRun(@PathVariable ID buildId) {
        Build build = structureService.getBuild(buildId);
        return Form.create()
                .with(
                        Selection.of("promotionLevel")
                                .items(structureService.getPromotionLevelListForBranch(build.getBranch().getId()))
                )
                .with(
                        DateTime.of("dateTime")
                                .label("Date/time")
                                .minuteStep(15)
                )
                .description();
    }

    // Resource assemblers

    private Resource<ValidationStamp> toValidationStampResourceWithActions(ValidationStamp validationStamp) {
        return toValidationStampResource(validationStamp);
        // TODO Update
        // TODO Delete
        // TODO Next validation stamp
        // TODO Previous validation stamp
    }

    private Resource<ValidationStamp> toValidationStampResource(ValidationStamp validationStamp) {
        return Resource.of(
                validationStamp,
                uri(on(StructureAPIController.class).getValidationStamp(validationStamp.getId()))
        )
                // Branch link
                .with("branchLink", uri(on(BranchController.class).getBranch(validationStamp.getBranch().getId())))
                        // Project link
                .with("projectLink", uri(on(ProjectController.class).getProject(validationStamp.getBranch().getProject().getId())))
                        // Image link
                .with("imageLink", uri(on(StructureAPIController.class).getValidationStampImage_(validationStamp.getId())))
                ;
    }
}
