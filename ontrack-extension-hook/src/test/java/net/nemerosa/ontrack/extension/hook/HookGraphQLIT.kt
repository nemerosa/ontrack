package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HookGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var hookTestSupport: HookTestSupport

    @Test
    fun `Accessing the hook records using GraphQL`() {
        asAdmin {
            // Calling the test hook
            hookTestSupport.testHook(
                    body = "Testing GraphQL",
                    parameters = mapOf(
                            "test" to "xxx",
                    )
            )
            // Getting the last record for this hook
            run("""
                {
                    hookRecords(hook: "test", size: 1) {
                        pageItems {
                            id
                            hook
                            request {
                                body
                                parameters {
                                    name
                                    value
                                }
                            }
                            startTime
                            state
                            message
                            exception
                            endTime
                            response {
                                type
                                info
                            }
                        }
                    }
                }
            """) { data ->
                val record = data.path("hookRecords").path("pageItems").firstOrNull()
                assertNotNull(record, "At least one record") {
                    assertEquals("test", it.getRequiredTextField("hook"))
                    it.path("request").let { request ->
                        assertEquals("Testing GraphQL", request.getRequiredTextField("body"))
                        request.path("parameters").let { parameters ->
                            assertEquals(1, parameters.size())
                            val parameter = parameters.path(0)
                            assertEquals("test", parameter.getRequiredTextField("name"))
                            assertEquals("xxx", parameter.getRequiredTextField("value"))
                        }
                    }
                    assertEquals("SUCCESS", it.getRequiredTextField("state"))
                    assertEquals("PROCESSED", it.path("response").path("type").asText())
                }
            }
        }
    }

}