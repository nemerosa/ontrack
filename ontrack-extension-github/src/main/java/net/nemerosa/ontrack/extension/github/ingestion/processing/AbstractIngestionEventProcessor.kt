package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.json.parseInto
import kotlin.reflect.KClass

abstract class AbstractIngestionEventProcessor<T : Any> : IngestionEventProcessor {

    final override fun process(payload: IngestionHookPayload): IngestionEventProcessingResult {
        // Parsing of the payload
        val parsedPayload = parsePayload(payload)
        // Processes the payload
        return process(parsedPayload)
    }

    abstract fun process(payload: T): IngestionEventProcessingResult

    private fun parsePayload(payload: IngestionHookPayload): T = payload.payload.parseInto(payloadType)

    /**
     * Payload type
     */
    protected abstract val payloadType: KClass<T>

}