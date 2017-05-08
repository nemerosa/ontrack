package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.security.ValidationStampCreate;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static net.nemerosa.ontrack.boot.ui.UIUtils.setupDefaultImageCache;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class ValidationStampController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;
    private final DecorationService decorationService;

    @Autowired
    public ValidationStampController(StructureService structureService, SecurityService securityService, DecorationService decorationService) {
        this.structureService = structureService;
        this.securityService = securityService;
        this.decorationService = decorationService;
    }

    // Validation stamps

    @RequestMapping(value = "branches/{branchId}/validationStamps", method = RequestMethod.GET)
    public Resources<ValidationStamp> getValidationStampListForBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        return Resources.of(
                structureService.getValidationStampListForBranch(branchId),
                uri(on(ValidationStampController.class).getValidationStampListForBranch(branchId))
        )
                // Create
                .with(
                        Link.CREATE,
                        uri(on(ValidationStampController.class).newValidationStampForm(branchId)),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), ValidationStampCreate.class)
                )
                ;
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/view", method = RequestMethod.GET)
    @Transactional
    public Resources<ValidationStampView> getValidationStampViewListForBranch(@PathVariable ID branchId) {
        return getValidationStampListForBranch(branchId)
                .transform(validationStamp ->
                        ValidationStampView.of(
                                validationStamp,
                                decorationService.getDecorations(validationStamp)
                        )
                );
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/reorder", method = RequestMethod.PUT)
    public Resources<ValidationStamp> reorderValidationStampListForBranch(@PathVariable ID branchId, @RequestBody Reordering reordering) {
        // Reordering
        structureService.reorderValidationStamps(branchId, reordering);
        // OK
        return getValidationStampListForBranch(branchId);
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/create", method = RequestMethod.GET)
    public Form newValidationStampForm(@PathVariable ID branchId) {
        structureService.getBranch(branchId);
        return ValidationStamp.form();
    }

    @RequestMapping(value = "branches/{branchId}/validationStamps/create", method = RequestMethod.POST)
    public ValidationStamp newValidationStamp(@PathVariable ID branchId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Creates a new promotion level
        ValidationStamp validationStamp = ValidationStamp.of(branch, nameDescription);
        // Saves it into the repository
        return structureService.newValidationStamp(validationStamp);
    }

    @RequestMapping(value = "validationStamps/{validationStampId}", method = RequestMethod.GET)
    public ValidationStamp getValidationStamp(@PathVariable ID validationStampId) {
        return structureService.getValidationStamp(validationStampId);
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/update", method = RequestMethod.GET)
    public Form updateValidationStampForm(@PathVariable ID validationStampId) {
        return structureService.getValidationStamp(validationStampId).asForm();
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/update", method = RequestMethod.PUT)
    public ValidationStamp updateValidationStamp(@PathVariable ID validationStampId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets from the repository
        ValidationStamp validationStamp = structureService.getValidationStamp(validationStampId);
        // Updates
        validationStamp = validationStamp.update(nameDescription);
        // Saves in repository
        structureService.saveValidationStamp(validationStamp);
        // As resource
        return validationStamp;
    }

    @RequestMapping(value = "validationStamps/{validationStampId}", method = RequestMethod.DELETE)
    public Ack deleteValidationStamp(@PathVariable ID validationStampId) {
        return structureService.deleteValidationStamp(validationStampId);
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/image", method = RequestMethod.GET)
    public Document getValidationStampImage_(HttpServletResponse response, @PathVariable ID validationStampId) {
        Document image = structureService.getValidationStampImage(validationStampId);
        setupDefaultImageCache(response, image);
        return image;
    }

    @RequestMapping(value = "validationStamps/{validationStampId}/image", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setValidationStampImage(@PathVariable ID validationStampId, @RequestParam MultipartFile file) throws IOException {
        structureService.setValidationStampImage(validationStampId, new Document(
                file.getContentType(),
                file.getBytes()
        ));
    }

    /**
     * Bulk update of all validation stamps in other projects/branches and in predefined validation stamps,
     * following the model designed by the validation stamp ID.
     *
     * @param validationStampId ID of the validation stamp model
     * @return Result of the update
     */
    @PutMapping("validationStamps/{validationStampId}/bulk")
    public Ack bulkUpdate(@PathVariable ID validationStampId) {
        return structureService.bulkUpdateValidationStamps(validationStampId);
    }
}
