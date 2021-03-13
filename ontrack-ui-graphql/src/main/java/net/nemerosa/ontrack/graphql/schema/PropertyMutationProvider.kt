package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.model.structure.PropertyType
import kotlin.reflect.KClass

interface PropertyMutationProvider<T> {

    /**
     * Supported property type
     */
    val propertyType: KClass<out PropertyType<T>>

    /**
     * Name to use in the mutation complete name.
     *
     * For example, if this property returns `GitCommit`, a mutation for this
     * property and for a build identified by ID will be `setBuildGitCommitPropertyById`.
     */
    val mutationNameFragment: String

    /**
     * List of input fields for the mutation.
     */
    val inputFields: List<GraphQLInputObjectField>

    /**
     * Given an
     */
    fun readInput(input: MutationInput): T

}