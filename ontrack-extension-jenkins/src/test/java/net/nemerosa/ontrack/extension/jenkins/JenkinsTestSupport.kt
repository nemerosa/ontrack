package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.stereotype.Component

@Component
class JenkinsTestSupport(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
) : AbstractDSLTestSupport() {

    fun withConfig(
        name: String = TestUtils.uid("jc-"),
        url: String = "https://jenkins",
        user: String = "username",
        password: String = "password",
        code: (config: JenkinsConfiguration) -> Unit,
    ) {
        asAdmin {
            withDisabledConfigurationTest {
                val config = JenkinsConfiguration(
                    name = name,
                    url = url,
                    user = user,
                    password = password,
                )
                jenkinsConfigurationService.newConfiguration(config)
                code(config)
            }
        }
    }

}