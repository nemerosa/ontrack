package net.nemerosa.ontrack.extension.queue

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
@ConfigurationProperties(prefix = QueueConfigProperties.PREFIX)
class QueueConfigProperties {

    /**
     * General properties
     */
    var general = GeneralProperties()

    /**
     * Specific properties
     */
    val specific = mutableMapOf<String, SpecificProperties>()

    /**
     * Processing properties
     */
    abstract class ProcessingProperties {
        var async: Boolean = true
    }

    /**
     * General properties
     */
    class GeneralProperties : ProcessingProperties() {
        var warnIfAsync: Boolean = true
    }

    /**
     * Specific properties
     */
    class SpecificProperties : ProcessingProperties() {
        /**
         * Number of queues
         */
        var scale: Int = 1
    }

    /**
     * Gets thr routing key for a message.
     */
    fun <T : Any> getRoutingKey(
        queueProcessor: QueueProcessor<T>,
        payload: T
    ): String =
        queueProcessor.getSpecificRoutingKey(payload)
            ?: getGeneralRoutingKey(queueProcessor, payload)

    private fun <T : Any> getGeneralRoutingKey(
        queueProcessor: QueueProcessor<T>,
        payload: T
    ): String {
        val prefix = "ontrack.queue.${queueProcessor.id}.routing"
        val scale = specific[queueProcessor.id]?.scale ?: 1
        return if (scale > 1) {
            val identifier = queueProcessor.getRoutingIdentifier(payload)
            val code = abs(identifier.hashCode()) % scale
            "$prefix.$code"
        } else {
            "$prefix.0"
        }
    }

    companion object {
        const val PREFIX = "ontrack.extension.queue"
    }
}