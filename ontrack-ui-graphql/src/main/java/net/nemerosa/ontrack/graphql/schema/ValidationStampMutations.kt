package net.nemerosa.ontrack.graphql.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
class ValidationStampMutations(
    private val structureService: StructureService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val securityService: SecurityService,
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
            ),
            /**
             * Creating a validation stamp from a branch ID
             */
            simpleMutation(
                "createValidationStampById",
                "Creates a new validation stamp from a branch ID",
                CreateValidationStampByIdInput::class,
                "validationStamp",
                "Created validation stamp",
                ValidationStamp::class
            ) { input ->
                val branch = structureService.getBranch(ID.of(input.branchId))
                createValidationStamp(
                    branch = branch,
                    validation = input.name,
                    description = input.description,
                    dataType = input.dataType,
                    dataTypeConfig = input.dataTypeConfig,
                )
            },
            /**
             * Updating an existing validation stamp
             */
            simpleMutation(
                "updateValidationStampById",
                "Updates an existing validation stamp",
                UpdateValidationStampByIdInput::class,
                "validationStamp",
                "Updated validation stamp",
                ValidationStamp::class
            ) { input ->
                updateValidationStamp(input)
            },
            /**
             * Deleting an existing validation stamp
             */
            unitMutation<DeleteValidationStampByIdInput>(
                "deleteValidationStampById",
                "Deletes an existing validation stamp"
            ) { input ->
                structureService.deleteValidationStamp(ID.of(input.id))
            },
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
        return createValidationStamp(
            branch = branch,
            validation = input.validation,
            description = input.description,
            dataType = input.dataType,
            dataTypeConfig = input.dataTypeConfig,
        )
    }

    private fun createValidationStamp(
        branch: Branch,
        validation: String,
        description: String?,
        dataType: String?,
        dataTypeConfig: JsonNode?,
    ): ValidationStamp {
        val actualDataTypeConfig = validationDataTypeService.validateValidationDataTypeConfig<Any>(
            dataType,
            dataTypeConfig
        )
        val validationStamp = ValidationStamp.of(
            branch,
            NameDescription.nd(validation, description)
        ).withDataType(actualDataTypeConfig)
        // Saves it into the repository
        return structureService.newValidationStamp(validationStamp)
    }

    private fun updateValidationStamp(
        input: UpdateValidationStampByIdInput,
    ): ValidationStamp {
        val vs = structureService.getValidationStamp(ID.of(input.id))
        val actualDataTypeConfig = if (input.dataType.isNullOrBlank()) {
            vs.dataType
        } else {
            validationDataTypeService.validateValidationDataTypeConfig<Any>(
                input.dataType,
                input.dataTypeConfig
            )
        }
        structureService.saveValidationStamp(
            ValidationStamp(
                id = vs.id,
                name = input.name?.takeIf { it.isNotBlank() } ?: vs.name,
                description = input.description ?: vs.description,
                branch = vs.branch,
                isImage = vs.isImage,
                signature = securityService.currentSignature,
                owner = vs.owner,
                dataType = actualDataTypeConfig,
            )
        )
        return structureService.getValidationStamp(ID.of(input.id))
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
    val dataTypeConfig: JsonNode?,
)

/**
 * Input for the `createValidationStamp` mutation
 */
data class CreateValidationStampByIdInput(
    @APIDescription("Branch ID")
    val branchId: Int,
    @get:NotNull(message = "The name is required.")
    @get:Pattern(regexp = ValidationStamp.NAME_REGEX, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Validation stamp name")
    val name: String,
    @APIDescription("Validation stamp description")
    val description: String,
    @APIDescription("FQCN of the data type")
    val dataType: String?,
    @APIDescription("Configuration of the data type")
    val dataTypeConfig: JsonNode?,
)


data class UpdateValidationStampByIdInput(
    @APIDescription("Validation stamp ID")
    val id: Int,
    @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Validation stamp name")
    val name: String?,
    @APIDescription("Validation stamp description")
    val description: String?,
    @APIDescription("FQCN of the data type")
    val dataType: String?,
    @APIDescription("Configuration of the data type")
    val dataTypeConfig: JsonNode?,
)

data class DeleteValidationStampByIdInput(
    @APIDescription("Validation stamp ID")
    val id: Int,
)
