package net.nemerosa.ontrack.extension.queue

import org.springframework.amqp.core.Declarable
import org.springframework.amqp.core.DirectExchange
import kotlin.reflect.KClass

interface QueueProcessor<T : Any> {

    /**
     * Unique ID
     */
    val id: String

    /**
     * Returns true if the processing must be done synchronously
     */
    val sync: Boolean get() = false

    /**
     * Gets a specific routing key for the payload.
     *
     * Returns null by default, to rely on the global settings
     *
     * @param payload The payload to route
     * @return A routing key or `null` if the default routing key must be used
     */
    fun getSpecificRoutingKey(payload: T): String? = null

    /**
     * When there is no [specific routing key][getSpecificRoutingKey], this function
     * is called to return the identifier of the payload used to spread the load
     * among the available queues.
     */
    fun getRoutingIdentifier(payload: T): String

    /**
     * Processes the payload.
     */
    fun process(payload: T)

    /**
     * Performs some specific configuration for the queues.
     *
     * @param exchange Exchange to be used
     * @param declarables List of AMQP declarables to complete.
     */
    fun specificConfiguration(
        exchange: DirectExchange,
        declarables: MutableList<Declarable>,
    ) {
    }

    /**
     * If the payload cannot be processed, cancels it.
     *
     * @param payload Payload to be processed
     * @return A non-null reason is the payload must be cancelled.
     */
    fun isCancelled(payload: T): String?

    /**
     * Type of the payload
     */
    val payloadType: KClass<T>

    /**
     * Ack mode
     */
    val ackMode: QueueAckMode get() = QueueAckMode.AUTO

}