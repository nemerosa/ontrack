package net.nemerosa.ontrack.boot.ui

import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp.Companion.of
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

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
) {
    /**
     * Gets the list of predefined validation stamps.
     */
    @GetMapping("predefinedValidationStamps")
    fun predefinedValidationStampList(): List<PredefinedValidationStamp> =
        predefinedValidationStampService.predefinedValidationStamps

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

    @PutMapping("predefinedValidationStamps/{predefinedValidationStampId}/image")
    @ResponseStatus(HttpStatus.OK)
    fun putPredefinedValidationStampImage(
        @PathVariable predefinedValidationStampId: ID,
        @RequestBody imageBase64: String?
    ) {
        predefinedValidationStampService.setPredefinedValidationStampImage(
            predefinedValidationStampId, Document(
                "image/png",
                Base64.getDecoder().decode(imageBase64)
            )
        )
    }

}