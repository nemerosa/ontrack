package net.nemerosa.ontrack.extension.config.scm

import org.springframework.stereotype.Component

@Component
class MockSCMEngine : SCMEngine {
    override val name: String = "mock"
}