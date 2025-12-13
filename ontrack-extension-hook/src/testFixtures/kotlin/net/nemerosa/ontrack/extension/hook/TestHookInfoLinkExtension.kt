package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class TestHookInfoLinkExtension(
        extension: TestExtensionFeature,
) : AbstractExtension(extension), HookInfoLinkExtension<String> {
    override val id: String = "test"
}