package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.graphql.service.GraphQLService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.fail

abstract class AbstractQLITSupport extends AbstractServiceTestSupport {

    @Autowired
    private GraphqlSchemaService schemaService

    @Autowired
    private GraphQLService graphQLService

    def run(String query) {
        def result = graphQLService.execute(
                schemaService.schema,
                query,
                [:],
                null,
                false
        )
        if (result.errors && !result.errors.empty) {
            fail result.errors*.message.join('\n')
        } else if (result.data) {
            return result.data
        } else {
            fail "No data was returned and no error was thrown."
        }
    }

}
