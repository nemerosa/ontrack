package net.nemerosa.ontrack.graphql.service

import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError

class GraphQLServiceException(
        errors: List<GraphQLError>
) : RuntimeException(
        // Gets the first message
        message = errors.first().message,
        // Gets the first exception
        cause = errors
                .filterIsInstance(ExceptionWhileDataFetching::class.java)
                .firstOrNull()
                ?.exception
)
