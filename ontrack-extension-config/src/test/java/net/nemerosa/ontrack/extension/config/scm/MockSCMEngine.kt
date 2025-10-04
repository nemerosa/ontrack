package net.nemerosa.ontrack.extension.config.scm

import org.springframework.stereotype.Component

@Component
class MockSCMEngine : SCMEngine {
    override val name: String = "mock"

    override fun getProjectName(env: Map<String, String>): String? {
        return env["MOCK_SCM_NAME"]
    }
}