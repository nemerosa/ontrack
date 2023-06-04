package net.nemerosa.ontrack.graphql.support

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLNonNull

/**
 * Adjust a type so that it becomes nullable or not according to the value
 * of [nullable].
 */
fun nullableInputType(type: GraphQLInputType, nullable: Boolean): GraphQLInputType =
        if (nullable) {
            type
        } else {
            GraphQLNonNull(type)
        }
