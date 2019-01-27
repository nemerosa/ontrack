package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition

/**
 * Contributes some fields to any arbitrary class.
 */
interface GQLFieldContributor {

    /**
     * List of field contributions
     *
     * @param type Class
     */
    fun getFields(type: Class<*>): List<GraphQLFieldDefinition>

}

/**
 * Grouped contribution
 */
fun Class<*>.graphQLFieldContributions(fieldContributors: List<GQLFieldContributor>) =
        fieldContributors.flatMap {
            it.getFields(this)
        }
