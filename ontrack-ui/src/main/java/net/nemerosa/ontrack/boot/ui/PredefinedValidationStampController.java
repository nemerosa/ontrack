package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.ServiceConfigurator;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Access to the list of predefined validation stamps.
 *
 * @see PredefinedValidationStamp
 */
@RestController
@RequestMapping("/admin")
public class PredefinedValidationStampController extends AbstractResourceController {

    private final PredefinedValidationStampService predefinedValidationStampService;
    private final ValidationDataTypeService validationDataTypeService;

    @Autowired
    public PredefinedValidationStampController(PredefinedValidationStampService predefinedValidationStampService, ValidationDataTypeService validationDataTypeService) {
        this.predefinedValidationStampService = predefinedValidationStampService;
        this.validationDataTypeService = validationDataTypeService;
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
        return Form.nameAndDescription()
                .with(
                        ServiceConfigurator.of("dataType")
                                .label("Data type")
                                .help("Type of the data to associate with a validation run.")
                                .optional()
                                .sources(
                                        validationDataTypeService.getAllTypes().stream()
                                                .map(
                                                        dataType -> new ServiceConfigurationSource(
                                                                dataType.getClass().getName(),
                                                                dataType.getDisplayName(),
                                                                dataType.getConfigForm(null),
                                                                Collections.emptyMap()
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                );
    }

    @RequestMapping(value = "predefinedValidationStamps/create", method = RequestMethod.POST)
    public PredefinedValidationStamp newPredefinedValidationStamp(@RequestBody @Valid ValidationStampInput input) {
        ValidationDataTypeConfig<?> config = validateValidationDataTypeConfig(input);
        return predefinedValidationStampService.newPredefinedValidationStamp(
                PredefinedValidationStamp.of(
                        NameDescription.nd(input.getName(), input.getDescription())
                ).withDataType(config)
        );
    }

    private <C> ValidationDataTypeConfig<C> validateValidationDataTypeConfig(ValidationStampInput input) {
        // Validating the data type configuration if needed
        ServiceConfiguration inputConfig = input.getDataType();
        if (inputConfig != null) {
            ValidationDataType<C, ?> dataType = validationDataTypeService.getValidationDataType(inputConfig.getId());
            if (dataType != null) {
                // Parsing without exception
                return new ValidationDataTypeConfig<>(
                        dataType.getDescriptor(),
                        dataType.fromConfigForm(inputConfig.getData())
                );
            }
        }
        return null;
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}", method = RequestMethod.GET)
    public PredefinedValidationStamp getValidationStamp(@PathVariable ID predefinedValidationStampId) {
        return predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId);
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}/update", method = RequestMethod.GET)
    public Form updateValidationStampForm(@PathVariable ID predefinedValidationStampId) {
        PredefinedValidationStamp validationStamp = predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId);
        return getPredefinedValidationStampCreationForm()
                .fill("name", validationStamp.getName())
                .fill("description", validationStamp.getDescription())
                .fill("dataType", validationDataTypeService.getServiceConfigurationForConfig(validationStamp.getDataType()))
                ;
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}/update", method = RequestMethod.PUT)
    public PredefinedValidationStamp updateValidationStamp(@PathVariable ID predefinedValidationStampId, @RequestBody @Valid ValidationStampInput input) {
        // Gets from the repository
        PredefinedValidationStamp validationStamp = predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId);
        // Validation
        ValidationDataTypeConfig<?> dataTypeServiceConfig = validateValidationDataTypeConfig(input);
        // Updates
        validationStamp = validationStamp
                .update(NameDescription.nd(input.getName(), input.getDescription()))
                .withDataType(dataTypeServiceConfig)
        ;
        // Saves in repository
        predefinedValidationStampService.savePredefinedValidationStamp(validationStamp);
        // OK
        return validationStamp;
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}", method = RequestMethod.DELETE)
    public Ack deleteValidationStamp(@PathVariable ID predefinedValidationStampId) {
        return predefinedValidationStampService.deletePredefinedValidationStamp(predefinedValidationStampId);
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}/image", method = RequestMethod.GET)
    public Document getValidationStampImage(@PathVariable ID predefinedValidationStampId) {
        return predefinedValidationStampService.getPredefinedValidationStampImage(predefinedValidationStampId);
    }

    @RequestMapping(value = "predefinedValidationStamps/{predefinedValidationStampId}/image", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setValidationStampImage(@PathVariable ID predefinedValidationStampId, @RequestParam MultipartFile file) throws IOException {
        predefinedValidationStampService.setPredefinedValidationStampImage(predefinedValidationStampId, new Document(
                file.getContentType(),
                file.getBytes()
        ));
    }

}
