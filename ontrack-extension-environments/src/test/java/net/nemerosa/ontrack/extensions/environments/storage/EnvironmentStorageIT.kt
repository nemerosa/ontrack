package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.EnvironmentTestFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnvironmentStorageIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentStorage: EnvironmentStorage

    @Test
    fun `Storing and retrieving an environment`() {
        val env = EnvironmentTestFixtures.testEnvironment()
        val saved = environmentStorage.save(env)
        val byId = environmentStorage.getById(saved.id)
        assertEquals(saved, byId)
        val byName = environmentStorage.findByName(env.name)
        assertEquals(saved, byName)
    }

    @Test
    fun `Getting a list of environments`() {
        val env = EnvironmentTestFixtures.testEnvironment()
        val saved = environmentStorage.save(env)
        val list = environmentStorage.findEnvironments()
        assertNotNull(
            list.find { it.name == env.name },
            "Environment found"
        )
    }

}