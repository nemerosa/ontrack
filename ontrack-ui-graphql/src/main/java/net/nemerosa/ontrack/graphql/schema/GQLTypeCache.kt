package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType

class GQLTypeCache {

    /**
     * Index of types per name
     */
    private val index = mutableMapOf<String, GraphQLObjectType>()

    /**
     * Gets or creates a type
     */
    fun getOrCreate(name: String, creator: () -> GraphQLObjectType): GraphQLObjectType {
        return index.getOrPut(
                name,
                creator
        )
    }

}