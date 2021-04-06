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

fun stringInputField(property: KProperty<String?>, description: String? = null, nullable: Boolean? = null) : GraphQLInputObjectField =
    inputField(property, GraphQLString, description, nullable)

fun intInputField(property: KProperty<Int?>, description: String? = null, nullable: Boolean? = null) : GraphQLInputObjectField =
    inputField(property, GraphQLInt, description, nullable)

fun stringListInputField(property: KProperty<List<String>?>) : GraphQLInputObjectField =
    inputField(property, GraphQLList(GraphQLNonNull(GraphQLString)))

fun inputField(
    property: KProperty<*>,
    type: GraphQLInputType,
    description: String? = null,
    nullable: Boolean? = null
): GraphQLInputObjectField {
    val actualDescription = description ?: getPropertyDescription(property)
    return GraphQLInputObjectField.newInputObjectField()
        .name(property.name)
        .description(actualDescription)
        .type(nullableInputType(type, nullable = nullable ?: property.returnType.isMarkedNullable))
        .build()
}