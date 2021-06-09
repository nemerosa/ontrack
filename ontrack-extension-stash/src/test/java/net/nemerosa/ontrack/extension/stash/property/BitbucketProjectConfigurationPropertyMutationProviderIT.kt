package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.stash.AbstractBitbucketTestSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BitbucketProjectConfigurationPropertyMutationProviderIT : AbstractBitbucketTestSupport() {

    @Test
    fun `Setting a Bitbucket configuration on a project identified by ID without any issue service`() {
        asAdmin {
            project {
                val cfg = bitbucketConfig()
                run(
                    """
                    mutation {
                        setProjectBitbucketConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            project: "NEM",
                            repository: "test"
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
                    val node = assertNoUserError(data, "setProjectBitbucketConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, StashProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("NEM", property.project)
                        assertEquals("test", property.repository)
                        assertEquals(0, property.indexationInterval)
                        assertEquals(null, property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

    @Test
    fun `Setting a Bitbucket configuration on a project identified by ID with indexation`() {
        asAdmin {
            project {
                val cfg = bitbucketConfig()
                run(
                    """
                    mutation {
                        setProjectBitbucketConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            project: "NEM",
                            repository: "test",
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
                    val node = assertNoUserError(data, "setProjectBitbucketConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, StashProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("NEM", property.project)
                        assertEquals("test", property.repository)
                        assertEquals(30, property.indexationInterval)
                        assertEquals(null, property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

    @Test
    fun `Setting a Bitbucket configuration on a project identified by ID with an issue service`() {
        asAdmin {
            project {
                val cfg = bitbucketConfig()
                run(
                    """
                    mutation {
                        setProjectBitbucketConfigurationPropertyById(input: {
                            id: $id,
                            configuration: "${cfg.name}",
                            project: "NEM",
                            repository: "test",
                            issueServiceConfigurationIdentifier: "jira//Name"
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
                    val node = assertNoUserError(data, "setProjectBitbucketConfigurationPropertyById")
                    assertEquals(id(), node.path("project").path("id").asInt())
                    assertNotNull(getProperty(this, StashProjectConfigurationPropertyType::class.java)) { property ->
                        assertEquals(cfg.name, property.configuration.name)
                        assertEquals("NEM", property.project)
                        assertEquals("test", property.repository)
                        assertEquals(0, property.indexationInterval)
                        assertEquals("jira//Name", property.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

}