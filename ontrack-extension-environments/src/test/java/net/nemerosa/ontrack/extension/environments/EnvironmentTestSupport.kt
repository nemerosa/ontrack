package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvironmentTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentService: EnvironmentService

    fun withEnvironment(
        env: Environment = EnvironmentTestFixtures.testEnvironment(),
        code: (env: Environment) -> Unit
    ): Environment {
        asAdmin {
            environmentService.save(env)
        }
        code(env)
        return env
    }

}