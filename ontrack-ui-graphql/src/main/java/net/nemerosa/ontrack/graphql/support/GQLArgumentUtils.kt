package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
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
    description: String,
    nullable: Boolean = true,
): GraphQLArgument = GraphQLArgument.newArgument()
    .name(name)
    .description(description)
    .type(nullableInputType(GraphQLString, nullable))
    .build()

/**
 * Creates a `Int` GraphQL argument.
 *
 * @param name Name of the argument
 * @param description Description of the argument
 */
fun intArgument(
    name: String,
    description: String,
    nullable: Boolean = true,
): GraphQLArgument = GraphQLArgument.newArgument()
    .name(name)
    .description(description)
    .type(nullableInputType(GraphQLInt, nullable))
    .build()

/**
 * Creates a `Boolean` GraphQL argument.
 *
 * @param name Name of the argument
 * @param description Description of the argument
 */
fun booleanArgument(
    name: String,
    description: String,
    nullable: Boolean = true,
): GraphQLArgument = GraphQLArgument.newArgument()
    .name(name)
    .description(description)
    .type(nullableInputType(GraphQLBoolean, nullable))
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
