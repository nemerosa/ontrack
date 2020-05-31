package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLArgument

/**
 * Creates a `String` GraphQL argument.
 *
 * @param name Name of the argument
 * @param description Description of the argument
 */
fun stringArgument(
        name: String,
        description: String
): GraphQLArgument = GraphQLArgument.newArgument()
        .name(name)
        .description(description)
        .type(GraphQLString)
        .build()


/**
 * Creates a date/time GraphQL argument.
 *
 * @param name Name of the argument
 * @param description Description of the argument
 */
fun dateTimeArgument(
        name: String,
        description: String
): GraphQLArgument = GraphQLArgument.newArgument()
        .name(name)
        .description(description)
        .type(GQLScalarLocalDateTime.INSTANCE)
        .build()
