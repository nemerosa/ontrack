package net.nemerosa.ontrack.graphql.service

import graphql.GraphQLError

interface GraphQLExceptionHandler {
    fun handle(error: GraphQLError)
}