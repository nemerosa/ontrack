package net.nemerosa.ontrack.graphql

import graphql.ErrorType
import graphql.ExceptionWhileDataFetching
import graphql.ExecutionResult
import graphql.GraphQL
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration

import static org.junit.Assert.fail

@ImportAutoConfiguration(HealthIndicatorAutoConfiguration)
abstract class AbstractQLITSupport extends AbstractServiceTestSupport {

    @Autowired
    private GraphqlSchemaService schemaService

    def run(String query) {
        def result = GraphQL.newGraphQL(schemaService.schema).build().execute(query)
        def error = getException(result)
        if (error != null) {
            throw error
        } else if (result.errors && !result.errors.empty) {
            fail result.errors*.message.join('\n')
        } else if (result.data) {
            return result.data
        } else {
            fail "No data was returned and no error was thrown."
        }
    }

    private static Throwable getException(ExecutionResult result) {
        def fetchingError = result.errors.find { it.errorType == ErrorType.DataFetchingException }
        if (fetchingError != null && fetchingError instanceof ExceptionWhileDataFetching) {
            return fetchingError.exception
        } else {
            return null
        }
    }


}

