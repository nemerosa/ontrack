package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.EnvironmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvironmentTestSupport {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    fun withEnvironment(
        env: Environment = EnvironmentTestFixtures.testEnvironment(),
        code: (env: Environment) -> Unit
    ) {
        environmentService.save(env)
        code(env)
    }

}