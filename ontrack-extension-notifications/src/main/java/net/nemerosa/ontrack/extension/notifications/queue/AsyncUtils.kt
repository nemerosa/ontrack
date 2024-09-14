package net.nemerosa.ontrack.extension.notifications.queue

import com.rabbitmq.client.Channel
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.amqp.core.Message
import java.io.IOException

fun ApplicationLogService.ackMessage(message: Message, channel: Channel?) {
    try {
        channel?.basicAck(message.messageProperties.deliveryTag, false)
    } catch (e: IOException) {
        // Log failing to acknowledge the message
        log(
            ApplicationLogEntry.error(
                e,
                NameDescription.nd(
                    "notifications-ack-error",
                    "Message could not be acked."
                ),
                "Message could not be acked: ${e.message}"
            )
        )
    }
}