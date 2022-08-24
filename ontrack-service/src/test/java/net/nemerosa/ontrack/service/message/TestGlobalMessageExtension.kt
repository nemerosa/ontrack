package net.nemerosa.ontrack.service.message

import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.message.Message
import org.springframework.stereotype.Component

@Component
class TestGlobalMessageExtension(
    extensionFeature: TestExtensionFeature,
) : AbstractExtension(extensionFeature), GlobalMessageExtension {

    val messages = mutableListOf<Message>()

    override val globalMessages: List<Message> get() = messages

}