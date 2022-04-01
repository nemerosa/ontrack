package net.nemerosa.ontrack.extension.notifications.inmemory

import net.nemerosa.ontrack.model.Ack
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/extension/notifications/in-memory")
@ConditionalOnBean(InMemoryNotificationChannel::class)
class InMemoryNotificationChannelController(
    private val channel: InMemoryNotificationChannel,
) {

    /**
     * Clears all messages
     */
    @PostMapping("clear")
    fun clear(): Ack = channel.clear()

    /**
     * Gets the list of messages for a given group or empty.
     */
    @GetMapping("group/{group}")
    fun messages(@PathVariable group: String): List<String> =
        channel.getMessages(group)

}