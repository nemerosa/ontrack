package net.nemerosa.ontrack.service.message

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.model.message.GlobalMessage
import net.nemerosa.ontrack.model.message.GlobalMessageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GlobalMessageServiceImpl(
    private val extensionManager: ExtensionManager,
) : GlobalMessageService {

    override val globalMessages: List<GlobalMessage>
        get() = extensionManager.getExtensions(GlobalMessageExtension::class.java)
            .flatMap { extension ->
                extension.globalMessages.map { msg ->
                    GlobalMessage(
                        featureId = extension.feature.id,
                        type = msg.type,
                        content = msg.content,
                    )
                }
            }
            .sortedWith(compareBy(GlobalMessage::type))

}