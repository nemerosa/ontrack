package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull

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
