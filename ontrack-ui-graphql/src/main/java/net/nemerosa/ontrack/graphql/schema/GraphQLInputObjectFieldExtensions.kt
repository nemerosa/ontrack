package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.getPropertyDescription
import net.nemerosa.ontrack.graphql.support.nullableInputType
import kotlin.reflect.KProperty

fun requiredStringInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLString))
    .build()

fun optionalStringInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLString)
    .build()

fun optionalIntInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLInt)
    .build()


fun requiredFloatInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLFloat))
    .build()

fun requiredIntInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLInt))
    .build()

fun optionalBooleanInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLBoolean)
    .build()

fun requiredRefInputField(
    name: String,
    description: String,
    typeRef: GraphQLTypeReference,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(typeRef))
    .build()


fun optionalRefInputField(
    name: String,
    description: String,
    typeRef: GraphQLTypeReference,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(typeRef)
    .build()

fun optionalStringListInputField(
    name: String,
    description: String,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLList(GraphQLNonNull(GraphQLString)))
    .build()

// ================================================================================================
// Typed fields
// ================================================================================================

fun stringInputField(property: KProperty<String?>) : GraphQLInputObjectField =
    inputField(property, GraphQLString)

fun intInputField(property: KProperty<Int?>) : GraphQLInputObjectField =
    inputField(property, GraphQLInt)

fun stringListInputField(property: KProperty<List<String>?>) : GraphQLInputObjectField =
    inputField(property, GraphQLList(GraphQLNonNull(GraphQLString)))

fun inputField(property: KProperty<*>, type: GraphQLInputType): GraphQLInputObjectField {
    val description = getPropertyDescription(property)
    return GraphQLInputObjectField.newInputObjectField()
        .name(property.name)
        .description(description)
        .type(nullableInputType(type, property.returnType.isMarkedNullable))
        .build()
}