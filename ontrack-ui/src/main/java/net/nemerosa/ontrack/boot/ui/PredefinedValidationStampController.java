package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Access to the list of predefined validation stamps.
 *
 * @see PredefinedValidationStamp
 */
@RestController
@RequestMapping("/structure")
public class PredefinedValidationStampController extends AbstractResourceController {

    private final PredefinedValidationStampService predefinedValidationStampService;

    @Autowired
    public PredefinedValidationStampController(PredefinedValidationStampService predefinedValidationStampService) {
        this.predefinedValidationStampService = predefinedValidationStampService;
    }

    /**
     * Gets the list of predefined validation stamps.
     */
    @RequestMapping(value = "predefinedValidationStamps", method = RequestMethod.GET)
    public Resources<PredefinedValidationStamp> getPredefinedValidationStampList() {
        return Resources.of(
                predefinedValidationStampService.getPredefinedValidationStamps(),
                uri(on(getClass()).getPredefinedValidationStampList())
        ).with(
                Link.CREATE, uri(on(getClass()).getPredefinedValidationStampCreationForm())
        );
    }

    @RequestMapping(value = "predefinedValidationStamps/create", method = RequestMethod.GET)
    public Form getPredefinedValidationStampCreationForm() {
        return PredefinedValidationStamp.form();
    }

    @RequestMapping(value = "predefinedValidationStamps/create", method = RequestMethod.POST)
    public PredefinedValidationStamp newPredefinedValidationStamp(@RequestBody @Valid NameDescription nameDescription) {
        return predefinedValidationStampService.newPredefinedValidationStamp(
                PredefinedValidationStamp.of(
                        nameDescription
                )
        );
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}", method = RequestMethod.GET)
    public PredefinedValidationStamp getValidationStamp(@PathVariable ID predefinedValidationStampId) {
        return predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId);
    }

}
