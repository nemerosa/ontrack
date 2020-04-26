package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.model.structure.Reordering;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Access to the list of predefined promotion levels.
 *
 * @see PredefinedPromotionLevel
 */
@RestController
@RequestMapping("/rest/admin")
public class PredefinedPromotionLevelController extends AbstractResourceController {

    private final PredefinedPromotionLevelService predefinedPromotionLevelService;

    @Autowired
    public PredefinedPromotionLevelController(PredefinedPromotionLevelService predefinedPromotionLevelService) {
        this.predefinedPromotionLevelService = predefinedPromotionLevelService;
    }

    /**
     * Gets the list of predefined promotion levels.
     */
    @RequestMapping(value = "predefinedPromotionLevels", method = RequestMethod.GET)
    public Resources<PredefinedPromotionLevel> getPredefinedPromotionLevelList() {
        return Resources.of(
                predefinedPromotionLevelService.getPredefinedPromotionLevels(),
                uri(on(getClass()).getPredefinedPromotionLevelList())
        ).with(
                Link.CREATE,
                uri(on(getClass()).getPredefinedPromotionLevelCreationForm())
        ).with(
                "_reorderPromotionLevels",
                uri(on(getClass()).reorderPromotionLevelListForBranch(null))
        );
    }

    @RequestMapping(value = "predefinedPromotionLevels/reorder", method = RequestMethod.PUT)
    public Resources<PredefinedPromotionLevel> reorderPromotionLevelListForBranch(@RequestBody Reordering reordering) {
        // Reordering
        predefinedPromotionLevelService.reorderPromotionLevels(reordering);
        // OK
        return getPredefinedPromotionLevelList();
    }

    @RequestMapping(value = "predefinedPromotionLevels/create", method = RequestMethod.GET)
    public Form getPredefinedPromotionLevelCreationForm() {
        return PredefinedPromotionLevel.form();
    }

    @RequestMapping(value = "predefinedPromotionLevels/create", method = RequestMethod.POST)
    public PredefinedPromotionLevel newPredefinedPromotionLevel(@RequestBody @Valid NameDescription nameDescription) {
        return predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                        nameDescription
                )
        );
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}", method = RequestMethod.GET)
    public PredefinedPromotionLevel getPromotionLevel(@PathVariable ID predefinedPromotionLevelId) {
        return predefinedPromotionLevelService.getPredefinedPromotionLevel(predefinedPromotionLevelId);
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}/update", method = RequestMethod.GET)
    public Form updatePromotionLevelForm(@PathVariable ID predefinedPromotionLevelId) {
        return predefinedPromotionLevelService.getPredefinedPromotionLevel(predefinedPromotionLevelId).asForm();
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}/update", method = RequestMethod.PUT)
    public PredefinedPromotionLevel updatePromotionLevel(@PathVariable ID predefinedPromotionLevelId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets from the repository
        PredefinedPromotionLevel promotionLevel = predefinedPromotionLevelService.getPredefinedPromotionLevel(predefinedPromotionLevelId);
        // Updates
        promotionLevel = promotionLevel.update(nameDescription);
        // Saves in repository
        predefinedPromotionLevelService.savePredefinedPromotionLevel(promotionLevel);
        // OK
        return promotionLevel;
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}", method = RequestMethod.DELETE)
    public Ack deletePromotionLevel(@PathVariable ID predefinedPromotionLevelId) {
        return predefinedPromotionLevelService.deletePredefinedPromotionLevel(predefinedPromotionLevelId);
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}/image", method = RequestMethod.GET)
    public Document getPromotionLevelImage(@PathVariable ID predefinedPromotionLevelId) {
        return predefinedPromotionLevelService.getPredefinedPromotionLevelImage(predefinedPromotionLevelId);
    }

    @RequestMapping(value = "predefinedPromotionLevels/{predefinedPromotionLevelId}/image", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setPromotionLevelImage(@PathVariable ID predefinedPromotionLevelId, @RequestParam MultipartFile file) throws IOException {
        predefinedPromotionLevelService.setPredefinedPromotionLevelImage(predefinedPromotionLevelId, new Document(
                file.getContentType(),
                file.getBytes()
        ));
    }

}
