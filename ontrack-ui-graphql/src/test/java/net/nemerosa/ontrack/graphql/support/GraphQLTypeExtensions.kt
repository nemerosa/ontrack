package net.nemerosa.ontrack.graphql.support

import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLType

fun typeName(type: GraphQLType): String = if (type is GraphQLNamedType) {
    type.name
} else {
    type.toString()
}
