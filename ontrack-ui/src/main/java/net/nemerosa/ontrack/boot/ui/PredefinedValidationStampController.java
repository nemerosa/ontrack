package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Resources<PredefinedValidationStamp> getValidationStampList() {
        return Resources.of(
                predefinedValidationStampService.getPredefinedValidationStamps(),
                uri(on(getClass()).getValidationStampList())
        );
        // TODO Create link
    }

}
