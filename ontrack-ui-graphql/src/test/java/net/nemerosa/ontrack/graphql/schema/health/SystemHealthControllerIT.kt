package net.nemerosa.ontrack.graphql.schema.health

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.support.ConnectorGlobalStatusService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemHealthControllerIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var connectorGlobalStatusService: ConnectorGlobalStatusService

    @Test
    @AsAdminTest
    fun `Getting the health of the system`() {
        connectorGlobalStatusService.collect()
        run(
            """{
                systemHealth {
                    health
                    connectors {
                        statuses {
                            status {
                                description {
                                    connector {
                                        type
                                        name
                                    }
                                    connection
                                }
                                type
                                error
                            }
                            time
                        }
                        count
                        upCount
                        downCount
                        status
                        percent
                    }
                }
            }"""
        ) { data ->
            val connectorGlobalStatus = data.path("systemHealth")
                .path("connectors")

            val statuses = connectorGlobalStatus.path("statuses")
            val testStatus = statuses.single {
                it.path("status")
                    .path("description")
                    .path("connector")
                    .path("type")
                    .asText() == "test"
            }
            assertTrue(testStatus.path("time").asText().isNotBlank(), "Time is set")
            assertEquals("UP", testStatus.path("status").path("type").asText())
        }
    }

}