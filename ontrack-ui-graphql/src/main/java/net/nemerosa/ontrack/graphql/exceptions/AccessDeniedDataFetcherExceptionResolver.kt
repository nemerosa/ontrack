package net.nemerosa.ontrack.graphql.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class AccessDeniedDataFetcherExceptionResolver : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? =
            if (ex is AccessDeniedException) {
                GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.FORBIDDEN)
                        .message(ex.message)
                        .build()
            } else {
                null
            }

}