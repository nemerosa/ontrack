package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLType

/**
 * Defines a mutation.
 *
 * All mutations must be defined on the same model:
 *
 * * a unique name starting by a verb
 * * a unique argument `input` whose type is unique to the mutation
 * * a return type whose type is unique to the mutation
 */
interface Mutation {

    /**
     * Name of this mutation
     */
    val name: String

    /**
     * Description for this mutation
     */
    val description: String

    /**
     * Deprecation reason (if any)
     */
    val deprecation: String? get() = null

    /**
     * Fields attached to the input of the mutation
     */
    fun inputFields(dictionary: MutableSet<GraphQLType>): List<GraphQLInputObjectField>

    /**
     * Fields attached to the output of the mutation
     */
    val outputFields: List<GraphQLFieldDefinition>

    /**
     * Data fetcher for this mutation
     */
    fun fetch(env: DataFetchingEnvironment): Any

}