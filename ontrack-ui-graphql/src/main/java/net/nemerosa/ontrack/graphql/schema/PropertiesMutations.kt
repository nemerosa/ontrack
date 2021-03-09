package net.nemerosa.ontrack.graphql.schema

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.graphql.support.getMutationInputField
import net.nemerosa.ontrack.graphql.support.getRequiredMutationInputField
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Mutations for the edition of propertie.
 */
@Component
class PropertiesMutations(
    private val structureService: StructureService,
    private val propertyService: PropertyService
) : TypedMutationProvider() {

    private val genericMutations: List<Mutation> = ProjectEntityType.values().flatMap { projectEntityType ->
        listOf(
            createGenericMutationById(projectEntityType)
            // TODO createGenericMutationByName(projectEntityType)
        )
    }

    override val mutations: List<Mutation> = genericMutations

    private fun createGenericMutationById(type: ProjectEntityType) = object : Mutation {

        override val name: String = "set${type.typeName}Property"

        override val description: String = "Sets a property on a ${type.displayName} identified by ID"

        override val inputFields: List<GraphQLInputObjectField> = listOf(
            id(type),
            propertyType(),
            propertyValue()
        )

        override val outputFields: List<GraphQLFieldDefinition> = listOf(
            projectEntityTypeField(type)
        )

        override fun fetch(env: DataFetchingEnvironment): Any {
            val id: Int = getRequiredMutationInputField(env, ARG_ID)
            val propertyName: String = getRequiredMutationInputField(env, ARG_PROPERTY_TYPE)
            val propertyValue: JsonNode? = getMutationInputField<Any>(env, ARG_PROPERTY_VALUE)?.asJson()
            // Loads the entity
            val entity: ProjectEntity = type.getEntityFn(structureService).apply(ID.of(id))
            // Edition
            val result = editProperty(entity, propertyName, propertyValue)
            // Result
            return mapOf(
                type.varName to result
            )
        }
    }

    private fun editProperty(entity: ProjectEntity, propertyName: String, propertyValue: JsonNode?): ProjectEntity {
        // Property deletion
        if (propertyValue == null) {
            propertyService.deleteProperty(entity, propertyName)
        }
        // Property edition
        else {
            propertyService.editProperty(entity, propertyName, propertyValue)
        }
        // OK
        return entity
    }

    private fun createGenericMutationByName(type: ProjectEntityType): Mutation {
        TODO()
    }

    private fun id(type: ProjectEntityType): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(ARG_ID)
        .description("ID of the ${type.displayName}")
        .type(GraphQLNonNull(GraphQLInt))
        .build()

    private fun propertyType(): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(ARG_PROPERTY_TYPE)
        .description("FQCN of the property to set")
        .type(GraphQLNonNull(GraphQLString))
        .build()

    private fun propertyValue(): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(ARG_PROPERTY_VALUE)
        .description("JSON for the property value or null to delete the property")
        .type(GQLScalarJSON.INSTANCE)
        .build()

    private fun projectEntityTypeField(type: ProjectEntityType): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name(type.varName)
            .description("${type.displayName.capitalize()} updated")
            .type(GraphQLTypeReference(type.typeName))
            .build()

    companion object {
        const val ARG_ID = "id"
        const val ARG_PROPERTY_TYPE = "property"
        const val ARG_PROPERTY_VALUE = "value"
    }
}