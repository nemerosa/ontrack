package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.EnvironmentService
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentNameAlreadyExists
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnvironmentServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Test
    fun `Creating and retrieving an environment`() {
        asAdmin {
            val env = EnvironmentTestFixtures.testEnvironment()
            environmentService.save(env)
            // Getting it by ID
            val saved = environmentService.getById(env.id)
            assertEquals(env, saved)
            // Getting it by name
            val byName = environmentService.findByName(env.name)
            assertEquals(env, byName)
        }
    }

    @Test
    fun `Cannot create an environment if name already defined`() {
        asAdmin {
            val env = EnvironmentTestFixtures.testEnvironment()
            environmentService.save(env)
            // New environment
            val other = EnvironmentTestFixtures.testEnvironment(name = env.name)
            assertFailsWith<EnvironmentNameAlreadyExists> {
                environmentService.save(other)
            }
        }
    }

    @Test
    fun `Getting an ordered list of environments`() {
        asAdmin {
            val env1 = EnvironmentTestFixtures.testEnvironment().apply { environmentService.save(this) }
            val env2 = EnvironmentTestFixtures.testEnvironment().apply { environmentService.save(this) }
            val envs = environmentService.findAll()
            assertNotNull(envs.find { it.id == env1.id })
            assertNotNull(envs.find { it.id == env2.id })
        }
    }

    @Test
    fun `Deleting an environment`() {
        asAdmin {
            val env = EnvironmentTestFixtures.testEnvironment()
            environmentService.save(env)
            assertNotNull(environmentService.findByName(env.name), "Environment present")
            environmentService.delete(env)
            assertNull(environmentService.findByName(env.name), "Environment gone")
        }
    }

    @Test
    fun `Creating and retrieving an environment with tags`() {
        asAdmin {
            val env = EnvironmentTestFixtures.testEnvironment(
                tags = listOf("custom"),
            )
            environmentService.save(env)
            assertNotNull(environmentService.findByName(env.name), "Environment present") {
                assertEquals(
                    listOf("custom"),
                    it.tags
                )
            }
        }
    }

    @Test
    fun `Selecting environments based on tags`() {
        asAdmin {
            val tag1 = uid("t1-")
            val tag2 = uid("t2-")
            val env1 = EnvironmentTestFixtures.testEnvironment(
                tags = listOf(tag1, tag2)
            ).apply { environmentService.save(this) }
            val env2 = EnvironmentTestFixtures.testEnvironment(
                tags = listOf(tag1)
            ).apply { environmentService.save(this) }

            assertEquals(
                listOf(env1, env2),
                environmentService.findAll(
                    EnvironmentFilter(
                        tags = listOf(tag1)
                    )
                )
            )

            assertEquals(
                listOf(env1),
                environmentService.findAll(
                    EnvironmentFilter(
                        tags = listOf(tag1, tag2)
                    )
                )
            )

            assertEquals(
                listOf(env1),
                environmentService.findAll(
                    EnvironmentFilter(
                        tags = listOf(tag2)
                    )
                )
            )
        }
    }

    @Test
    fun `Updating the tags of an environment`() {
        asAdmin {
            val env = EnvironmentTestFixtures.testEnvironment(
                tags = listOf("custom"),
            )
            environmentService.save(env)
            val tag = uid("t-")
            environmentService.save(
                env.withTags(listOf("custom", tag))
            )
            val saved = environmentService.getById(env.id)
            val envs = environmentService.findAll(
                EnvironmentFilter(
                    tags = listOf(tag)
                )
            )

            assertEquals(
                listOf(saved),
                envs
            )
        }
    }

}