package net.nemerosa.ontrack.graphql.schema

import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.graphql.support.getMutationInputField
import net.nemerosa.ontrack.graphql.support.getRequiredMutationInputField
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 * Utility class used to implement specific validation stamp mutations
 *
 * @param <C> Type of the data type configuration
 */
@Component
abstract class AbstractTypedValidationStampMutationProvider<C>(
    private val structureService: StructureService
) : TypedMutationProvider() {

    override val mutations: List<Mutation>
        get() = listOf(
                TypedValidationStampMutation()
        )

    private inner class TypedValidationStampMutation : Mutation {

        override val name: String = "setup${mutationFragmentName}ValidationStamp"
        override val description: String = "Creates or updates a $mutationFragmentName validation stamp"

        override fun inputFields(dictionary: MutableSet<GraphQLType>): List<GraphQLInputObjectField> = listOf(
            requiredStringInputField("project", "Name of the project"),
            requiredStringInputField("branch", "Name of the branch"),
            requiredStringInputField("validation", "Name of the validation stamp"),
            optionalStringInputField("description", "Description of the validation stamp")
        ) + dataTypeInputFields

        override val outputFields: List<GraphQLFieldDefinition> = listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("validationStamp")
                .description("Created or updated validation stamp")
                .type(GraphQLTypeReference(GQLTypeValidationStamp.VALIDATION_STAMP))
                .build()
        )

        override fun fetch(env: DataFetchingEnvironment): Map<String, ValidationStamp> {
            val project: String = getRequiredMutationInputField(env, "project")
            val branch: String = getRequiredMutationInputField(env, "branch")
            val validation: String = getRequiredMutationInputField(env, "validation")
            val description: String? = getMutationInputField(env, "description")
            val existing = structureService.findValidationStampByName(project, branch, validation).getOrNull()
            val dataTypeConfig: ValidationDataTypeConfig<C> = readInput(EnvMutationInput(env))
            val vs = if (existing != null) {
                // Updates the validation if need be
                updateValidationStamp(existing, validation, description, dataTypeConfig)
            } else {
                createValidationStamp(project, branch, validation, description, dataTypeConfig)
            }
            return mapOf("validationStamp" to vs)
        }
    }

    private fun updateValidationStamp(
        existing: ValidationStamp,
        validation: String,
        description: String?,
        dataTypeConfig: ValidationDataTypeConfig<C>
    ): ValidationStamp {
        val updated = existing.update(
            NameDescription.nd(validation, description)
        ).withDataType(dataTypeConfig)
        // Saves in repository
        structureService.saveValidationStamp(updated)
        // As resource
        return updated
    }

    private fun createValidationStamp(
        project: String,
        branch: String,
        validation: String,
        description: String?,
        dataTypeConfig: ValidationDataTypeConfig<C>
    ): ValidationStamp {
        val parentBranch =
            structureService.findBranchByName(project, branch).getOrNull()
                ?: throw BranchNotFoundException(project, branch)
        val validationStamp = ValidationStamp.of(
            parentBranch,
            NameDescription.nd(validation, description)
        ).withDataType(dataTypeConfig)
        // Saves it into the repository
        return structureService.newValidationStamp(validationStamp)
    }

    /**
     * Name of the validation stamp type to include into the mutation name.
     *
     * For example, if returning "CHML", the complete mutation name will
     * be `setupCHMLValidationStamp`.
     */
    abstract val mutationFragmentName: String

    /**
     * List of fields to add into the input
     */
    abstract val dataTypeInputFields: List<GraphQLInputObjectField>

    /**
     * Reads the input of the data type config from the environment
     */
    abstract fun readInput(input: MutationInput): ValidationDataTypeConfig<C>
}