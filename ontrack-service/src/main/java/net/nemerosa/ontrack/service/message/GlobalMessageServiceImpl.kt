package net.nemerosa.ontrack.service.message

import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class GlobalMessageServiceImpl(
    private val storageService: StorageService,
) : GlobalMessageService {

    override val globalMessages: List<Message>
        get() = storageService.getData(STORE, StoredMessage::class.java)
            .map { (id, stored) ->
                Message(
                    id = id,
                    datetime = stored.datetime,
                    content = stored.content,
                    type = stored.type,
                )
            }
            .sortedWith(compareBy(Message::type, Message::datetime))

    private data class StoredMessage(
        val datetime: LocalDateTime,
        val content: String,
        val type: MessageType,
    )

    companion object {
        private val STORE = GlobalMessageService::class.java.name
    }

}