package net.nemerosa.ontrack.boot.ui

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.ValidationStamp.Companion.of
import net.nemerosa.ontrack.ui.support.UIUtils.setupDefaultImageCache
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/rest/structure")
class ValidationStampController(
    private val structureService: StructureService,
    private val validationDataTypeService: ValidationDataTypeService
) {

    // Validation stamps
    @GetMapping("branches/{branchId}/validationStamps")
    fun getValidationStampListForBranch(@PathVariable branchId: ID): List<ValidationStamp> {
        val (_, _, _, _, project) = structureService.getBranch(branchId)
        return structureService.getValidationStampListForBranch(branchId)
    }

    @PutMapping("branches/{branchId}/validationStamps/reorder")
    fun reorderValidationStampListForBranch(
        @PathVariable branchId: ID,
        @RequestBody reordering: Reordering?
    ): List<ValidationStamp> {
        // Reordering
        reordering?.let {
            structureService.reorderValidationStamps(branchId, reordering)
        }
        // OK
        return getValidationStampListForBranch(branchId)
    }

    @PostMapping("branches/{branchId}/validationStamps/create")
    fun newValidationStamp(
        @PathVariable branchId: ID,
        @RequestBody input: @Valid ValidationStampInput
    ): ResponseEntity<ValidationStamp> {
        // Gets the holding branch
        val branch = structureService.getBranch(branchId)
        // Validation
        val config: ValidationDataTypeConfig<*>? =
            validationDataTypeService.validateValidationDataTypeConfig<Any>(input.dataType?.id, input.dataType?.data)
        // Creates a new validation stamp
        val validationStamp = of(
            branch,
            input.asNameDescription()
        ).withDataType(config)
        // Saves it into the repository
        return ResponseEntity.ok(structureService.newValidationStamp(validationStamp))
    }

    @GetMapping("validationStamps/{validationStampId}")
    fun getValidationStamp(@PathVariable validationStampId: ID): ResponseEntity<ValidationStamp> {
        return ResponseEntity.ok(structureService.getValidationStamp(validationStampId))
    }

    @PutMapping("validationStamps/{validationStampId}/update")
    fun updateValidationStamp(
        @PathVariable validationStampId: ID,
        @RequestBody input: @Valid ValidationStampInput
    ): ResponseEntity<ValidationStamp> {
        // Gets from the repository
        var validationStamp = structureService.getValidationStamp(validationStampId)
        // Validation
        val dataTypeServiceConfig: ValidationDataTypeConfig<*>? =
            validationDataTypeService.validateValidationDataTypeConfig<Any>(input.dataType?.id, input.dataType?.data)
        // Updates
        validationStamp = validationStamp.update(input.asNameDescription()).withDataType(dataTypeServiceConfig)
        // Saves in repository
        structureService.saveValidationStamp(validationStamp)
        // As resource
        return ResponseEntity.ok(validationStamp)
    }

    @DeleteMapping("validationStamps/{validationStampId}")
    fun deleteValidationStamp(@PathVariable validationStampId: ID): ResponseEntity<Ack> {
        return ResponseEntity.ok(structureService.deleteValidationStamp(validationStampId))
    }

    @GetMapping("validationStamps/{validationStampId}/image")
    fun getValidationStampImage_(response: HttpServletResponse?, @PathVariable validationStampId: ID): Document {
        val image = structureService.getValidationStampImage(validationStampId)
        setupDefaultImageCache(response, image)
        return image
    }

    @PostMapping("validationStamps/{validationStampId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Deprecated("Will be removed in V5. Use the PUT method.")
    fun setValidationStampImage(@PathVariable validationStampId: ID, @RequestParam file: MultipartFile) {
        structureService.setValidationStampImage(
            validationStampId, Document(
                file.contentType!!,
                file.bytes
            )
        )
    }

    @PutMapping("validationStamps/{validationStampId}/image")
    @ResponseStatus(HttpStatus.OK)
    fun putValidationStampImage(@PathVariable validationStampId: ID, @RequestBody imageBase64: String) {
        structureService.setValidationStampImage(
            validationStampId,
            Document(
                "image/png",
                Base64.getDecoder().decode(imageBase64)
            )
        )
    }

    /**
     * Bulk update of all validation stamps in other projects/branches and in predefined validation stamps,
     * following the model designed by the validation stamp ID.
     *
     * @param validationStampId ID of the validation stamp model
     * @return Result of the update
     */
    @PutMapping("validationStamps/{validationStampId}/bulk")
    fun bulkUpdate(@PathVariable validationStampId: ID): ResponseEntity<Ack> {
        return ResponseEntity.ok(structureService.bulkUpdateValidationStamps(validationStampId))
    }
}