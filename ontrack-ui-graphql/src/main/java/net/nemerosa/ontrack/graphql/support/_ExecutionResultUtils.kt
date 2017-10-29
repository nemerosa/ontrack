package net.nemerosa.ontrack.graphql.support

import graphql.ErrorType
import graphql.ExceptionWhileDataFetching
import graphql.ExecutionResult

val ExecutionResult.exception: Throwable?
    get() {
        val fetchingError = errors.find { it.errorType == ErrorType.DataFetchingException }
        return if (fetchingError != null && fetchingError is ExceptionWhileDataFetching) {
            fetchingError.exception
        } else {
            null
        }
    }
