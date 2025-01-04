package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
@ConfigurationProperties(prefix = QueueConfigProperties.PREFIX)
@APIName("Queues configuration")
@APIDescription("General configuration for the RabbitMQ queues.")
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
        @APIDescription("Emits a warning if the queues are not asynchronous (careful: the property name is a misnomer and will be renamed at one point into warnIfSync")
        @Deprecated("The property name is a misnomer and will be renamed at one point into warnIfSync")
        var warnIfAsync: Boolean = true
    }

    /**
     * Specific properties
     */
    class SpecificProperties : ProcessingProperties() {
        @APIDescription("Number of queues")
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
        val prefix = queueProcessor.queueRoutingPrefix
        val scale = getQueueProcessorScale(queueProcessor)
        return if (scale > 1) {
            val identifier = queueProcessor.getRoutingIdentifier(payload)
            val code = abs(identifier.hashCode()) % scale
            "$prefix.$code"
        } else {
            "$prefix.0"
        }
    }

    fun getQueueProcessorScale(queueProcessor: QueueProcessor<*>) =
        specific[queueProcessor.id]?.scale ?: queueProcessor.defaultScale ?: 1

    companion object {
        const val PREFIX = "ontrack.extension.queue"
    }
}