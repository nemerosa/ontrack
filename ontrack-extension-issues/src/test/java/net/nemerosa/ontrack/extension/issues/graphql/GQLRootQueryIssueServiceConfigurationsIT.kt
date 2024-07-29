package net.nemerosa.ontrack.extension.issues.graphql

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLRootQueryIssueServiceConfigurationsIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting the issue service configurations with GraphQL`() {
        asAdmin {
            run(
                """
                {
                    issueServiceConfigurations {
                        id
                        name
                        serviceId
                    }
                }
            """
            ) { data ->
                val configs = data.path("issueServiceConfigurations")
                val testConfigs = configs.filter { it.path("serviceId").asText() == "test" }
                assertEquals(1, testConfigs.size)
                val testConfig = testConfigs.first()
                assertEquals("default (Test issues)", testConfig?.path("name")?.asText())
                assertEquals("test//default", testConfig?.path("id")?.asText())
            }
        }
    }

}