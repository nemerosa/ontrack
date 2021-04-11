package net.nemerosa.ontrack.graphql.schema.settings

import graphql.schema.GraphQLObjectType

/**
 * This provider allows a class of settings to contribute to the GrapphQL schema.
 *
 * @param T Type of the settings
 */
interface SettingsQueryProvider<T> {

    /**
     * Unique ID for those settings, suitable for a GraphQL field name
     */
    val id: String

    /**
     * Description for those settings
     */
    val description: String

    /**
     * GraphQL type for those settings
     */
    fun createType(): GraphQLObjectType

    /**
     * Loads the settings
     */
    fun getSettings(): T

}