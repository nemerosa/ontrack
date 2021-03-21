package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.model.structure.ProjectEntity
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
     * Given an entity and the input of the mutation, returns the value of the property to set.
     */
    fun readInput(entity: ProjectEntity, input: MutationInput): T

}