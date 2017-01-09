package net.nemerosa.ontrack.graphql

import graphql.GraphQL
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.fail

abstract class AbstractQLITSupport extends AbstractServiceTestSupport {

    @Autowired
    private GraphqlSchemaService schemaService

    def run(String query) {
        def result = new GraphQL(schemaService.schema).execute(query)
        if (result.errors && !result.errors.empty) {
            fail result.errors*.message.join('\n')
        } else if (result.data) {
            return result.data
        } else {
            fail "No data was returned and no error was thrown."
        }
    }

}
