package net.nemerosa.ontrack.extension.queue

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseInto
import java.util.*
import kotlin.reflect.KClass

class QueuePayload private constructor(
    val id: String,
    val processor: String,
    val body: JsonNode,
) {
    fun <T : Any> parse(payloadType: KClass<out T>): T =
        body.parseInto(payloadType)

    companion object {
        fun <T : Any> create(processor: QueueProcessor<T>, body: T) =
            QueuePayload(
                id = UUID.randomUUID().toString(),
                processor = processor.id,
                body = body.asJson()
            )

        fun parse(body: JsonNode) = body.parse<QueuePayload>()
    }
}
