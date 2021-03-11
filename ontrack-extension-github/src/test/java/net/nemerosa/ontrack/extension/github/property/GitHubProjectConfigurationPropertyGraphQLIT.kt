package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubProjectConfigurationPropertyGraphQLIT : AbstractGitHubTestSupport() {

    @Test
    fun `Setting a GitHub configuration on a project identified by name with generic mutation`() {
        asAdmin {
            val cfg = gitHubConfig()
            project {
                run("""
                    mutation {
                        setProjectProperty(input: {
                            project: "$name",
                            property: "net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType",
                            value: {
                                configuration: "${cfg.name}",
                                repository: "nemerosa/test"
                            }
                        }) {
                            project {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "setProjectProperty")
                    assertEquals(id(),
                        data.path("setProjectProperty").path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitHubProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("nemerosa/test", property.repository)
                        assertEquals(0, property.indexationInterval)
                        assertEquals(null, property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

}