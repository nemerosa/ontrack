package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference

fun requiredStringInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLString))
    .build()

fun optionalStringInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLString)
    .build()

fun optionalIntInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLInt)
    .build()


fun requiredFloatInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLFloat))
    .build()

fun requiredIntInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLInt))
    .build()

fun optionalBooleanInputField(
    name: String,
    description: String
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(GraphQLBoolean))
    .build()

fun requiredRefInputField(
    name: String,
    description: String,
    typeRef: GraphQLTypeReference
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(GraphQLNonNull(typeRef))
    .build()


fun optionalRefInputField(
    name: String,
    description: String,
    typeRef: GraphQLTypeReference
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(typeRef)
    .build()
