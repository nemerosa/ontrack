package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.nameAndDescription
import net.nemerosa.ontrack.model.form.ServiceConfigurator
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp.Companion.of
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.stream.Collectors
import javax.validation.Valid

/**
 * Access to the list of predefined validation stamps.
 *
 * @see PredefinedValidationStamp
 */
@RestController
@RequestMapping("/rest/admin")
class PredefinedValidationStampController(
    private val predefinedValidationStampService: PredefinedValidationStampService,
    private val validationDataTypeService: ValidationDataTypeService
) : AbstractResourceController() {
    /**
     * Gets the list of predefined validation stamps.
     */
    @GetMapping("predefinedValidationStamps")
    fun predefinedValidationStampList(): Resources<PredefinedValidationStamp> = Resources.of(
        predefinedValidationStampService.predefinedValidationStamps,
        uri(MvcUriComponentsBuilder.on(javaClass).predefinedValidationStampList())
    ).with(
        Link.CREATE, uri(MvcUriComponentsBuilder.on(javaClass).predefinedValidationStampCreationForm())
    )

    @Suppress("DuplicatedCode")
    @GetMapping("predefinedValidationStamps/create")
    fun predefinedValidationStampCreationForm(): Form =
        nameAndDescription()
            .with(
                ServiceConfigurator.of("dataType")
                    .label("Data type")
                    .help("Type of the data to associate with a validation run.")
                    .optional()
                    .sources(
                        validationDataTypeService.getAllTypes().stream()
                            .map { dataType: ValidationDataType<*, *> ->
                                ServiceConfigurationSource(
                                    dataType.javaClass.name,
                                    dataType.displayName,
                                    dataType.getConfigForm(null), emptyMap<String, Any>())
                            }
                            .collect(Collectors.toList())
                    )
            )

    @PostMapping("predefinedValidationStamps/create")
    fun newPredefinedValidationStamp(@RequestBody input: @Valid ValidationStampInput): PredefinedValidationStamp {
        val config: ValidationDataTypeConfig<*>? =
            validationDataTypeService.validateValidationDataTypeConfig<Any>(input.dataType?.id, input.dataType?.data)
        return predefinedValidationStampService.newPredefinedValidationStamp(
            of(
                nd(input.name, input.description)
            ).withDataType(config)
        )
    }

    @GetMapping("predefinedValidationStamps/{predefinedValidationStampId}")
    fun getValidationStamp(@PathVariable predefinedValidationStampId: ID): PredefinedValidationStamp {
        return predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId)
    }

    @GetMapping("predefinedValidationStamps/{predefinedValidationStampId}/update")
    fun updateValidationStampForm(@PathVariable predefinedValidationStampId: ID): Form {
        val validationStamp = predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId)
        return predefinedValidationStampCreationForm()
            .fill("name", validationStamp.name)
            .fill("description", validationStamp.description)
            .fill("dataType", validationDataTypeService.getServiceConfigurationForConfig(validationStamp.dataType))
    }

    @PutMapping("predefinedValidationStamps/{predefinedValidationStampId}/update")
    fun updateValidationStamp(
        @PathVariable predefinedValidationStampId: ID,
        @RequestBody input: @Valid ValidationStampInput
    ): PredefinedValidationStamp {
        // Gets from the repository
        var validationStamp = predefinedValidationStampService.getPredefinedValidationStamp(predefinedValidationStampId)
        // Validation
        val dataTypeServiceConfig: ValidationDataTypeConfig<*>? =
            validationDataTypeService.validateValidationDataTypeConfig<Any>(input.dataType?.id, input.dataType?.data)
        // Updates
        validationStamp = validationStamp
            .update(nd(input.name, input.description))
            .withDataType(dataTypeServiceConfig)
        // Saves in repository
        predefinedValidationStampService.savePredefinedValidationStamp(validationStamp)
        // OK
        return validationStamp
    }

    @DeleteMapping("predefinedValidationStamps/{predefinedValidationStampId}")
    fun deleteValidationStamp(@PathVariable predefinedValidationStampId: ID): Ack {
        return predefinedValidationStampService.deletePredefinedValidationStamp(predefinedValidationStampId)
    }

    @GetMapping("predefinedValidationStamps/{predefinedValidationStampId}/image")
    fun getValidationStampImage(@PathVariable predefinedValidationStampId: ID): Document {
        return predefinedValidationStampService.getPredefinedValidationStampImage(predefinedValidationStampId)
    }

    @PostMapping("predefinedValidationStamps/{predefinedValidationStampId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun setValidationStampImage(@PathVariable predefinedValidationStampId: ID, @RequestParam file: MultipartFile) {
        predefinedValidationStampService.setPredefinedValidationStampImage(predefinedValidationStampId, Document(
            file.contentType!!,
            file.bytes
        ))
    }
}