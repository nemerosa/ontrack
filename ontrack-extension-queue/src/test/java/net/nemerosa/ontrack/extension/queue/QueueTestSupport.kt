package net.nemerosa.ontrack.extension.queue

import org.springframework.stereotype.Component

@Component
class QueueTestSupport(
    private val queueConfigProperties: QueueConfigProperties,
) {
    fun withSyncQueuing(code: () -> Unit) {
        val old = queueConfigProperties.general.async
        try {
            queueConfigProperties.general.async = false
            code()
        } finally {
            queueConfigProperties.general.async = old
        }
    }
}