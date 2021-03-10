package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubProjectConfigurationPropertyMutationProviderIT : AbstractGitHubTestSupport() {

    @Test
    fun `Setting a GitHub configuration on a project identified by ID without any issue service`() {
        asAdmin {
            project {
                val cfg = gitHubConfig()
                run("""
                    mutation {
                        setProjectGitHubConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            repository: "nemerosa/test"
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
                    assertNoUserError(data, "setProjectGitHubConfigurationPropertyById")
                    assertEquals(id(),
                        data.path("setProjectGitHubConfigurationPropertyById").path("project").path("id").asInt())
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

    @Test
    fun `Setting a GitHub configuration on a project identified by ID with indexation`() {
        asAdmin {
            project {
                val cfg = gitHubConfig()
                run("""
                    mutation {
                        setProjectGitHubConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            repository: "nemerosa/test",
                            indexationInterval: 30
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
                    assertNoUserError(data, "setProjectGitHubConfigurationPropertyById")
                    assertEquals(id(),
                        data.path("setProjectGitHubConfigurationPropertyById").path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitHubProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("nemerosa/test", property.repository)
                        assertEquals(30, property.indexationInterval)
                        assertEquals(null, property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

    @Test
    fun `Setting a GitHub configuration on a project identified by ID with an issue service`() {
        asAdmin {
            project {
                val cfg = gitHubConfig()
                run("""
                    mutation {
                        setProjectGitHubConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            repository: "nemerosa/test",
                            issueServiceConfigurationIdentifier: "github"
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
                    assertNoUserError(data, "setProjectGitHubConfigurationPropertyById")
                    assertEquals(id(),
                        data.path("setProjectGitHubConfigurationPropertyById").path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitHubProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("nemerosa/test", property.repository)
                        assertEquals(0, property.indexationInterval)
                        assertEquals("github", property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

}