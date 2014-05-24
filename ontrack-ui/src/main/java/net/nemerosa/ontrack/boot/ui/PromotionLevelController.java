package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.PromotionLevelCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
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
public class PromotionLevelController extends AbstractResourceController {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public PromotionLevelController(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    // Promotion levels

    @RequestMapping(value = "branches/{branchId}/promotionLevels", method = RequestMethod.GET)
    public ResourceCollection<PromotionLevel> getPromotionLevelListForBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        return ResourceCollection.of(
                structureService.getPromotionLevelListForBranch(branchId).stream().map(this::toPromotionLevelResource),
                uri(on(PromotionLevelController.class).getPromotionLevelListForBranch(branchId))
        )
                // Create
                .with(
                        Link.CREATE,
                        uri(on(PromotionLevelController.class).newPromotionLevelForm(branchId)),
                        securityService.isProjectFunctionGranted(branch.getProject().id(), PromotionLevelCreate.class)
                )
                ;
    }

    @RequestMapping(value = "branches/{branchId}/promotionLevels/create", method = RequestMethod.GET)
    public Form newPromotionLevelForm(@PathVariable ID branchId) {
        structureService.getBranch(branchId);
        return PromotionLevel.form();
    }

    @RequestMapping(value = "branches/{branchId}/promotionLevels/create", method = RequestMethod.POST)
    public Resource<PromotionLevel> newPromotionLevel(@PathVariable ID branchId, @RequestBody NameDescription nameDescription) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Creates a new promotion level
        PromotionLevel promotionLevel = PromotionLevel.of(branch, nameDescription);
        // Saves it into the repository
        promotionLevel = structureService.newPromotionLevel(promotionLevel);
        // OK
        return toPromotionLevelResource(promotionLevel);
    }

    @RequestMapping(value = "promotionLevels/{promotionLevelId}", method = RequestMethod.GET)
    public Resource<PromotionLevel> getPromotionLevel(@PathVariable ID promotionLevelId) {
        return toPromotionLevelResourceWithActions(
                structureService.getPromotionLevel(promotionLevelId)
        );
    }

    @RequestMapping(value = "promotionLevels/{promotionLevelId}/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getPromotionLevelImage_(@PathVariable ID promotionLevelId) {
        // Gets the file
        Document file = structureService.getPromotionLevelImage(promotionLevelId);
        if (file == null) {
            return new ResponseEntity<>(new byte[0], HttpStatus.NO_CONTENT);
        } else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentLength(file.getContent().length);
            responseHeaders.setContentType(MediaType.parseMediaType(file.getType()));
            return new ResponseEntity<>(file.getContent(), responseHeaders, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "promotionLevels/{promotionLevelId}/image", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setPromotionLevelImage(@PathVariable ID promotionLevelId, @RequestParam MultipartFile file) throws IOException {
        structureService.setPromotionLevelImage(promotionLevelId, new Document(
                file.getContentType(),
                file.getBytes()
        ));
    }

    // Resource assemblers

    private Resource<PromotionLevel> toPromotionLevelResourceWithActions(PromotionLevel promotionLevel) {
        return toPromotionLevelResource(promotionLevel);
        // TODO Update
        // TODO Delete
        // TODO Next promotion level
        // TODO Previous promotion level
    }

    private Resource<PromotionLevel> toPromotionLevelResource(PromotionLevel promotionLevel) {
        return Resource.of(
                promotionLevel,
                uri(on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId()))
        )
                // Branch link
                .with("branchLink", uri(on(BranchController.class).getBranch(promotionLevel.getBranch().getId())))
                        // Project link
                .with("projectLink", uri(on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId())))
                        // Image link
                .with("imageLink", uri(on(PromotionLevelController.class).getPromotionLevelImage_(promotionLevel.getId())))
                ;
    }
}
