package net.nemerosa.ontrack.graphql.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class ValidationStampMutations(
    private val structureService: StructureService,
    private val validationDataTypeService: ValidationDataTypeService
) : TypedMutationProvider() {

    override val mutations: List<Mutation>
        get() = listOf(
            /**
             * Setting up a validation stamp
             */
            simpleMutation(
                name = "setupValidationStamp",
                description = "Creates or updates a validation stamp for a branch",
                input = SetupValidationStampInput::class,
                outputName = "validationStamp",
                outputDescription = "Created or updated validation stamp",
                outputType = ValidationStamp::class,
                fetcher = this::setupValidationStamp
            )
        )

    private fun setupValidationStamp(input: SetupValidationStampInput): ValidationStamp {
        val existing =
            structureService.findValidationStampByName(input.project, input.branch, input.validation).getOrNull()
        return if (existing != null) {
            // Updates the validation if need be
            updateValidationStamp(existing, input)
        } else {
            createValidationStamp(input)
        }
    }

    private fun updateValidationStamp(existing: ValidationStamp, input: SetupValidationStampInput): ValidationStamp {
        val dataTypeConfig = validationDataTypeService.validateValidationDataTypeConfig<Any>(
            input.dataType,
            input.dataTypeConfig
        )
        val updated = existing.update(
            NameDescription.nd(input.validation, input.description)
        ).withDataType(dataTypeConfig)
        // Saves in repository
        structureService.saveValidationStamp(updated)
        // As resource
        return updated
    }

    private fun createValidationStamp(input: SetupValidationStampInput): ValidationStamp {
        val branch =
            structureService.findBranchByName(input.project, input.branch).getOrNull()
                ?: throw BranchNotFoundException(input.project, input.branch)
        val dataTypeConfig = validationDataTypeService.validateValidationDataTypeConfig<Any>(
            input.dataType,
            input.dataTypeConfig
        )
        val validationStamp = ValidationStamp.of(
            branch,
            NameDescription.nd(input.validation, input.description)
        ).withDataType(dataTypeConfig)
        // Saves it into the repository
        return structureService.newValidationStamp(validationStamp)
    }

}

/**
 * Input for the `setupValidationStamp` mutation.
 */
data class SetupValidationStampInput(
    val project: String,
    val branch: String,
    val validation: String,
    val description: String?,
    val dataType: String?,
    val dataTypeConfig: JsonNode?
)