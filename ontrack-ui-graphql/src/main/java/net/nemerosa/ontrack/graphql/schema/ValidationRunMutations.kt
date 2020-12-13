package net.nemerosa.ontrack.graphql.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataJSONInputException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class ValidationRunMutations(
    private val structureService: StructureService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = CREATE_VALIDATION_RUN,
            description = "Creating a validation run for a build",
            input = CreateValidationRunInput::class,
            outputName = "validationRun",
            outputDescription = "Created validation run",
            outputType = ValidationRun::class
        ) { input ->
            val build = (structureService.findBuildByName(input.project, input.branch, input.build)
                .getOrNull()
                ?: throw BuildNotFoundException(input.project, input.branch, input.build))
            structureService.newValidationRun(
                build = build,
                validationRunRequest = ValidationRunRequest(
                    validationStampName = input.validationStamp,
                    validationRunStatusId = input.validationRunStatus?.let(validationRunStatusService::getValidationRunStatus),
                    dataTypeId = input.dataTypeId,
                    data = parseValidationRunData(build, input.validationStamp, input.dataTypeId, input.data),
                    description = input.description
                )
            )
        }
    )

    fun parseValidationRunData(
        build: Build,
        validationStampName: String,
        dataTypeId: String?,
        data: JsonNode?
    ): Any? = data?.run {
        // Gets the validation stamp
        val validationStamp: ValidationStamp = structureService.getOrCreateValidationStamp(
            build.branch,
            validationStampName
        )
        // Gets the data type ID if any
        // First, the data type in the request, and if not specified, the type of the validation stamp
        val typeId: String? = dataTypeId
            ?: validationStamp.dataType?.descriptor?.id
        // If no type, ignore the data
        return typeId
            ?.run {
                // Gets the actual type
                validationDataTypeService.getValidationDataType<Any, Any>(this)
            }?.run {
                // Parses data from the form
                try {
                    fromForm(data)
                } catch (ex: JsonParseException) {
                    throw ValidationRunDataJSONInputException(ex, data)
                }
            }
    }

    companion object {
        const val CREATE_VALIDATION_RUN = "createValidationRun"
    }
}

class CreateValidationRunInput(
    @APIDescription("Project name")
    val project: String,
    @APIDescription("Branch name")
    val branch: String,
    @APIDescription("Build name")
    val build: String,
    @APIDescription("Validation stamp name")
    val validationStamp: String,
    @APIDescription("Validation run status")
    val validationRunStatus: String?,
    @APIDescription("Validation description")
    val description: String?,
    @APIDescription("Type of the data to associated with the validation")
    val dataTypeId: String?,
    @APIDescription("Data to associated with the validation")
    val data: JsonNode?

)