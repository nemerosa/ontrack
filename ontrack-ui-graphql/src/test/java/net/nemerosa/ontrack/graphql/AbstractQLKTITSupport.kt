package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.graphql.service.GraphQLService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.fail

abstract class AbstractQLKTITSupport : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var schemaService: GraphqlSchemaService

    @Autowired
    private lateinit var graphQLService: GraphQLService

    fun run(query: String): JsonNode {
        val result = graphQLService.execute(
                schemaService.schema,
                query,
                mapOf(),
                null,
                true
        )
        if (result.data != null) {
            return JsonUtils.format(result.data)
        } else {
            fail("No data was returned and no error was thrown.")
        }
    }

}
