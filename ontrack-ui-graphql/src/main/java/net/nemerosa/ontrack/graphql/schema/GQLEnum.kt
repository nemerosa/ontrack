package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLTypeReference

/**
 * Creation of an enum type
 */
interface GQLEnum {

    fun getTypeRef(): GraphQLTypeReference

    fun createEnum(): GraphQLEnumType
}