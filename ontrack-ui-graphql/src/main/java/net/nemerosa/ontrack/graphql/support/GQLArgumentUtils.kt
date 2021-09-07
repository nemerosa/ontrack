package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import java.util.*

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


/**
 * Checks list of arguments
 */
fun checkArgList(environment: DataFetchingEnvironment, vararg args: String) {
    val actualArgs: Set<String> = environment.arguments.filterValues { it != null }.keys
    val expectedArgs: Set<String> = args.toSet()
    check(actualArgs == expectedArgs) {
        "Expected this list of arguments: $expectedArgs, but was: $actualArgs"
    }
}
