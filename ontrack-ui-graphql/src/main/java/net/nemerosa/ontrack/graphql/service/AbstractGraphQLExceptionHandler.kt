package net.nemerosa.ontrack.graphql.service

import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import kotlin.reflect.KClass

abstract class AbstractGraphQLExceptionHandler(
        private val exceptionClass: KClass<out Exception>
) : GraphQLExceptionHandler {
    override fun handle(error: GraphQLError) {
        if (error is ExceptionWhileDataFetching && exceptionClass.isInstance(error.exception)) {
            throw error.exception
        }
    }
}