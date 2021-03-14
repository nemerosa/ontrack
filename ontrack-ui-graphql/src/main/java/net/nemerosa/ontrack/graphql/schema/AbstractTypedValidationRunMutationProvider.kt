package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.getMutationInputField
import net.nemerosa.ontrack.graphql.support.getRequiredMutationInputField
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.structure.*
import kotlin.reflect.KClass

/**
 * Defines a mutation for a given validation data type.
 *
 * @param T Type of validation data
 */
abstract class AbstractTypedValidationRunMutationProvider<T>(
    private val structureService: StructureService,
    private val validationRunStatusService: ValidationRunStatusService,
) : MutationProvider {

    override val mutations: List<Mutation>
        get() = listOf(
            mutationById,
            mutationByName
        )

    private val mutationById: Mutation
        get() = object : Mutation {

            override val name: String = "validateBuildByIdWith${mutationFragmentName}"
            override val description: String = "Validates a build identified by id with $mutationFragmentName data"

            override val inputFields: List<GraphQLInputObjectField> = commonInputFields + dataInputFields + listOf(
                requiredIntInputField("id", "ID of the build"),
            )

            override val outputFields: List<GraphQLFieldDefinition> = listOf(
                returnField()
            )

            override fun fetch(env: DataFetchingEnvironment): Any {
                // Loads the build
                val id = getRequiredMutationInputField<Int>(env, "id")
                val build = structureService.getBuild(ID.of(id))
                // Validation
                val run = validate(build, env)
                // Result
                return mapOf(
                    "validationRun" to run
                )
            }

        }

    private val mutationByName: Mutation
        get() = object : Mutation {

            override val name: String = "validateBuildWith${mutationFragmentName}"
            override val description: String = "Validates a build identified by name with $mutationFragmentName data"

            override val inputFields: List<GraphQLInputObjectField> = commonInputFields + dataInputFields + listOf(
                requiredStringInputField("project", "Name of the project"),
                requiredStringInputField("branch", "Name of the branch"),
                requiredStringInputField("build", "Name of the build"),
            )

            override val outputFields: List<GraphQLFieldDefinition> = listOf(
                returnField()
            )

            override fun fetch(env: DataFetchingEnvironment): Any {
                // Loads the build
                val project = getRequiredMutationInputField<String>(env, "project")
                val branch = getRequiredMutationInputField<String>(env, "branch")
                val name = getRequiredMutationInputField<String>(env, "build")
                val build = structureService.findBuildByName(project, branch, name)
                    .getOrNull()
                    ?: throw BuildNotFoundException(project, branch, name)
                // Validation
                val run = validate(build, env)
                // Result
                return mapOf(
                    "validationRun" to run
                )
            }

        }

    private fun validate(build: Build, env: DataFetchingEnvironment): ValidationRun {
        val validation = getRequiredMutationInputField<String>(env, "validation")
        val input = EnvMutationInput(env)
        val data = readInput(input)
        return structureService.newValidationRun(
            build = build,
            validationRunRequest = ValidationRunRequest(
                validationStampName = validation,
                validationRunStatusId = getMutationInputField<String>(env, "status")?.let {
                    validationRunStatusService.getValidationRunStatus(it)
                },
                description = getMutationInputField<String>(env, "description"),
                dataTypeId = dataType.java.name,
                data = data,
            )
        )
        // TODO Run info
    }

    /**
     * Data type
     */
    abstract val dataType: KClass<out ValidationDataType<*, T>>

    /**
     * Reads the specific data from the mutation input
     */
    abstract fun readInput(input: EnvMutationInput): T

    private val commonInputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("validation", "Name of the validation stamp"),
        optionalStringInputField("status", "Status of the validation run"),
        optionalStringInputField("description", "Description of the validation run"),
        // TODO Run info input
    )

    private fun returnField(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("validationRun")
        .description("Created validation run")
        .type(ValidationRun::class.toTypeRef())
        .build()

    /**
     * Name of the validation stamp type to include into the mutation name.
     *
     * For example, if returning "CHML", the complete mutation name will
     * be `validateBuildWithCHML`.
     */
    abstract val mutationFragmentName: String

    /**
     * Additional fields in the `input`.
     */
    abstract val dataInputFields: List<GraphQLInputObjectField>
}
