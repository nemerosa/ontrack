package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvironmentTestSupport {

    @Autowired
    private lateinit var environmentStorage: EnvironmentStorage

    fun withEnvironment(
        env: Environment = EnvironmentTestFixtures.testEnvironment(),
        code: (env: Environment) -> Unit
    ) {
        environmentStorage.save(env)
        code(env)
    }

}