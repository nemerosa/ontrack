package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.model.extension.ExtensionFeature
import org.springframework.stereotype.Component

@Component
class TestExtensionFeature : ExtensionFeature {
    override val id: String = "test"

    override val name: String = "Test extension"

    override val description: String = "Extensions for tests"

    override val version: String = "test"
}