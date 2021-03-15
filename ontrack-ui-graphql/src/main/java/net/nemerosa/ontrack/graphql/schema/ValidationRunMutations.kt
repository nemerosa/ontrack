package net.nemerosa.ontrack.graphql.schema

import com.fasterxml.jackson.databind.JsonNode
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataJSONInputException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ValidationRunMutations(
    private val structureService: StructureService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val runInfoService: RunInfoService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = CREATE_VALIDATION_RUN_FOR_BUILD_BY_NAME,
            description = "Creating a validation run for a build identified by its name",
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
        },
        simpleMutation(
            name = CREATE_VALIDATION_RUN_FOR_BUILD_BY_ID,
            description = "Creating a validation run for a build identified by its ID",
            input = CreateValidationRunByIdInput::class,
            outputName = "validationRun",
            outputDescription = "Created validation run",
            outputType = ValidationRun::class
        ) { input ->
            val build = structureService.getBuild(ID.of(input.buildId))
            val run = structureService.newValidationRun(
                build = build,
                validationRunRequest = ValidationRunRequest(
                    validationStampName = input.validationStamp,
                    validationRunStatusId = input.validationRunStatus?.let(validationRunStatusService::getValidationRunStatus),
                    dataTypeId = input.dataTypeId,
                    data = parseValidationRunData(build, input.validationStamp, input.dataTypeId, input.data),
                    description = input.description
                )
            )
            // Run info
            if (input.runInfo != null) {
                runInfoService.setRunInfo(
                    entity = run,
                    input = input.runInfo,
                )
            }
            // OK
            run
        }
    )

    fun parseValidationRunData(
        build: Build,
        validationStampName: String,
        dataTypeId: String?,
        data: JsonNode?,
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
        const val CREATE_VALIDATION_RUN_FOR_BUILD_BY_ID = "createValidationRunById"
        const val CREATE_VALIDATION_RUN_FOR_BUILD_BY_NAME = "createValidationRun"
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
    val data: JsonNode?,
    @APIDescription("Run info")
    @TypeRef
    val runInfo: RunInfoInput?,
)

class CreateValidationRunByIdInput(
    @APIDescription("Build ID")
    val buildId: Int,
    @APIDescription("Validation stamp name")
    val validationStamp: String,
    @APIDescription("Validation run status")
    val validationRunStatus: String?,
    @APIDescription("Validation description")
    val description: String?,
    @APIDescription("Type of the data to associated with the validation")
    val dataTypeId: String?,
    @APIDescription("Data to associated with the validation")
    val data: JsonNode?,
    @APIDescription("Run info")
    @TypeRef
    val runInfo: RunInfoInput?,
)

@Component
class GQLInputRunInfoInput : GQLInputType<RunInfoInput> {

    override fun createInputType(): GraphQLInputType = GraphQLInputObjectType.newInputObject()
        .name(RunInfoInput::class.java.simpleName)
        .description("Input for some run info")
        .fields(GraphQLBeanConverter.asInputFields(RunInfoInput::class))
        .build()

    override fun convert(argument: Any?): RunInfoInput =
        argument.asJson().parse()

    override fun getTypeRef() = GraphQLTypeReference(RunInfoInput::class.java.simpleName)

}
