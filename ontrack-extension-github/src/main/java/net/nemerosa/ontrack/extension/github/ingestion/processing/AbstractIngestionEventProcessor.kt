package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.json.parseInto
import kotlin.reflect.KClass

abstract class AbstractIngestionEventProcessor<T : Any> : IngestionEventProcessor {

    final override fun preProcessingCheck(payload: IngestionHookPayload): IngestionEventPreprocessingCheck {
        // Parsing of the payload
        val parsedPayload = parsePayload(payload)
        // Processes the payload
        return preProcessingCheck(parsedPayload)
    }

    final override fun process(payload: IngestionHookPayload): IngestionEventProcessingResult {
        // Parsing of the payload
        val parsedPayload = parsePayload(payload)
        // Processes the payload
        return process(parsedPayload, payload.configuration)
    }

    override fun getPayloadSource(payload: IngestionHookPayload): String? {
        // Parsing of the payload
        val parsedPayload = parsePayload(payload)
        // Using the typed payload
        return getPayloadSource(parsedPayload)
    }

    abstract fun getPayloadSource(payload: T): String?

    abstract fun preProcessingCheck(payload: T): IngestionEventPreprocessingCheck

    abstract fun process(payload: T, configuration: String?): IngestionEventProcessingResult

    private fun parsePayload(payload: IngestionHookPayload): T = payload.payload.parseInto(payloadType)

    /**
     * Payload type
     */
    protected abstract val payloadType: KClass<T>

}