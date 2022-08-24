package net.nemerosa.ontrack.service.message

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GlobalMessageServiceImpl(
    private val extensionManager: ExtensionManager,
) : GlobalMessageService {

    override val globalMessages: List<Message>
        get() = extensionManager.getExtensions(GlobalMessageExtension::class.java)
            .flatMap { it.globalMessages }
            .sortedWith(compareBy(Message::type))

}