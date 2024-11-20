package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Test
    fun `Creating an environment with only required fields`() {
        asAdmin {
            val name = uid("env-")
            run(
                """
                mutation {
                    createEnvironment(input: {
                        name: "$name",
                        order: 100,
                    }) {
                        environment {
                            id
                        }
                        errors {
                            message
                        }
                    }
                }
            """
            ) { data ->
                checkGraphQLUserErrors(data, "createEnvironment") { node ->
                    val id = node.path("environment").path("id").asText()
                    val env = environmentService.getById(id)
                    assertEquals(name, env.name)
                    assertEquals(100, env.order)
                    assertEquals(null, env.description)
                    assertEquals(emptyList(), env.tags)
                }
            }
        }
    }

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