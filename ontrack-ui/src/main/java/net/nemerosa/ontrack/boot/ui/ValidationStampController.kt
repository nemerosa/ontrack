package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.nameAndDescription
import net.nemerosa.ontrack.model.form.ServiceConfigurator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.ValidationStamp.Companion.of
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import net.nemerosa.ontrack.ui.support.UIUtils.setupDefaultImageCache
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/rest/structure")
class ValidationStampController(
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val decorationService: DecorationService,
    private val validationDataTypeService: ValidationDataTypeService
) : AbstractResourceController() {

    // Validation stamps
    @GetMapping("branches/{branchId}/validationStamps")
    fun getValidationStampListForBranch(@PathVariable branchId: ID): Resources<ValidationStamp> {
        val (_, _, _, _, project) = structureService.getBranch(branchId)
        return Resources.of(
            structureService.getValidationStampListForBranch(branchId),
            uri(MvcUriComponentsBuilder.on(ValidationStampController::class.java)
                .getValidationStampListForBranch(branchId))
        ) // Create
            .with(
                Link.CREATE,
                uri(MvcUriComponentsBuilder.on(ValidationStampController::class.java).newValidationStampForm(branchId)),
                securityService.isProjectFunctionGranted(project.id(), ValidationStampCreate::class.java)
            )
    }

    @GetMapping("branches/{branchId}/validationStamps/view")
    @Transactional
    fun getValidationStampViewListForBranch(@PathVariable branchId: ID): Resources<ValidationStampView> {
        return getValidationStampListForBranch(branchId)
            .transform { validationStamp: ValidationStamp? ->
                ValidationStampView.of(
                    validationStamp,
                    decorationService.getDecorations(validationStamp)
                )
            }
    }

    @PutMapping("branches/{branchId}/validationStamps/reorder")
    fun reorderValidationStampListForBranch(
        @PathVariable branchId: ID,
        @RequestBody reordering: Reordering?
    ): Resources<ValidationStamp> {
        // Reordering
        reordering?.let {
            structureService.reorderValidationStamps(branchId, reordering)
        }
        // OK
        return getValidationStampListForBranch(branchId)
    }

    @GetMapping("branches/{branchId}/validationStamps/create")
    fun newValidationStampForm(@PathVariable branchId: ID): Form {
        structureService.getBranch(branchId)
        return nameAndDescription()
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
    }

    @PostMapping("branches/{branchId}/validationStamps/create")
    fun newValidationStamp(
        @PathVariable branchId: ID,
        @RequestBody input: @Valid ValidationStampInput
    ): ValidationStamp {
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
        return structureService.newValidationStamp(validationStamp)
    }

    @GetMapping("validationStamps/{validationStampId}")
    fun getValidationStamp(@PathVariable validationStampId: ID): ValidationStamp {
        return structureService.getValidationStamp(validationStampId)
    }

    @GetMapping("validationStamps/{validationStampId}/update")
    fun updateValidationStampForm(@PathVariable validationStampId: ID): Form {
        val (_, name, description, branch, _, _, _, dataType) = structureService.getValidationStamp(
            validationStampId)
        return newValidationStampForm(branch.id)
            .fill("name", name)
            .fill("description", description)
            .fill("dataType", validationDataTypeService.getServiceConfigurationForConfig(dataType))
    }

    @PutMapping("validationStamps/{validationStampId}/update")
    fun updateValidationStamp(
        @PathVariable validationStampId: ID,
        @RequestBody input: @Valid ValidationStampInput
    ): ValidationStamp {
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
        return validationStamp
    }

    @DeleteMapping("validationStamps/{validationStampId}")
    fun deleteValidationStamp(@PathVariable validationStampId: ID): Ack {
        return structureService.deleteValidationStamp(validationStampId)
    }

    @GetMapping("validationStamps/{validationStampId}/image")
    fun getValidationStampImage_(response: HttpServletResponse?, @PathVariable validationStampId: ID): Document {
        val image = structureService.getValidationStampImage(validationStampId)
        setupDefaultImageCache(response, image)
        return image
    }

    @PostMapping("validationStamps/{validationStampId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun setValidationStampImage(@PathVariable validationStampId: ID, @RequestParam file: MultipartFile) {
        structureService.setValidationStampImage(validationStampId, Document(
            file.contentType!!,
            file.bytes
        ))
    }

    /**
     * Bulk update of all validation stamps in other projects/branches and in predefined validation stamps,
     * following the model designed by the validation stamp ID.
     *
     * @param validationStampId ID of the validation stamp model
     * @return Result of the update
     */
    @PutMapping("validationStamps/{validationStampId}/bulk")
    fun bulkUpdate(@PathVariable validationStampId: ID): Ack {
        return structureService.bulkUpdateValidationStamps(validationStampId)
    }
}