package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitLabProjectConfigurationPropertyMutationProviderIT : AbstractGitLabTestSupport() {

    @Test
    fun `Setting a GitLab configuration on a project identified by ID without any issue service`() {
        asAdmin {
            project {
                val cfg = gitLabConfig()
                run(
                    """
                    mutation {
                        setProjectGitLabConfigurationPropertyById(input: {
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
                """
                ).let { data ->
                    val node = assertNoUserError(data, "setProjectGitLabConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitLabProjectConfigurationPropertyType::class.java)) { property ->
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
    fun `Setting a GitLab configuration on a project identified by name without any issue service`() {
        asAdmin {
            project {
                val cfg = gitLabConfig()
                run(
                    """
                    mutation {
                        setProjectGitLabConfigurationProperty(input: {
                            project: "$name",
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
                """
                ).let { data ->
                    val node = assertNoUserError(data, "setProjectGitLabConfigurationProperty")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitLabProjectConfigurationPropertyType::class.java)) { property ->
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
    fun `Setting a GitLab configuration on a project identified by ID with indexation`() {
        asAdmin {
            project {
                val cfg = gitLabConfig()
                run(
                    """
                    mutation {
                        setProjectGitLabConfigurationPropertyById(input: {
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
                """
                ).let { data ->
                    val node = assertNoUserError(data, "setProjectGitLabConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitLabProjectConfigurationPropertyType::class.java)) { property ->
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
    fun `Setting a GitLab configuration on a project identified by ID with an issue service`() {
        asAdmin {
            project {
                val cfg = gitLabConfig()
                run(
                    """
                    mutation {
                        setProjectGitLabConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            repository: "nemerosa/test",
                            issueServiceConfigurationIdentifier: "gitLab"
                        }) {
                            project {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
                ).let { data ->
                    val node = assertNoUserError(data, "setProjectGitLabConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, GitLabProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("nemerosa/test", property.repository)
                        assertEquals(0, property.indexationInterval)
                        assertEquals("gitLab", property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

}