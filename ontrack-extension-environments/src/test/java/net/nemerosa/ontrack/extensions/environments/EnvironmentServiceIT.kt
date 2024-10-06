package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.EnvironmentService
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentNameAlreadyExists
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
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
        val env = EnvironmentTestFixtures.testEnvironment()
        environmentService.save(env)
        // Getting it by ID
        val saved = environmentService.getById(env.id)
        assertEquals(env, saved)
        // Getting it by name
        val byName = environmentService.findByName(env.name)
        assertEquals(env, byName)
    }

    @Test
    fun `Cannot create an environment if name already defined`() {
        val env = EnvironmentTestFixtures.testEnvironment()
        environmentService.save(env)
        // New environment
        val other = EnvironmentTestFixtures.testEnvironment(name = env.name)
        assertFailsWith<EnvironmentNameAlreadyExists> {
            environmentService.save(other)
        }
    }

    @Test
    fun `Getting an ordered list of environments`() {
        val env1 = EnvironmentTestFixtures.testEnvironment().apply { environmentService.save(this) }
        val env2 = EnvironmentTestFixtures.testEnvironment().apply { environmentService.save(this) }
        val envs = environmentService.findAll()
        assertNotNull(envs.find { it.id == env1.id })
        assertNotNull(envs.find { it.id == env2.id })
    }

    @Test
    fun `Deleting an environment`() {
        val env = EnvironmentTestFixtures.testEnvironment()
        environmentService.save(env)
        assertNotNull(environmentService.findByName(env.name), "Environment present")
        environmentService.delete(env)
        assertNull(environmentService.findByName(env.name), "Environment gone")
    }

}