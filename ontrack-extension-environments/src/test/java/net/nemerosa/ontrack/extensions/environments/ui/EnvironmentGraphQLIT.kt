package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extensions.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Test
    fun `Getting an environment by name`() {
        asAdmin {
            environmentTestSupport.withEnvironment { env ->
                run(
                    """
                {
                    environmentByName(name: "${env.name}") {
                        id
                    }
                }
            """
                ) { data ->
                    val id = data.path("environmentByName").path("id").asText()
                    assertEquals(env.id, id)
                }
            }
        }
    }

}