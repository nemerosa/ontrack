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

    /**
     * Getter only
     */
    operator fun get(name: String): GraphQLObjectType? = index[name]

    /**
     * List of types in the cache
     */
    val types: Collection<GraphQLObjectType> get() = index.values

}