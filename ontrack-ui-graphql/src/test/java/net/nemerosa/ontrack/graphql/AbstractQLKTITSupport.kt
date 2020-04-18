package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import graphql.GraphQL
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService
import net.nemerosa.ontrack.graphql.support.exception
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.fail

abstract class AbstractQLKTITSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var schemaService: GraphqlSchemaService

    fun run(query: String, variables: Map<String, *> = emptyMap<String, Any>()): JsonNode {
        // Task to run
        val code = { internalRun(query, variables) }
        // Making sure we're at least authenticated
        return if (securityService.isLogged) {
            code()
        } else {
            asUser().call(code)
        }
    }

    private fun internalRun(query: String, variables: Map<String, *> = emptyMap<String, Any>()): JsonNode {
        val result = GraphQL
                .newGraphQL(schemaService.schema)
                .build()
                .execute {
                    it.query(query).variables(variables)
                }
        val error = result.exception
        if (error != null) {
            throw error
        } else if (result.errors != null && !result.errors.isEmpty()) {
            fail(result.errors.joinToString("\n") { it.message })
        } else {
            val data: Any? = result.getData()
            if (data != null) {
                return JsonUtils.format(data)
            } else {
                fail("No data was returned and no error was thrown.")
            }
        }
    }

}
