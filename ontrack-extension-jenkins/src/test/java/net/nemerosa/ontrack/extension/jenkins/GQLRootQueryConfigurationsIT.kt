package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryConfigurationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var jenkinsTestSupport: JenkinsTestSupport

    @Test
    fun `Getting Jenkins configurations using the generic API`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                     {
                        configurations(configurationType: "jenkins") {
                            name
                            data
                        }
                     }
                """
            ) { data ->
                val node = data.path("configurations").find { it.getRequiredTextField("name") == config.name }
                assertNotNull(node, "Config found") {
                    val nodeData = it.path("data")
                    assertEquals(config.url, nodeData.getRequiredTextField("url"))
                    assertEquals(config.user, nodeData.getRequiredTextField("user"))
                    assertEquals(null, nodeData.getTextField("password"))
                }
            }
        }
    }

}